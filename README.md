# Jarvis · Giulietta

Tableau de bord façon Jarvis pour Alfa Romeo Giulietta 2.0 JTDM, sur tablette Android, avec un adaptateur OBDLink CX.

Deux façons de l'utiliser :
- **L'appli Android (recommandé)** : connexion OBD automatique, vraie musique, écran toujours allumé.
- **La version web** dans Chrome (GitHub Pages) : rien à installer, mais connexion OBD à la main à chaque démarrage.

## Appli Android

### Installer
1. Sur la tablette, ouvrez https://github.com/EvannDV/jarvis-giulietta/releases/latest et touchez **Jarvis.apk**.
2. Ouvrez le fichier téléchargé. Android demande d'autoriser l'installation depuis Chrome (ou « Fichiers ») : acceptez, puis **Installer**.
3. Pour une mise à jour, refaites la même chose : la nouvelle version s'installe par-dessus, vos réglages restent.

### Premier lancement
- **Position** : acceptez (GPS et navigation).
- **Appareils à proximité** : acceptez (Bluetooth OBD).
- **OBD** : contact mis, touchez **Connecter OBD** et choisissez l'OBDLink CX. C'est la seule fois : ensuite Jarvis s'y reconnecte tout seul dès que vous mettez le contact.
- **Musique** : touchez le titre dans le lecteur (« Accès à la musique »). Android ouvre la page « Accès aux notifications » : activez **Jarvis**. Jarvis affiche alors le titre, l'artiste et la pochette de Spotify, YouTube Music, Deezer… et les boutons pilotent le lecteur. Jarvis ne lit aucune notification.
- **Cerveau Gemini** : touchez l'engrenage en haut à droite, collez votre clé API Gemini (https://aistudio.google.com/apikey), puis **Tester** et **Enregistrer**. La clé reste sur la tablette, elle n'est jamais dans ce dépôt. Jarvis comprend alors n'importe quelle question et agit : itinéraire, musique, mode DNA, pages, météo, actualités, diagnostic, entretien. Sans clé, il garde ses réponses intégrées.
- **Plan gratuit Gemini** : Flash-Lite = 500 requêtes/jour, Flash = 20/jour. Une question coûte 1 à 3 requêtes ; les commandes simples (« mode sport », « musique suivante », « montre la carte », « nuit ») et les boutons État, Diag, Entretien, Actus n'en coûtent aucune. Le compteur du jour est dans les réglages ; quota épuisé = réponses intégrées jusqu'à la remise à zéro (9 h en France).
- **Briefing au démarrage** : météo de votre position (Open-Meteo), pluie à venir, rappels d'entretien, état de la voiture, rédigé par Gemini et lu à voix haute si la voix est activée. Se désactive dans les réglages.
- **Voix** : bouton Voix dans la console. Elle utilise la synthèse vocale d'Android (voix française à installer dans Paramètres → Synthèse vocale si besoin).

L'APK est fabriqué automatiquement par GitHub Actions à chaque modification (onglet **Actions** du dépôt).

## Version web

## Mettre en ligne sur GitHub Pages (une seule fois)

Ce dépôt contient déjà tous les fichiers : il suffit de faire l'étape 3. L'appli sera sur `https://evanndv.github.io/jarvis-giulietta/`.


1. Créez un compte sur https://github.com, puis un **nouveau dépôt public** appelé `jarvis`.
2. Dans le dépôt, cliquez sur **Add file → Upload files** et glissez **tout le contenu** de ce dossier (`index.html`, `sw.js`, `manifest.webmanifest`, `.nojekyll`, et les dossiers `lib/` et `icons/`). Validez avec **Commit changes**.
3. Allez dans **Settings → Pages**. Sous *Build and deployment*, choisissez **Deploy from a branch**, branche `main`, dossier `/ (root)`, puis **Save**.
4. Après 1 à 2 minutes, l'appli est en ligne sur `https://VOTRE-PSEUDO.github.io/jarvis/`.

## Installer sur la tablette

1. Ouvrez l'adresse dans **Chrome**.
2. Menu **⋮ → Installer l'application** (ou **Ajouter à l'écran d'accueil**). Jarvis s'ouvre ensuite en plein écran, comme une appli.
3. Autorisez la **position** quand Chrome la demande (pour le GPS), et les **appareils à proximité** (pour le Bluetooth).

Réglages Android conseillés :
- Paramètres → Batterie : retirez Chrome de l'optimisation de batterie.
- Options pour les développeurs → **Rester activé** (l'écran ne s'éteint pas pendant la charge).

## Utilisation

- **Connecter l'OBD** : contact mis, touchez le badge **Connecter OBD** en haut à gauche, puis choisissez l'OBDLink CX. À refaire à chaque démarrage pour l'instant ; l'appli Android (APK) fera la connexion automatique.
- **Coupure OBD** : toutes les valeurs passent à NONE et Jarvis retente la connexion toutes les 4 secondes.
- **Navigation** : tapez ou dites « aller à Place Bellecour », « itinéraire vers Lyon », « emmène-moi au Carrefour de Vénissieux ». « Annule l'itinéraire » pour arrêter.
- **Mode démo** : tapez « mode démo » pour voir l'interface avec des données simulées (sans OBD).

## Mettre à jour

Re-téléversez les fichiers modifiés dans le dépôt GitHub. La tablette récupère la nouvelle version au prochain lancement avec du réseau.

## Services utilisés

Tous gratuits pour un usage personnel :
- Carte : tuiles © contributeurs OpenStreetMap
- Recherche d'adresses : Nominatim (OpenStreetMap)
- Itinéraires : serveur public OSRM
- Météo : Open-Meteo (sans clé)
- Actualités : flux RSS du Monde (dans l'appli Android)
- Intelligence : API Gemini de Google (votre clé)
- Carte affichée avec Leaflet (inclus dans `lib/`)

Sans réseau, le tableau de bord marche normalement. La carte affiche les zones déjà vues, mais la recherche d'itinéraire a besoin d'Internet.

## Pour modifier le projet

- `src/index.html` : le code de l'interface (une seule page), partagé par la version web et l'appli.
- `python3 src/build.py` : génère la version web (`index.html`, `sw.js` à la racine) et `www/` pour l'appli.
- `android/` : projet Android (Capacitor 8). Code natif dans `android/app/src/main/java/fr/evanndv/jarvis/` :
  `MainActivity` (plein écran, écran allumé) et `JarvisMediaPlugin` (musique des autres applis).
- `.github/workflows/android.yml` : compile l'APK et le publie dans les releases.
- La clé de signature de l'APK n'est pas dans le dépôt : elle est stockée dans les secrets GitHub `ANDROID_KEYSTORE_BASE64` et `ANDROID_KEYSTORE_PASSWORD` (Settings → Secrets and variables → Actions).
