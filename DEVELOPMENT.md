# Onde development

## Toolchain

Use JDK 17, Android SDK platform 36, Build Tools 35.0.0 and 36.0.0, NDK 27.2.12479018 and CMake 3.22.1. Gradle 8.11.1 and Android Gradle Plugin 8.10.1 are pinned in the project. Keep machine-specific paths in the environment or ignored local.properties, never in Git.

```sh
export JAVA_HOME=/path/to/jdk17
export ANDROID_HOME=/path/to/android-sdk
sdkmanager "platforms;android-36" "build-tools;35.0.0" "build-tools;36.0.0" "ndk;27.2.12479018" "cmake;3.22.1" "platform-tools"
./gradlew assembleDebug assembleRelease bundleRelease testDebugUnitTest testReleaseUnitTest lintDebug lintRelease
```

## Debug versus standard release

`src/main` contains shared playback capture and AirPlay code. `src/debug` contains optional Shizuku integration, its provider and privileged capture implementation; Shizuku dependencies are debug-only. `src/release` contains a non-privileged stub so the release never grants itself permissions or uses the Shizuku SDK. The standard release needs Android's capture consent and cannot override source-app restrictions. Release builds also refuse to start the inherited HTTP diagnostic server. Share diagnostics explicitly through Help instead.

The app package is `app.onde.audio`. Debug builds use a debug signing key; `-PondeDebugKeystore=/absolute/private/path` can reuse an existing debug key. Different signing keys cannot update each other in place. Do not automatically uninstall a user's app to resolve a signing mismatch; uninstalling clears local settings.

## Signed Play candidate

```sh
python3 tools/create_upload_key.py
source .signing/signing.env
./gradlew assembleRelease bundleRelease
python3 tools/verify_native_apk.py app/build/outputs/apk/release/app-release.apk
python3 tools/verify_release.py app/build/outputs/bundle/release/app-release.aab
```

`.signing/` contains sensitive, persistent key material and is ignored by Git. Back it up privately. Never attach it to an issue or PR. CI has no upload key and emits an unsigned AAB; this is intentional. See `play/PUBLISHING.md` for bundletool, signature checks and Console handoff.

## Android 16 UI smoke test and screenshots

Use a dedicated emulator, not the owner's physical phone. Install an Android 36 ARM64 image on Apple Silicon (x86_64 on an x86 CI host), then boot it. Set ANDROID_HOME and run:

```sh
python3 tools/smoke_release.py --serial emulator-5580 --offline-screenshots
```

The script refuses non-emulator serials. It installs the signed release, opens main/settings/help/privacy, checks for crashes and captures the actual UI. Offline screenshots avoid leaking real LAN receiver names and addresses. Results stay under ignored `work/emulator-smoke/`. These checks do not validate audio capture, network transport or any physical HomePod. Never use upstream screenshots as screenshots of Onde.

`tools/render_store_assets.swift` exports the existing mark to a 512px RGBA icon and a 1024×500 RGB feature graphic on macOS. `tools/export_store_screenshots.swift` makes lossless opaque RGB exports of real emulator screenshots. No synthetic receiver/playback states are used. Asset dimensions and sources are recorded in `play/graphics/manifest.json`.

## Privacy and website

`app/src/main/assets/privacy-fr.txt` is the canonical policy, displayed offline in PrivacyActivity. Run `python3 tools/sync_privacy_site.py` after editing it. `site/` is deployed through the Privacy website GitHub Actions workflow when main changes. No trackers, external scripts or forms are included. Review all privacy/Data safety statements against implementation before submitting an update.

## Protocol and hardware checks

See AGENTS.md, docs/RESEARCH_AND_ROADMAP.md and the protocol notes. AirPlay 1 RSA and AirPlay 2 HAP/transient selection are covered by unit tests, not by a universal receiver guarantee. FairPlay-only paths are unsupported. Use file-writing software receivers for experiments, and never play audible test tones on real speakers at night. Galaxy S23/HomePod mini tests remain a separate gate in play/HARDWARE_TESTS.md.
