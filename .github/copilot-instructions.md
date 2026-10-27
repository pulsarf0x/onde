# Onde development notes

Read ../AGENTS.md and ../README.md for the current project structure, build requirements and validation limits.

Source namespace: app.onde.audio; Kotlin sources are under app/src/main/java/app/onde/audio/.
Use JDK 17, SDK 36, NDK 27.2.12479018 and CMake 3.22.1.
Run ./gradlew assembleDebug assembleRelease bundleRelease testDebugUnitTest testReleaseUnitTest lintDebug lintRelease and python3 tools/verify_native_apk.py app/build/outputs/apk/debug/app-debug.apk.

Preserve the AGPL v3 license and upstream notices. No signing keys, local credentials or machine-specific SDK configuration may be committed. HomePod mini and music-app compatibility remain unverified until tested on physical hardware. Do not describe unit tests or build success as end-to-end playback validation. Diagnostics are shared explicitly by the user; do not automatically start the inherited LAN log server.
