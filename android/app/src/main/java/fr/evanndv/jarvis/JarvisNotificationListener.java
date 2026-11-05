package fr.evanndv.jarvis;

import android.service.notification.NotificationListenerService;

/**
 * Service vide : Android exige un « écouteur de notifications » autorisé par l'utilisateur
 * pour qu'une appli puisse voir et piloter les lecteurs de musique des autres applis
 * (Spotify, YouTube Music, Deezer…). Jarvis ne lit aucune notification.
 */
public class JarvisNotificationListener extends NotificationListenerService {
}
