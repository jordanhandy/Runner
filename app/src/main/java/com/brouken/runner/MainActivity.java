package com.brouken.runner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PackageManager packageManager = getPackageManager();
        for (ApplicationInfo applicationInfo : packageManager.getInstalledApplications(0)) {
            // Skip system apps; they don't deep-sleep and aren't Play Store updatable.
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            // Older One UI marked deep-sleeping apps as disabled (!enabled), but One UI 6/7
            // no longer does, so that check woke nothing. Launching an already-awake app is
            // harmless, so we simply launch every Play Store app and drop the unreliable,
            // version-specific sleep detection.
            final String installer = getInstaller(packageManager, applicationInfo.packageName);
            if (!"com.android.vending".equals(installer)) {
                continue;
            }
            final Intent launchIntent = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(launchIntent);
                } catch (Exception ignored) {
                    // One app refusing to launch must not abort the whole run.
                }
            }
        }

        final Intent intent = new Intent("com.google.android.finsky.VIEW_MY_DOWNLOADS");
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent);
        }

        finish();
    }

    @SuppressWarnings("deprecation")
    private static String getInstaller(PackageManager pm, String packageName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return pm.getInstallSourceInfo(packageName).getInstallingPackageName();
            }
            return pm.getInstallerPackageName(packageName);
        } catch (Exception e) {
            return null;
        }
    }
}
