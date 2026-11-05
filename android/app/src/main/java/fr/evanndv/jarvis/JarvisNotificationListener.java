package fr.evanndv.jarvis;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * « Accès aux notifications » autorisé par l'utilisateur dans Android.
 * Sert à deux choses :
 *  - voir et piloter les lecteurs de musique des autres applis (JarvisMediaPlugin) ;
 *  - lire les messages à voix haute, SEULEMENT si l'option est activée dans les réglages de Jarvis
 *    (JarvisNotifPlugin filtre et ne transmet rien quand l'option est coupée).
 */
public class JarvisNotificationListener extends NotificationListenerService {

    public interface Sink { void onPosted(StatusBarNotification sbn); }

    static volatile Sink sink;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Sink s = sink;
        if (s != null) s.onPosted(sbn);
    }
}
