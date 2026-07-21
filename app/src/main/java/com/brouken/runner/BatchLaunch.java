package com.brouken.runner;

import java.util.List;

/**
 * Drives a batch of launch requests one at a time so the caller can space them
 * out (e.g. on the main looper) instead of firing every startActivity() in a
 * single synchronous loop, which stalls the UI thread.
 */
final class BatchLaunch {
    interface Launcher {
        boolean launch(String packageName);
    }

    private final List<String> packageNames;
    private final Launcher launcher;
    private int index;
    private int requested;

    BatchLaunch(List<String> packageNames, Launcher launcher) {
        this.packageNames = packageNames;
        this.launcher = launcher;
    }

    boolean hasNext() {
        return index < packageNames.size();
    }

    /** Launches the next package, advancing the cursor and success count. */
    void step() {
        if (launcher.launch(packageNames.get(index++))) {
            requested++;
        }
    }

    int position() {
        return index;
    }

    int total() {
        return packageNames.size();
    }

    int requested() {
        return requested;
    }
}
