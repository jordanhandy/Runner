package com.brouken.runner;

import java.util.List;

final class AppQueue {
    private final List<String> packageNames;
    private int nextIndex;

    AppQueue(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    boolean hasNext() {
        return nextIndex < packageNames.size();
    }

    String next() {
        return packageNames.get(nextIndex++);
    }

    int openedCount() {
        return nextIndex;
    }

    int totalCount() {
        return packageNames.size();
    }
}
