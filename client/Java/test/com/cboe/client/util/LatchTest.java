package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;      // annotation

public class LatchTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(LatchTest.class);
    }

    void pause(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            // We don't care that sleep ended early
        }
    }

    @Test public void testConstructor()
    {
        Latch latch = new Latch();
        assertEquals(0, latch.getCurrentCount());
        assertEquals(1, latch.getTargetCount());

        latch = new Latch(false);
        assertEquals(0, latch.getCurrentCount());
        assertEquals(1, latch.getTargetCount());

        latch = new Latch(true);
        assertEquals(1, latch.getCurrentCount());
        assertEquals(1, latch.getTargetCount());

        latch = new Latch(3);
        assertEquals(0, latch.getCurrentCount());
        assertEquals(3, latch.getTargetCount());

        latch = new Latch(5, false);
        assertEquals(0, latch.getCurrentCount());
        assertEquals(5, latch.getTargetCount());

        latch = new Latch(5, true);
        assertEquals(5, latch.getCurrentCount());
        assertEquals(5, latch.getTargetCount());
    }

    class Releaser extends Thread
    {
        private Latch latch;
        private int count;
        public Releaser(Latch lat)
        {
            this (lat, 1);
        }
        public Releaser(Latch l, int n)
        {
            latch = l;
            count = n;
        }
        public void run()
        {
            Thread.yield();     // Let our caller get set up
            latch.release();    // Now release the latch
            for (int i = 1; i < count; ++i)
            {
                pause(1000);
                latch.release();
            }
        }
    }

    class WaitReleaser extends Thread
    {
        private Latch latch;
        private int count;
        WaitReleaser(Latch lat)
        {
            this(lat, 1);
        }
        WaitReleaser(Latch l, int n)
        {
            latch = l;
            count = n;
        }
        public void run()
        {
            pause(2000);
            latch.release();
            for (int i = 1; i < count; ++i)
            {
                pause(1000);
                latch.release();
            }
        }
    }

    class OverReleaser extends Thread
    {
        private Latch latch;
        private int count;
        public OverReleaser(Latch lat)
        {
            this (lat, 1);
        }
        public OverReleaser(Latch l, int n)
        {
            latch = l;
            count = n;
        }
        public void run()
        {
            Thread.yield();     // Let our caller get set up
            latch.release(3);  // Now release the latch
            for (int i = 3; i < count; i += 3)
            {
                pause(1000);
                latch.release(3);
            }
        }
    }


    @Test public void testAcquire()
    {
        Latch latch = new Latch();
        new Releaser(latch).start();
        
        Boolean unchanged = latch.acquire();
        assertFalse(unchanged);

        unchanged = latch.acquire();
        assertTrue(unchanged);

        latch = new Latch(2);
        new Releaser(latch, 2).start();

        unchanged = latch.acquire();
        assertFalse(unchanged);

        unchanged = latch.acquire();
        assertTrue(unchanged);
    }

    @Test public void testAcquireMillis()
    {
        Latch latch = new Latch();
        new WaitReleaser(latch).start();

        // WaitReleaser should not be able to release in this short time
        Boolean unchanged = latch.acquire(10);
        assertTrue(unchanged);

        // WaitReleaser should be able to release in this longer time
        unchanged = latch.acquire(4000);
        assertFalse(unchanged);

        // Already released, no change with this acquire call.
        unchanged = latch.acquire(4000);
        assertTrue(unchanged);
    }

    @Test public void testAcquireAndReset()
    {
        Latch latch = new Latch();
        new Releaser(latch).start();

        // We'll get the latch, state has changed
        Boolean unchanged = latch.acquireAndReset();
        assertFalse(unchanged);
        // Reset means that count is back to 0
        assertEquals(0, latch.getCurrentCount());
    }

    @Test public void testAcquireAndResetMillis()
    {
        Latch latch = new Latch();
        new WaitReleaser(latch).start();

        // WaitReleaser should not be able to release in this short time
        Boolean  unchanged = latch.acquireAndReset(10);
        assertTrue(unchanged);

        // WaitReleaser should be able to release in this longer time
        unchanged = latch.acquireAndReset(4000);
        assertFalse(unchanged);
        // Reset means that count is back to 0
        assertEquals(0, latch.getCurrentCount());
    }

    @Test public void testAcquireIf()
    {
        Latch latch = new Latch();
        boolean changed = latch.acquireIf(false);
        assertFalse(changed);

        new Releaser(latch).start();
        Boolean unchanged = latch.acquireIf(true);
        assertFalse(unchanged);
    }

    @Test public void testReleaseMany()
    {
        Latch latch = new Latch();
        new OverReleaser(latch).start();

        Boolean unchanged = latch.acquire();
        assertFalse(unchanged);

        unchanged = latch.acquire();
        assertTrue(unchanged);
    }
}
