package com.brouken.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class AppQueueTest {

    @Test
    public void returnsEachPackageOnceInOrder() {
        AppQueue queue = new AppQueue(Arrays.asList("one", "two"));

        assertTrue(queue.hasNext());
        assertEquals("one", queue.next());
        assertEquals("two", queue.next());
        assertFalse(queue.hasNext());
        assertEquals(2, queue.openedCount());
    }
}
