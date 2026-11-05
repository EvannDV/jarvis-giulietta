package fr.evanndv.jarvis;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Pont entre Jarvis et la musique du téléphone / de la tablette.
 * Lit le titre en cours (titre, artiste, pochette, position) de l'appli qui joue
 * et permet lecture / pause / suivant / précédent.
 * Événement JS : "media" à chaque changement.
 */
@CapacitorPlugin(name = "JarvisMedia")
public class JarvisMediaPlugin extends Plugin implements MediaSessionManager.OnActiveSessionsChangedListener {

    private MediaSessionManager sessionManager;
    private ComponentName listenerComponent;
    private MediaController controller;
    private boolean attached = false;
    private String lastArtKey = null;
    private String lastArt = null;
    private final Handler main = new Handler(Looper.getMainLooper());

    private final MediaController.Callback callback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) { emit(); }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) { emit(); }

        @Override
        public void onSessionDestroyed() {
            controller = null;
            refreshSessions();
        }
    };

    @Override
    public void load() {
        sessionManager = (MediaSessionManager) getContext().getSystemService(Context.MEDIA_SESSION_SERVICE);
        listenerComponent = new ComponentName(getContext(), JarvisNotificationListener.class);
        attach();
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        attach(); // l'utilisateur revient peut-être des réglages après avoir donné l'accès
    }

    private boolean granted() {
        String flat = Settings.Secure.getString(getContext().getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(getContext().getPackageName());
    }

    private void attach() {
        if (attached || !granted() || sessionManager == null) return;
        try {
            sessionManager.addOnActiveSessionsChangedListener(this, listenerComponent, main);
            attached = true;
            refreshSessions();
        } catch (SecurityException ignored) {
            attached = false;
        }
    }

    private void refreshSessions() {
        if (!granted()) return;
        try {
            onActiveSessionsChanged(sessionManager.getActiveSessions(listenerComponent));
        } catch (SecurityException ignored) {
        }
    }

    @Override
    public void onActiveSessionsChanged(List<MediaController> controllers) {
        MediaController best = null;
        if (controllers != null) {
            for (MediaController c : controllers) {
                PlaybackState st = c.getPlaybackState();
                if (st != null && st.getState() == PlaybackState.STATE_PLAYING) { best = c; break; }
                if (best == null) best = c;
            }
        }
        if (controller != null && best != null && controller.getSessionToken().equals(best.getSessionToken())) {
            emit();
            return;
        }
        if (controller != null) controller.unregisterCallback(callback);
        controller = best;
        if (controller != null) controller.registerCallback(callback, main);
        emit();
    }

    private JSObject snapshot() {
        JSObject o = new JSObject();
        o.put("granted", granted());
        if (controller == null) {
            o.put("active", false);
            return o;
        }
        o.put("active", true);
        String pkg = controller.getPackageName();
        o.put("package", pkg);
        o.put("app", appLabel(pkg));
        MediaMetadata md = controller.getMetadata();
        if (md != null) {
            o.put("title", md.getString(MediaMetadata.METADATA_KEY_TITLE));
            String artist = md.getString(MediaMetadata.METADATA_KEY_ARTIST);
            if (artist == null) artist = md.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
            o.put("artist", artist);
            o.put("album", md.getString(MediaMetadata.METADATA_KEY_ALBUM));
            o.put("duration", md.getLong(MediaMetadata.METADATA_KEY_DURATION));
            String key = pkg + "|" + md.getString(MediaMetadata.METADATA_KEY_TITLE) + "|" + md.getString(MediaMetadata.METADATA_KEY_ALBUM);
            if (!key.equals(lastArtKey)) {
                lastArtKey = key;
                Bitmap art = md.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
                if (art == null) art = md.getBitmap(MediaMetadata.METADATA_KEY_ART);
                lastArt = art != null ? toDataUrl(art) : null;
            }
            o.put("art", lastArt);
        }
        PlaybackState st = controller.getPlaybackState();
        if (st != null) {
            o.put("playing", st.getState() == PlaybackState.STATE_PLAYING);
            long pos = st.getPosition();
            if (st.getState() == PlaybackState.STATE_PLAYING && st.getLastPositionUpdateTime() > 0) {
                pos += (long) ((android.os.SystemClock.elapsedRealtime() - st.getLastPositionUpdateTime()) * st.getPlaybackSpeed());
            }
            o.put("position", Math.max(0, pos));
        } else {
            o.put("playing", false);
            o.put("position", 0);
        }
        return o;
    }

    private void emit() {
        notifyListeners("media", snapshot(), true);
    }

    private String appLabel(String pkg) {
        try {
            PackageManager pm = getContext().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
            return pm.getApplicationLabel(ai).toString();
        } catch (Exception e) {
            return pkg;
        }
    }

    private static String toDataUrl(Bitmap src) {
        try {
            int max = 320;
            float k = Math.min(1f, (float) max / Math.max(src.getWidth(), src.getHeight()));
            Bitmap b = k < 1f ? Bitmap.createScaledBitmap(src, Math.round(src.getWidth() * k), Math.round(src.getHeight() * k), true) : src;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 82, out);
            return "data:image/jpeg;base64," + Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    @PluginMethod
    public void getState(PluginCall call) {
        attach();
        call.resolve(snapshot());
    }

    @PluginMethod
    public void openSettings(PluginCall call) {
        Intent i = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
        call.resolve();
    }

    @PluginMethod
    public void control(PluginCall call) {
        String action = call.getString("action", "toggle");
        if (controller == null) { call.reject("Aucun lecteur actif"); return; }
        MediaController.TransportControls t = controller.getTransportControls();
        PlaybackState st = controller.getPlaybackState();
        boolean playing = st != null && st.getState() == PlaybackState.STATE_PLAYING;
        switch (action) {
            case "play": t.play(); break;
            case "pause": t.pause(); break;
            case "next": t.skipToNext(); break;
            case "previous": t.skipToPrevious(); break;
            default: if (playing) t.pause(); else t.play();
        }
        call.resolve();
    }
}
