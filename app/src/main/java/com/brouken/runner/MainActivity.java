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
    private Button queueButton;
    private AppQueue queue;
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
                "Runner helps you open installed apps one at a time before checking for "
                        + "updates. One UI does not let other apps read or change its Deep sleeping "
                        + "apps list, so Runner cannot tell which apps are sleeping.",
                15, false);

        queueButton = new Button(this);
        queueButton.setText("Prepare app queue");
        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { handleQueueButton(); }
        });
        root.addView(queueButton);

        status = new TextView(this);
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        status.setPadding(0, dp(12), 0, dp(12));
        root.addView(status);

        addText(root,
                "How to refresh apps:\n\n"
                        + "1. Prepare the queue, then tap to open one app.\n"
                        + "2. Return to Runner and repeat for the apps you want to refresh.\n"
                        + "3. Open the Play Store and pull down to refresh.\n\n"
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

    private void handleQueueButton() {
        if (queue == null) {
            prepareQueue();
        } else {
            openNextApp();
        }
    }

    private void prepareQueue() {
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
        queue = new AppQueue(packageNames);
        updateQueueUi();
    }

    private void openNextApp() {
        if (!queue.hasNext()) {
            updateQueueUi();
            return;
        }
        final String packageName = queue.next();
        final Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch == null) {
            unavailableApps++;
            updateQueueUi();
            return;
        }
        try {
            startActivity(launch);
        } catch (Exception e) {
            unavailableApps++;
            Toast.makeText(this, "Runner could not open " + packageName + ".", Toast.LENGTH_LONG).show();
        }
        updateQueueUi();
    }

    private void updateQueueUi() {
        if (queue.hasNext()) {
            final int next = queue.openedCount() + 1;
            status.setText("Ready to request " + queue.totalCount() + " app launch(es). "
                    + unavailableApps + " app(s) have no launcher activity.");
            queueButton.setText("Open next app (" + next + " of " + queue.totalCount() + ")");
            return;
        }
        status.setText("Requested " + queue.openedCount() + " app launch(es). "
                + unavailableApps + " app(s) could not be opened. Open Play Store and refresh.");
        status.setTextColor(Color.parseColor("#2E7D32"));
        queueButton.setEnabled(false);
        queueButton.setText("Queue complete");
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
