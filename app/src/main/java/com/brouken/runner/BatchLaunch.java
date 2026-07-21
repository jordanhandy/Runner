package com.brouken.runner;

import java.util.List;

final class BatchLaunch {
    interface Launcher {
        boolean launch(String packageName);
    }

    static int requestAll(List<String> packageNames, Launcher launcher) {
        int requested = 0;
        for (String packageName : packageNames) {
            if (launcher.launch(packageName)) {
                requested++;
            }
        }
        return requested;
    }
}
