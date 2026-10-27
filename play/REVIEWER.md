# Reviewer access and foreground-service declarations — draft

No Onde login, activation code, payment wall or personal test credentials are required by the app binary. The price of the Play listing is undecided. The tester needs a compatible AirPlay receiver on the same local network. A HomePod must already be set up; the household owner must permit this AirPlay connection. No remote test HomePod or playback credentials are supplied by the developer.

Steps: open Onde → review the help → select a discovered receiver → approve audio capture via Android → open a source player that allows playback capture → start playback. Notifications and optional media-session access are requested through Android; notification-listener access is optional and used for track metadata/controls, not personal-message contents. The release app uses standard Android capture and does not include Shizuku/privileged capture. Use Stop in Onde/its notification or Android’s capture indicator to end the session.

## FGS types declared by AudioCaptureService

- `mediaProjection`: user-authorized playback-audio capture and real-time transmission to the selected local receiver. Deferring or interrupting it breaks the current listening session. There is no video/image capture or microphone-input recording; Android’s standard sharing prompt is used for playback audio.
- `mediaPlayback`: user-initiated audio-stream session/service initialization and playback management. No boot receiver launches playback. Describe the implemented transition to projection accurately in the Console. No microphone/location/camera FGS type is declared.

Required video: on a real test setup, show receiver selection, the Android consent prompt, initiation of a permitted source track, actual receiver playback, background/notification behavior and Stop. Host the video at a reviewer-accessible URL; do not substitute a mockup or an emulator-only screen recording as proof of HomePod playback. A script is supplied here, but the real feature video is still missing.

The notification is visible while streaming; audio capture and network resources stop when the user presses Stop. Android or the source app can stop/block capture. No circumvention of source capture restrictions or DRM is implemented in release.
