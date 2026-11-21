# Pulse Widgets 2.0

Four resizable home-screen widgets for Android: Analog clock, Digital clock, Clock + day + weather, Calendar.
Each widget swaps its layout as you resize it (1x1 shows only the clock; larger sizes add date, weather, week strip, month grid).

## Build
Push to GitHub. Actions builds `app-debug.apk` (artifact `PulseWidgets-debug`). Or run `./gradlew assembleDebug`.

## Updating without uninstalling
- `applicationId` stays `com.pulse.widgets`.
- `versionCode` = 100 + GitHub run number, so every build is an upgrade.
- Every build is signed with `app/pulse.keystore` (committed on purpose). Do not delete or regenerate it:
  Android only updates in place when the signature is identical.
- If your currently installed copy came from an older build signed with a different key, uninstall it ONE time.
  After that, every new APK installs as an update.
