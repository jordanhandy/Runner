# Runner

_Runner_ is a small direct-APK utility for Samsung devices running Android (One UI). It helps you open installed apps before checking the Play Store for updates.

### Deep sleeping apps

One UI's Deep sleeping apps only run when you open them. Runner cannot read or change One UI's private Deep sleeping apps list, and it cannot confirm that the Play Store will offer an update. It simply gives you a deliberate way to request launches for installed user apps.

Runner requests launches for all user-installed apps with launcher activities in one foreground-initiated batch, then immediately brings Runner back to the front. Apps may flash briefly while Android processes the requests. Apps without launcher activities are reported as unavailable; they cannot be refreshed by opening an activity.

## How to update deep sleeping apps

1. Install Runner's release APK.
2. Tap **Open apps for refresh** and remain on Runner while it requests the launches.
3. Tap **Open Play Store updates**, then pull down to refresh.

One UI, not Runner, controls whether apps return to Deep sleeping apps after you stop using them.

## Distribution

Runner needs Android's `QUERY_ALL_PACKAGES` permission to show a complete local launch queue. That broad package visibility is not suitable for ordinary Google Play distribution, so this project distributes APKs directly.

## Building

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug assembleRelease
```

The debug APK is written to `app/build/outputs/apk/debug/`. The non-debuggable release APK is written to `app/build/outputs/apk/release/`.
