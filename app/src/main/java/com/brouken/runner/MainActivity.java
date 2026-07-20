package com.brouken.runner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PLAY_UPDATES = "com.google.android.finsky.VIEW_MY_DOWNLOADS";

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildUi());
    }

    private View buildUi() {
        final LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        final int pad = dp(20);
        root.setPadding(pad, pad, pad, pad);

        addText(root, "Runner", 26, true);
        addText(root,
                "Samsung puts unused apps into deep sleep, which hides them from the "
                        + "Play Store so they never get updated. Tap the button below to briefly "
                        + "wake every Play Store app so updates become available again.",
                15, false);

        final Button wake = new Button(this);
        wake.setText("Wake sleeping apps");
        wake.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { wakeApps(); }
        });
        root.addView(wake);

        status = new TextView(this);
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        status.setPadding(0, dp(12), 0, dp(12));
        root.addView(status);

        addText(root,
                "How to get the updates:\n\n"
                        + "1. Tap \"Wake sleeping apps\" and let the apps flash past.\n"
                        + "2. Open the Play Store.\n"
                        + "3. Tap your profile icon > Manage apps & device.\n"
                        + "4. Pull down to refresh — updates do NOT appear on their own.",
                15, false);

        final Button openStore = new Button(this);
        openStore.setText("Open Play Store updates");
        openStore.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { openPlayStore(); }
        });
        root.addView(openStore);

        final ScrollView scroller = new ScrollView(this);
        scroller.addView(root, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return scroller;
    }

    private void wakeApps() {
        final PackageManager pm = getPackageManager();
        int woken = 0;
        for (ApplicationInfo app : pm.getInstalledApplications(0)) {
            // Skip system apps; they don't deep-sleep and aren't Play Store updatable.
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            // ponytail: launch every Play-Store app, not just ones we detect as sleeping.
            // OneUI 6/7 no longer marks deep-sleeping apps as !enabled, so the old detection
            // woke nothing. Launching an already-awake app is harmless.
            if (!"com.android.vending".equals(getInstaller(pm, app.packageName))) {
                continue;
            }
            final Intent launch = pm.getLaunchIntentForPackage(app.packageName);
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(launch);
                    woken++;
                } catch (Exception ignored) {
                    // One app refusing to launch must not abort the whole run.
                }
            }
        }
        // ponytail: BAL ceiling. On Android 12+ only the first launch fires from the
        // foreground; the rest are throttled once we're backgrounded, though the target
        // process is usually still woken. Upgrade path if a device wakes only one app:
        // run `am start` per package via Shizuku (ADB-privileged, no root).
        final String msg = "Woke " + woken + " app(s). Now open the Play Store and pull down "
                + "to refresh to see the updates.";
        status.setText(msg);
        status.setTextColor(Color.parseColor("#2E7D32"));
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void openPlayStore() {
        final Intent intent = new Intent(PLAY_UPDATES);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Play Store not found.", Toast.LENGTH_LONG).show();
        }
    }

    private void addText(LinearLayout parent, String text, int sizeSp, boolean bold) {
        final TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        if (bold) {
            tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        tv.setPadding(0, dp(8), 0, dp(8));
        parent.addView(tv);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
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
