package fr.evanndv.jarvis;

import android.app.Notification;
import android.app.RemoteInput;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Messages reçus (WhatsApp, SMS, Messenger, Telegram, Signal…) lus à voix haute par Jarvis,
 * et réponse dictée via le bouton « Répondre » de la notification.
 * Désactivé par défaut : rien n'est transmis tant que configure({enabled:true}) n'a pas été appelé.
 */
@CapacitorPlugin(name = "JarvisNotif")
public class JarvisNotifPlugin extends Plugin {

    private volatile boolean enabled = false;
    private Set<String> apps = new HashSet<>();
    private final Map<String, StatusBarNotification> recent = new LinkedHashMap<String, StatusBarNotification>() {
        @Override protected boolean removeEldestEntry(Map.Entry<String, StatusBarNotification> e) { return size() > 30; }
    };
    private final Map<String, String> lastText = new LinkedHashMap<String, String>() {
        @Override protected boolean removeEldestEntry(Map.Entry<String, String> e) { return size() > 60; }
    };

    @Override
    public void load() {
        JarvisNotificationListener.sink = this::onPosted;
    }

    private boolean granted() {
        String flat = Settings.Secure.getString(getContext().getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(getContext().getPackageName());
    }

    @PluginMethod
    public void configure(PluginCall call) {
        enabled = Boolean.TRUE.equals(call.getBoolean("enabled", false));
        JSArray list = call.getArray("apps");
        Set<String> s = new HashSet<>();
        if (list != null) {
            try { for (Object o : list.toList()) s.add(String.valueOf(o)); } catch (Exception ignored) { }
        }
        apps = s;
        JSObject o = new JSObject();
        o.put("granted", granted());
        call.resolve(o);
    }

    private String label(String pkg) {
        try {
            PackageManager pm = getContext().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
            return pm.getApplicationLabel(ai).toString();
        } catch (Exception e) {
            return pkg;
        }
    }

    private static Notification.Action replyAction(Notification n) {
        if (n.actions == null) return null;
        Notification.Action fallback = null;
        for (Notification.Action a : n.actions) {
            RemoteInput[] ri = a.getRemoteInputs();
            if (ri == null || ri.length == 0) continue;
            if (Build.VERSION.SDK_INT >= 28 && a.getSemanticAction() == Notification.Action.SEMANTIC_ACTION_REPLY) return a;
            if (fallback == null) fallback = a;
        }
        return fallback;
    }

    private void onPosted(StatusBarNotification sbn) {
        if (!enabled) return;
        String pkg = sbn.getPackageName();
        if (pkg.equals(getContext().getPackageName())) return;
        if (!apps.isEmpty() && !apps.contains(pkg)) return;
        Notification n = sbn.getNotification();
        if ((n.flags & Notification.FLAG_GROUP_SUMMARY) != 0 || sbn.isOngoing()) return;
        Bundle ex = n.extras;
        CharSequence title = ex.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence text = ex.getCharSequence(Notification.EXTRA_TEXT);
        Parcelable[] msgs = ex.getParcelableArray(Notification.EXTRA_MESSAGES);
        if (msgs != null && msgs.length > 0 && msgs[msgs.length - 1] instanceof Bundle) {
            CharSequence t = ((Bundle) msgs[msgs.length - 1]).getCharSequence("text");
            if (t != null) text = t;
        }
        if (text == null || text.length() == 0) return;
        String key = sbn.getKey(), body = text.toString();
        if (body.equals(lastText.get(key))) return; // même message renvoyé (mise à jour de la notification)
        lastText.put(key, body);
        recent.put(key, sbn);
        JSObject o = new JSObject();
        o.put("key", key);
        o.put("package", pkg);
        o.put("app", label(pkg));
        o.put("from", title != null ? title.toString() : "");
        o.put("text", body);
        o.put("canReply", replyAction(n) != null);
        o.put("time", sbn.getPostTime());
        notifyListeners("message", o);
    }

    @PluginMethod
    public void reply(PluginCall call) {
        String key = call.getString("key"), text = call.getString("text");
        StatusBarNotification sbn = key != null ? recent.get(key) : null;
        if (sbn == null || text == null) { call.reject("Message introuvable"); return; }
        Notification.Action a = replyAction(sbn.getNotification());
        if (a == null) { call.reject("Cette appli ne permet pas de répondre depuis la notification"); return; }
        try {
            Intent intent = new Intent();
            Bundle results = new Bundle();
            for (RemoteInput ri : a.getRemoteInputs()) results.putCharSequence(ri.getResultKey(), text);
            RemoteInput.addResultsToIntent(a.getRemoteInputs(), intent, results);
            a.actionIntent.send(getContext(), 0, intent);
            call.resolve();
        } catch (Exception e) {
            call.reject("Envoi impossible : " + e.getMessage());
        }
    }
}
