# Required physical validation — pending

Target: Samsung Galaxy S23 and a HomePod mini. Do not treat this as passed until actual results are recorded. Never play audible test tones at night.

Record Android/One UI, HomePod firmware, Onde version/signature, Wi-Fi configuration, source-player versions and subscription tier. For each result include date, observed behavior and a private diagnostic if needed.

- [ ] Fresh signed release install and launch; privacy/help readable; no Shizuku prompt/provider.
- [ ] Receiver discovery and AirPlay access settings; connection/authentication.
- [ ] Consent accepted; audio starts; phone silencing and sensible receiver volume.
- [ ] Deezer: playback, track change, pause/resume; any capture restrictions noted.
- [ ] Spotify: playback, track change, pause/resume; any capture restrictions noted.
- [ ] YouTube Music: playback, track change, pause/resume; any capture restrictions noted.
- [ ] Screen off/locked for at least 10 minutes, with exact OS behavior recorded (projection may be stopped by Android).
- [ ] Stop from app and notification: receiver stops, Android capture indication disappears, phone volume restored.
- [ ] Reject capture consent and notifications separately: no crash, clear recovery; denied notifications must not prevent permitted capture.
- [ ] Revoke capture via Android chip; no crash/restart without consent.
- [ ] Wi-Fi interruption, receiver unavailable, reconnect; playback does not unexpectedly blast from phone.
- [ ] Rotate app, large text, light/dark themes; help/privacy and speaker actions remain usable.
- [ ] Metadata permission optional; capture works independently; no unrelated notification contents in report.
- [ ] Diagnostic share chooser cancelled: nothing sent; report contains no pairing secrets.
- [ ] Real foreground-service demonstration video recorded for Play review.

Other receivers, multiroom and stereo pairs require separate tests before advertising those capabilities.
