package com.brouken.runner;

import android.app.Activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class MainActivity extends Activity {

    private static final String PLAY_UPDATES = "com.google.android.finsky.VIEW_MY_DOWNLOADS";

    private TextView status;
    private Button wakeButton;
    private int unavailableApps;

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
                "Runner helps you open installed apps in one batch before checking for "
                        + "updates. One UI does not let other apps read or change its Deep sleeping "
                        + "apps list, so Runner cannot tell which apps are sleeping.",
                15, false);

        wakeButton = new Button(this);
        wakeButton.setText("Open apps for refresh");
        wakeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { wakeApps(); }
        });
        root.addView(wakeButton);

        status = new TextView(this);
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        status.setPadding(0, dp(12), 0, dp(12));
        root.addView(status);

        addText(root,
                "How to refresh apps:\n\n"
                        + "1. Tap \"Open apps for refresh\". Runner requests every launch, then "
                        + "returns to this screen.\n"
                        + "2. Open the Play Store and pull down to refresh.\n\n"
                        + "Runner only records launch requests. One UI manages whether apps are "
                        + "deep sleeping after you stop using them.",
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
        final List<String> packageNames = new ArrayList<>();
        unavailableApps = 0;
        for (ApplicationInfo app : pm.getInstalledApplications(0)) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                    || getPackageName().equals(app.packageName)) {
                continue;
            }
            if (pm.getLaunchIntentForPackage(app.packageName) == null) {
                unavailableApps++;
            } else {
                packageNames.add(app.packageName);
            }
        }
        final int requested = BatchLaunch.requestAll(packageNames, packageName -> launchPackage(pm, packageName));
        bringRunnerToFront();
        status.setText("Requested launches for " + requested + " of " + packageNames.size()
                + " launchable user app(s). " + unavailableApps + " app(s) have no launcher activity.");
        status.setTextColor(Color.parseColor("#2E7D32"));
    }

    private boolean launchPackage(PackageManager pm, String packageName) {
        final Intent launch = pm.getLaunchIntentForPackage(packageName);
        if (launch == null) {
            unavailableApps++;
            return false;
        }
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        try {
            startActivity(launch);
            return true;
        } catch (Exception e) {
            unavailableApps++;
            return false;
        }
    }

    private void bringRunnerToFront() {
        final Intent back = new Intent(this, MainActivity.class);
        back.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        try {
            startActivity(back);
        } catch (Exception ignored) {
            // The batch still completed even if Android keeps the last app in front.
        }
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
}
