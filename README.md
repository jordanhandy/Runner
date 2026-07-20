# Runner

_Runner_ is a simple app that helps you update [**Deep sleeping apps**](https://www.samsung.com/us/support/answer/ANS00088422/) on **Samsung** devices running Android (One UI).

This is a fork of [moneytoo/Runner](https://github.com/moneytoo/Runner), updated to work on current versions of One UI. See [What's different in this fork](#whats-different-in-this-fork) below.

### Deep sleeping apps

If you have this feature enabled, unused (and manually added) apps get put to sleep. The problem is that sleeping apps are invisible to the _Play Store_, so it never updates them unless they are briefly woken up by launching them.

That is all _Runner_ does: it launches your installed apps so the _Play Store_ can see them and offer updates. Once the apps go idle again, One UI puts them back to sleep on its own.

## How to update deep sleeping apps

1. Install the latest apk of _Runner_.
2. Open _Runner_ and tap **Wake sleeping apps**. Your apps will flash past on the screen.
3. Open the _Play Store_, tap your profile icon, then **Manage apps & device**.
4. Pull down to refresh. Updates do not show up on their own, so this step is important.
5. Update your apps. When they go idle, One UI puts them back to sleep automatically.

## What's different in this fork

The original app stopped working on newer One UI (roughly One UI 6 and 7). This fork fixes that and adds some feedback so you can tell it worked:

- **It works on current One UI again.** The original only launched apps that One UI reported as "disabled," which is how older versions marked sleeping apps. Newer versions no longer do that, so the app launched nothing. It now launches every app installed from the Play Store, which is safe because waking an app that is already awake does nothing.
- **No more "built for an older version of Android" warning.** The app now targets a current Android version, and the version number was bumped so it installs cleanly over the old build.
- **A real screen instead of a crash.** The original did everything invisibly and then closed, which on newer phones showed the launch screen, froze, and crashed. There is now a simple screen with a button to wake the apps, a count of how many were woken, step-by-step instructions, and a shortcut to the Play Store.

### Building it yourself

With Android Studio, open the project and run it. From the command line:

```
./gradlew assembleDebug
```

The apk is written to `app/build/outputs/apk/debug/`.

### A note on very new devices

On Android 12 and up, the system limits how many apps one app can launch in a row. In practice the wake still works on most phones, but if you find only one app wakes up, that limit is why. Waking every app reliably would need Shizuku (ADB access without root); this fork does not require it.
