package fr.evanndv.jarvis;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import java.util.ArrayList;

/**
 * Reconnaissance vocale Android (français) pour parler à Jarvis.
 * Une écoute = une phrase. Événements JS : "state", "partial", "result", "error".
 * L'écoute permanente (« Jarvis… ») est gérée côté JS en relançant une écoute après chaque phrase.
 */
@CapacitorPlugin(
        name = "JarvisVoice",
        permissions = { @Permission(strings = { Manifest.permission.RECORD_AUDIO }, alias = "micro") }
)
public class JarvisVoicePlugin extends Plugin {

    private SpeechRecognizer recognizer;
    private final Handler main = new Handler(Looper.getMainLooper());

    // Le « bip » de début / fin d'écoute d'Android : coupé pendant l'écoute permanente.
    // On coupe les sons système le temps de l'écoute, et le son média seulement si aucune musique ne joue.
    private static final int[] BEEP_STREAMS = { AudioManager.STREAM_SYSTEM, AudioManager.STREAM_NOTIFICATION, AudioManager.STREAM_RING };
    private boolean muted = false, musicMuted = false;
    private int session = 0;

    private AudioManager audio() { return (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE); }

    private void muteBeep() {
        if (muted) return;
        AudioManager am = audio(); if (am == null) return;
        for (int st : BEEP_STREAMS) { try { am.adjustStreamVolume(st, AudioManager.ADJUST_MUTE, 0); } catch (Exception ignored) { } }
        musicMuted = !am.isMusicActive();
        if (musicMuted) { try { am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0); } catch (Exception e) { musicMuted = false; } }
        muted = true;
    }

    private void unmuteNow() {
        if (!muted) return;
        AudioManager am = audio(); if (am == null) return;
        for (int st : BEEP_STREAMS) { try { am.adjustStreamVolume(st, AudioManager.ADJUST_UNMUTE, 0); } catch (Exception ignored) { } }
        if (musicMuted) { try { am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0); } catch (Exception ignored) { } }
        muted = false; musicMuted = false;
    }

    // Rétablit le son un peu après la fin de l'écoute, sauf si une nouvelle écoute a démarré entre-temps (pas de bip entre deux écoutes)
    private void unmuteLater() {
        final int s = session;
        main.postDelayed(() -> { if (s == session) unmuteNow(); }, 700);
    }

    /** Rétablit le son tout de suite (Jarvis va parler). */
    @PluginMethod
    public void unmute(PluginCall call) {
        main.post(() -> { session++; if (recognizer != null) recognizer.cancel(); unmuteNow(); call.resolve(); }); // répond seulement une fois le son rétabli
    }

    @PluginMethod
    public void available(PluginCall call) {
        JSObject o = new JSObject();
        o.put("value", SpeechRecognizer.isRecognitionAvailable(getContext()));
        o.put("micro", getPermissionState("micro") == PermissionState.GRANTED);
        call.resolve(o);
    }

    @PluginMethod
    public void start(PluginCall call) {
        if (getPermissionState("micro") != PermissionState.GRANTED) {
            requestPermissionForAlias("micro", call, "microCallback");
            return;
        }
        begin(call);
    }

    @PermissionCallback
    private void microCallback(PluginCall call) {
        if (getPermissionState("micro") == PermissionState.GRANTED) begin(call);
        else call.reject("Autorisation du micro refusée");
    }

    private void emit(String event, String key, Object value) {
        JSObject o = new JSObject();
        if (key != null) o.put(key, value);
        notifyListeners(event, o);
    }

    private static String best(Bundle b) {
        if (b == null) return "";
        ArrayList<String> list = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        return list != null && !list.isEmpty() ? list.get(0) : "";
    }

    private void begin(PluginCall call) {
        final boolean offline = call.getBoolean("offline", false);
        final boolean quiet = call.getBoolean("quiet", false);
        main.post(() -> {
            try {
                session++;
                if (quiet) muteBeep();
                if (recognizer != null) recognizer.destroy();
                recognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
                recognizer.setRecognitionListener(new RecognitionListener() {
                    @Override public void onReadyForSpeech(Bundle params) { emit("state", "state", "listening"); }
                    @Override public void onBeginningOfSpeech() { emit("state", "state", "speech"); }
                    @Override public void onRmsChanged(float rmsdB) { }
                    @Override public void onBufferReceived(byte[] buffer) { }
                    @Override public void onEndOfSpeech() { emit("state", "state", "processing"); }
                    @Override public void onError(int error) { if (quiet) unmuteLater(); emit("error", "code", error); }
                    @Override public void onResults(Bundle results) { if (quiet) unmuteLater(); emit("result", "text", best(results)); }
                    @Override public void onPartialResults(Bundle partialResults) { emit("partial", "text", best(partialResults)); }
                    @Override public void onEvent(int eventType, Bundle params) { }
                });
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, offline);
                // laisse finir la phrase : on attend 1,5 s de silence avant de couper
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1200L);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getContext().getPackageName());
                recognizer.startListening(intent);
                call.resolve();
            } catch (Exception e) {
                unmuteNow();
                call.reject("Reconnaissance vocale indisponible : " + e.getMessage());
            }
        });
    }

    @PluginMethod
    public void stop(PluginCall call) {
        main.post(() -> { session++; if (recognizer != null) recognizer.cancel(); unmuteNow(); call.resolve(); });
    }

    @Override
    protected void handleOnPause() { // appli en arrière-plan : jamais de son laissé coupé
        main.post(() -> { session++; unmuteNow(); });
        super.handleOnPause();
    }

    @Override
    protected void handleOnDestroy() {
        main.post(() -> { session++; unmuteNow(); if (recognizer != null) { recognizer.destroy(); recognizer = null; } });
        super.handleOnDestroy();
    }
}
