# Onde — Android → AirPlay

[![Android build](https://github.com/pulsarf0x/onde/actions/workflows/build.yml/badge.svg)](https://github.com/pulsarf0x/onde/actions/workflows/build.yml)

Version 0.1.0 expérimentale, préparée le 23 octobre 2026.

Application gratuite, sans compte et sans limite de durée, pour transmettre le son des apps Android vers des récepteurs AirPlay. Cible de validation : Samsung Galaxy S23 et HomePod mini. **Le fonctionnement sur ce matériel reste à tester.**

## Installation

Compiler un APK avec les instructions ci-dessous, ou récupérer l’artefact `onde-debug` d’une exécution réussie de [GitHub Actions](https://github.com/pulsarf0x/onde/actions/workflows/build.yml) (connexion GitHub nécessaire). Ouvrir Onde, lire l’aide puis choisir l’enceinte. Le téléphone et le HomePod doivent être sur le même réseau local. L’iPhone/iPad qui gère le HomePod doit autoriser les personnes du même réseau à utiliser AirPlay.

Autoriser la capture audio Android, puis lancer Deezer, Spotify ou YouTube Music. Le bouton avec la note de musique ouvre les lecteurs installés. Android peut parler de partage d’écran : seule la capture audio de lecture est utilisée, sans microphone ni transmission d’image.

L’APK de cette première version est signé avec une clé de développement locale. Il n’est pas publié sur le Play Store. Il est destiné au test personnel. Conserver sa propre clé pour signer les mises à jour ; les clés ne sont jamais versionnées. Les compilations GitHub Actions utilisent une clé de debug éphémère et ne garantissent donc pas l’installation par-dessus une autre compilation.

## Fonctionnalités

- Détection mDNS des récepteurs AirPlay 1 et 2 ; choix du récepteur et appairage.
- Capture audio Android, diffusion en service de premier plan, arrêt depuis la notification.
- Volume, jauge audio, minuterie, reconnexion et options héritées du moteur CenturyPlay.
- Interface principale et réglages en français ; aide HomePod/Samsung et raccourcis musicaux.
- Diagnostic partageable depuis l’aide ; aucune transmission automatique. Le serveur HTTP de diagnostic amont n’est pas activé automatiquement, même en mode debug.
- Aucune publicité ni paiement ; aucune intégration de connexion aux comptes musicaux.

## Limites

La compatibilité HomePod mini, le comportement de One UI, la lecture écran verrouillé et les trois services musicaux doivent être validés sur le téléphone. Les tests logiciels ne remplacent pas ces vérifications. Les apps ou contenus qui refusent la capture peuvent produire du silence. Une latence d’environ deux secondes est attendue. Les réglages avancés Shizuku restent facultatifs. Le multiroom et les paires stéréo ne sont pas validés pour cette version.

## Compiler

JDK 17, Android SDK 35, Build Tools 35.0.0, NDK 27.2.12479018 et CMake 3.22.1.

```sh
export JAVA_HOME=/chemin/vers/jdk17
export ANDROID_HOME=/chemin/vers/android-sdk
./gradlew assembleDebug testDebugUnitTest lintDebug
```

L’APK est créé dans `app/build/outputs/apk/debug/app-debug.apk`. Les dépendances Gradle sont téléchargées depuis Google Maven et Maven Central. Pour signer les mises à jour de l’APK fourni, réutiliser sa clé de développement locale via la propriété Gradle `ondeDebugKeystore`. Une nouvelle compilation sur un autre poste utilise par défaut une autre clé : désinstaller l’ancien APK est alors nécessaire.

## Provenance et licence

Adaptation de [CenturyPlay par g8row](https://github.com/g8row/centuryplay), révision `17b7cf818119fc18e2d28d3dd0ad4a7f59a06065`, sous GNU AGPL v3 (voir LICENSE et UPSTREAM.md). Les sources complètes sont disponibles dans [ce dépôt](https://github.com/pulsarf0x/onde). Les notices des dépendances et du codec ALAC d’Apple (Apache 2.0) sont conservées. Les noms de paquet et l’identité de l’application sont propres à Onde.

Modifications : interface française, aide et diagnostic local, raccourcis musicaux, identité Onde, correctif des autorisations audio déjà accordées, absence de démarrage automatique du serveur de logs, sauvegarde Android désactivée pour ne pas exporter les secrets d’appairage, bibliothèques natives alignées sur 16 Kio et tests de sélection de transport.

Aucune affiliation avec Apple, Samsung ou les services musicaux.
