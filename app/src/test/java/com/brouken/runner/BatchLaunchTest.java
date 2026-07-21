package com.brouken.runner;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class BatchLaunchTest {

    @Test
    public void steppingEveryCandidateCountsOnlySuccessfulRequests() {
        BatchLaunch batch = new BatchLaunch(Arrays.asList("one", "two", "three"),
                packageName -> !"two".equals(packageName));

        int steps = 0;
        while (batch.hasNext()) {
            batch.step();
            steps++;
        }

        assertEquals(3, steps);
        assertEquals(3, batch.total());
        assertEquals(3, batch.position());
        assertEquals(2, batch.requested());
    }
}
