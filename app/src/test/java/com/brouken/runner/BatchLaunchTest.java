package com.brouken.runner;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class BatchLaunchTest {

    @Test
    public void requestsEveryCandidateAndCountsOnlySuccessfulRequests() {
        int requested = BatchLaunch.requestAll(Arrays.asList("one", "two", "three"),
                packageName -> !"two".equals(packageName));

        assertEquals(2, requested);
    }
}
