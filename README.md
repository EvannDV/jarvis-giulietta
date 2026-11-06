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
- **Photos** : « affiche-moi des photos des manifs en France » (photos d'actualité du Monde et de franceinfo), « montre-moi des photos de la Giulietta » (Wikimedia Commons). Galerie plein écran : « zoome sur la deuxième », « zoome encore », « suivante », « affiche-les toutes », « ferme » (sans IA, gratuit), et « qu'est-ce qu'on voit sur la photo 3 ? » (Gemini regarde l'image). Au doigt : toucher pour ouvrir, double-tap pour zoomer, glisser pour déplacer ou changer de photo.
- **Mode Sport (DNA D)** : combiné course (rapport géant avec effet d'impact, rampe LED vert → jaune → rouge, chrono 0-100 en direct), sol en perspective et hyperespace qui suivent la vitesse, lignes de vitesse sur les bords, secousse et flash à chaque passage de rapport, vibration en zone rouge, étincelles et « BOOST » au-dessus de 1,2 bar, coup de gaz synthétique à l'activation (désactivable dans les réglages).
- **Carte 3D** (tous les modes DNA) : vraie ville en relief (bâtiments OpenStreetMap via OpenFreeMap, gratuit et sans clé, moteur MapLibre), caméra derrière la voiture dans le sens de la marche, itinéraire en ruban lumineux visible à travers les immeubles, flèche géante au-dessus du prochain virage, faisceau sur l'arrivée, couleurs du mode D/N/A. Option « Carte en 3D » dans les réglages pour repasser en 2D si la tablette rame.
- **Mode Normal (DNA N) « Grand Tourisme »** : noir profond et champagne, deux grands cadrans à aiguille (compte-tours avec rapport engagé, vitesse avec la limitation), carte et prochaine manœuvre au centre, conso, autonomie, huile et eau en dessous.
- **Mode Pluie (DNA A)** : panneaux en verre mouillé, essuie-glaces qui balaient les gouttes (cadence selon l'intensité), limite abaissée par temps de pluie avec la limite « sur sec » barrée, distance de sécurité doublée (× 2,5 sous forte pluie), radar pluie sur 3 h par quart d'heure (Open-Meteo, gratuit), visibilité et risque de verglas, éclairs en cas d'orage et alerte vocale d'aquaplaning au-dessus de 90 km/h sous pluie intense.
- **Banc de puissance virtuel** (Perf → Banc, ou « lance le banc ») : tirage en 3e de 1 500 à 4 500 tr/min sur du plat, Jarvis trace les courbes de puissance et de couple et les compare aux 140 ch d'origine. Estimation à ±10 % (poids, aérodynamique, pertes de transmission).
- **Copilote pauses** : après 2 h de route (1 h la nuit), Jarvis cherche une aire ou un café sur le trajet et propose de l'ajouter comme étape ; « je suis fatigué » déclenche la proposition tout de suite. Temps de conduite affiché en haut (☕) à partir d'1 h 30. Désactivable dans les réglages. Aucune requête Gemini.
- **Actualité en direct** : « les dernières infos », « fil info » ou « quoi de neuf » affiche le fil des dépêches (franceinfo, Le Monde en continu, Google Actualités) du plus récent au plus ancien, avec l'heure, sans requête Gemini. Avec Gemini, Jarvis va chercher les articles des dernières 24 h sur n'importe quel sujet (« où en est le match du PSG ? »). Option « Flash info » dans les réglages : annonce vocale des alertes et directs importants.
- **Mises à jour depuis Jarvis** (appli Android) : Jarvis vérifie au démarrage puis toutes les 6 h si un APK plus récent est publié ici et le signale. « Installe la mise à jour » (ou Réglages → Installer) le télécharge et ouvre l'installateur Android ; la première fois, Android demande d'autoriser Jarvis à installer des applis. Rien n'est installé sans votre confirmation.
- **Station la moins chère** : « station la moins chère », « gazole pas cher sur la route », « SP98 le moins cher ». Prix officiels du jour (data.economie.gouv.fr), sur le trajet (à moins de 3,5 km de la route) ou autour, avec l'économie estimée sur un plein. « Ajoute la 1 comme étape » pour y aller.
- **G-mètre** (page Perf) : accéléromètre de la tablette, recalibré automatiquement à chaque arrêt ; accélération, freinage et virages en g, maximums mémorisés. Tablette face au conducteur (ou posée à plat).
- **Mécano de bord** : valeurs normales du 2.0 JTDM (affichées sous chaque capteur, page Moteur) et verdict calculé par l'appli ; mémoire des 15 dernières minutes (min/max/moyenne, pics de turbo en pleine charge par régime, tension au démarrage, montée en température) ; lecture des vrais codes défaut OBD (mémorisés et en attente, voyant moteur), jamais d'effacement ; surveillance automatique sans IA (surchauffe, huile, alternateur, surtension, batterie faible, turbo faible ou en surpression, FAP saturé). « Jarvis, explique-moi » après une alerte.
- **Parler à Jarvis** : bouton micro à gauche de la saisie, ou toucher le cercle de Jarvis. Option « Écoute permanente » (réglages, appli Android) : dites « Jarvis, … » ; les conversations sans « Jarvis » sont ignorées. Après une question de Jarvis (« je l'envoie ? »), répondez directement « oui ». Une question posée à la voix reçoit une réponse à voix haute.
- **Limitation de vitesse** : panneau sur le combiné et la carte (OpenStreetMap), réduit automatiquement sous la pluie (mode A ou météo), alerte au-delà de la limite + 5 %. Option d'alerte vocale dans les réglages.
- **Lieux sur le trajet, à la demande** : « trouve-moi une station / un McDo / un parking près de l'arrivée ». Jarvis affiche une liste numérotée et ne modifie l'itinéraire que si vous le demandez (« ajoute la 2 comme étape », « emmène-moi au 1 »).
- **Météo sur le trajet** : « il va pleuvoir sur la route ? » (prévisions à l'heure de passage, départ → arrivée).
- **Autonomie** : niveau de carburant OBD (si la voiture le transmet), autonomie selon la conso réelle ; alertes à 80 et 40 km et si l'autonomie ne suffit pas pour la destination. Capacité du réservoir dans Garage → Modifier.
- **Messages** (option, désactivée par défaut) : WhatsApp, SMS, Messenger, Telegram, Signal lus à voix haute. « Réponds-lui : j'arrive vers 19 h », Jarvis relit, « oui » pour envoyer.
- **Itinéraire** : « est-ce que je passe par l'autoroute ? » (km d'autoroute, péages, grands axes), « évite les péages », « sans autoroute », « prends le plus rapide / le plus court », « passe par Vienne », « montre-moi les autres itinéraires » (comparaison à l'écran, alternatives en pointillés sur la carte). Jarvis annonce le temps gagné ou perdu. Pas de trafic en temps réel avec le service gratuit OSRM.
- **Savoir** : culture générale directement, Wikipédia pour les faits précis (avec image), Google Actualités pour l'actualité d'un sujet.
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
- Actualités et photos d'actualité : flux RSS du Monde et de franceinfo, Google Actualités (dans l'appli Android)
- Photos libres : Wikimedia Commons ; résumés : Wikipédia
- Prix des carburants : data.economie.gouv.fr (flux officiel, sans clé)
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
