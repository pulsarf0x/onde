# Origine du code et dépendances

Onde 0.1.0 adapte CenturyPlay, g8row, révision 17b7cf818119fc18e2d28d3dd0ad4a7f59a06065.
Sources amont : https://github.com/g8row/centuryplay — GNU AGPL v3, texte complet dans LICENSE.
Les modifications de cette version sont décrites dans README.md. Les notes et l’historique technique amont sont conservés dans UPSTREAM.md et docs/.

Le codec ALAC d’Apple est conservé dans app/src/main/cpp/alac/, avec les fichiers APPLE_LICENSE.txt et LICENSE-Apache-2.0.txt d’origine (Apache 2.0).

Les dépendances Android/Java sont téléchargées telles quelles par Gradle ; les versions exactes figurent dans app/build.gradle.kts : AndroidX, Material Components, Kotlin/coroutines, jmDNS, Bouncy Castle, dd-plist et Shizuku. Leurs licences et notices intégrées ne sont pas supprimées. JUnit est utilisé uniquement pour les tests.

Les marques HomePod, AirPlay, Samsung, Deezer, Spotify et YouTube Music appartiennent à leurs détenteurs respectifs. Aucune affiliation n’est revendiquée.
