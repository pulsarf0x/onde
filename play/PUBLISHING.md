# Onde — Play Store handoff

Status: preparation for internal testing, not a production approval. No Play Console app has been created and no price has been selected.

## Human decisions and access still required

- Play Console account, verified developer identity, access to an Android device where Google requires it. Account creation/payment cannot be completed without the owner.
- Choose paid upfront versus free before offering the app free on Play: Google does not allow an app already offered free to become a paid download with the same package. An upfront price such as EUR 2.99 is only a proposal, not configured. Do not publish a free production/open listing while this decision is pending. Confirm treatment of closed/internal tracks in the account before selecting pricing there.
- Publisher/contact details and the accuracy of the privacy policy must be confirmed. Draft listing category: Music & Audio. Contact: pascal@plumia.net. Website: https://pulsarf0x.github.io/onde/ . Policy: https://pulsarf0x.github.io/onde/privacy.html . Source: https://github.com/pulsarf0x/onde . Default language: French.
- Real Galaxy S23 + HomePod mini playback, all three music players, reconnect, stop/revocation, lock-screen behavior, stability and volume safety. Record exact Android, One UI, HomePod and player versions. Never claim this has passed based on an emulator or CI.
- Choose audience and complete the IARC content-rating questionnaire truthfully. No user accounts, chat, user-generated public content or ads are implemented. The owner must review all Play declarations.

## Build and signing

Requires JDK 17, SDK 36, NDK 27.2.12479018, CMake 3.22.1. `./gradlew assembleDebug assembleRelease bundleRelease testDebugUnitTest testReleaseUnitTest lintDebug lintRelease`.

The release build excludes the Shizuku SDK/provider and privileged capture implementation. Debug builds retain the optional developer integration. Both builds use the same app package; builds signed with different keys cannot update each other in place. Remove the old debug install before installing a separately signed release (this clears local app settings).

Create a persistent upload key once using `JAVA_HOME=/path/to/jdk17 python3 tools/create_upload_key.py`. It writes `.signing/onde-upload.p12` and `.signing/signing.env`, both private and ignored by Git. Back them up securely before the first Play upload. Never send them in a public issue or commit. Load the environment with `source .signing/signing.env` and build `./gradlew bundleRelease assembleRelease`. The upload key signs the AAB; Play App Signing manages the distributed signing key. The public certificate can be exported with keytool using environment-variable password options.

Without all four `ONDE_UPLOAD_*` environment variables CI creates an **unsigned release candidate**, never a Play-uploadable signed release. A partially configured environment fails configuration. GitHub Actions has no signing secret and never publishes to Play.

Run `python3 tools/verify_native_apk.py <release.apk>` and `python3 tools/verify_release.py app/build/outputs/bundle/release/app-release.aab`. Check APK signature with `apksigner verify`, AAB signature with `jarsigner -verify` and the final bundle with bundletool before uploading. The verify scripts do not replace Play pre-launch reports or real receiver tests.

## Console sequence

1. Configure the verified account and create the app only after deciding pricing; use `app.onde.audio` for its bundle.
2. Review publisher contact, privacy page, Data safety draft, audience/content rating and ads (none). Ensure the privacy URL is public and available in the release app.
3. Configure Play App Signing and upload the signed 0.2.0 AAB to internal testing. Retain the upload key; do not use the local debug key.
4. Use the FR/EN listing drafts in `play/listings`, real screenshots and the graphics. Verify title <=30 characters, short description <=80, full description <=4000. Store text deliberately avoids universal compatibility claims. Do not reuse the upstream screenshots under `images/` as screenshots of Onde.
5. Complete foreground-service declarations using `play/REVIEWER.md` and record a real feature-demonstration video. Provide reviewer access instructions; reviewers need a reachable compatible receiver on the same LAN. No app login is required, but the source music service may require its own account/subscription.
6. Complete the hardware test matrix and review Play’s pre-launch report. For a personal developer account created after 13 November 2023, the currently documented closed-test minimum is 12 continuously opted-in testers for 14 days; meeting the minimum only enables an application for production access, not automatic approval. Organization/older accounts may differ.
7. Confirm commercial/AGPL obligations, price and distribution countries, then submit for review. Do not promise approval or an exact review date. No live production submission has been performed by this preparation.

## References (checked 27 October 2026)

- Target SDK: https://support.google.com/googleplay/android-developer/answer/11926878
- AAB: https://developer.android.com/guide/app-bundle
- Signing: https://support.google.com/googleplay/android-developer/answer/9842756
- Pricing: https://support.google.com/googleplay/android-developer/answer/6334373
- Testing: https://support.google.com/googleplay/android-developer/answer/14151465
- App content/privacy: https://support.google.com/googleplay/android-developer/answer/9859455
- Data safety: https://support.google.com/googleplay/android-developer/answer/10787469
- FGS: https://support.google.com/googleplay/android-developer/answer/13392821
