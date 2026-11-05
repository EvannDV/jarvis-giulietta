# Jarvis · Giulietta

Tableau de bord façon Jarvis pour Alfa Romeo Giulietta 2.0 JTDM, sur tablette Android, avec un adaptateur OBDLink CX.

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
- Carte affichée avec Leaflet (inclus dans `lib/`)

Sans réseau, le tableau de bord marche normalement. La carte affiche les zones déjà vues, mais la recherche d'itinéraire a besoin d'Internet.

## Pour modifier le projet

- `src/index.html` : le code de l'interface (une seule page).
- `python3 src/build.py` : génère `index.html` et `sw.js` à la racine, servis par GitHub Pages.
