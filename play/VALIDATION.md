# Validation — 0.2.0 preparation (27 October 2026)

Local toolchain: JDK 17, Gradle 8.11.1, AGP 8.10.1, Android SDK 36, NDK 27.2.12479018, CMake 3.22.1.

- Debug and release compilation succeeded; signed release APK and AAB were generated with a persistent local upload key.
- 16 debug unit tests and 17 release unit tests passed. The additional release test ensures privileged capture is not offered by the standard build.
- Debug and release lint completed with no errors/fatal issues. Non-blocking upstream/style/dependency warnings remain; local reports are retained under app/build/reports (not committed).
- APK signature and 16 KiB zip alignment verified. Native JNI entry points and 16 KiB ELF alignment checked for arm64-v8a, armeabi-v7a and x86_64. Bundletool 1.18.3 validated the AAB structure. Its official downloaded JAR SHA-256 was a099cfa1543f55593bc2ed16a70a7c67fe54b1747bb7301f37fdfd6d91028e29.
- The standard release manifest/bundle checks require API 36+, minSdk 30, backup restrictions, offline privacy policy and no Shizuku SDK/provider. Signing keys and passwords are private and excluded from Git.
- Signed release installed and launched on a dedicated Android 16 emulator. Main, settings, help, offline privacy and dark-theme screens were exercised; no app crash was found in the crash log. The emulator could discover LAN receivers, but no receiver playback was initiated. This is not physical-phone validation.
- Store screenshots are actual release UI, captured with emulator Wi-Fi disabled to avoid exposing private receiver names/addresses. They are not evidence of successful audio playback. Icon, feature graphic and screenshot dimensions/color formats were checked.
- FR/EN store text length limits checked. Privacy website and Console handoff drafts prepared. Price and Play Console settings have not been selected.

Still pending: Galaxy S23/HomePod mini audio playback, each source music app, screen-off behavior, reconnect/revocation/Stop with an active receiver, additional receivers/multiroom/stereo pairs, real FGS demonstration video, owner review of privacy/Data safety/publisher details, account verification, required Play testing and Google review. See HARDWARE_TESTS.md and PUBLISHING.md.

CI and privacy-site deployment must be checked against the resulting GitHub revision; a local build is not evidence that CI or Pages passed.
