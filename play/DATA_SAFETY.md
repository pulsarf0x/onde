# Data safety — implementation-backed draft for owner review

Do not paste a blanket “no data collected” answer: audio and optional metadata leave the phone for a selected LAN receiver. No developer backend, ads, analytics or crash-reporting SDK is implemented in the standard release. This draft is not a completed Console declaration; final categorization depends on the exact form and its definitions.

| Flow | Processing/destination | Retention | Console consideration |
|---|---|---|---|
| Playback audio | Captured only after Android consent; sent to user-selected LAN receivers | Live buffers; no audio-file recording in Onde | Review Audio files → Other audio files. Off-device real-time transmission must be considered; ephemeral processing can still need declaration. Purpose: app functionality. Required for streaming. |
| Title, artist, album, artwork | Optional notification/media-session access; shown locally; optionally sent to receiver | Current track in memory, no listening history | Review applicable audio/other media metadata and photos/artwork categories; do not imply no off-device transmission. Optional. |
| Receiver names/IDs/IP addresses, pairing credentials | Used for discovery/authentication and stored privately on phone | Preferences remain until forgotten/data cleared/uninstalled | Local data generally not collection unless transmitted outside device; identifiers/addresses are also sent as necessary in local receiver protocol exchanges. App functionality only. |
| Technical logs | In-memory diagnostic buffer; explicit Android share sheet can send report to chosen destination | Buffer dies with process; recipient controls shared copies | Review diagnostics/device-or-other-IDs when transmitted. User-initiated sharing exemption may apply, but it does not remove privacy-disclosure obligations. No automatic error-report upload. |
| Support email/public issues | Only when user chooses to contact support | As described in policy; public GitHub posts follow GitHub rules | Confirm support processes and applicable form definitions. Avoid posting secrets/public personal information. |

Answers supported by the implementation: no ads; no account creation in Onde; no targeted advertising; no analytics SDK; no collection on a developer backend during streaming; no physical microphone input capture; no camera or screen-image transmission; no in-app audio recording; no automatic cloud backup of app data; optional notification access for media sessions; standard release excludes Shizuku.

Do not claim “all data encrypted in transit”: encrypted AirPlay 2 is supported, but some AirPlay 1 paths can be unencrypted. Review the Console encryption question for the actual declared flows. Raw local audio transmission and the source service’s separate practices must be explained accurately.

Privacy policy is bundled offline under `app/src/main/assets/privacy-fr.txt`; `tools/sync_privacy_site.py` creates its equivalent at `site/privacy.html`. Publisher identity, contact, support retention and all form answers need the owner’s confirmation before submission.
