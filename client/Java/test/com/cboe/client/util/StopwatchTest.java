package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;      // annotation

public class StopwatchTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(StopwatchTest.class);
    }

    private static final String TIME_SENSITIVE =
            "TIME-SENSITIVE, running test again may report success";

    private void pause(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            // ignore
        }
    }

    @Test public void testStopwatch()
    {
        Stopwatch s = new Stopwatch();
        long started = System.currentTimeMillis();
        pause(10);
        s.stop();
        long ended = System.currentTimeMillis();
        assertEquals(TIME_SENSITIVE, ended-started, s.elapsed());

        s.start();
        started = System.currentTimeMillis();
        pause(10);
        ended = System.currentTimeMillis();
        assertEquals(TIME_SENSITIVE,
                ended-started, s.currentlyElapsedMilliseconds());
        long firstPart = ended-started;
        s.pause();
        started = System.currentTimeMillis();
        assertEquals(TIME_SENSITIVE, 0, s.currentlyElapsedMilliseconds());
        pause(10);
        ended = System.currentTimeMillis();
        assertEquals(TIME_SENSITIVE,
                ended-started, s.currentlyElapsedMilliseconds());

        s.resume();
        started = System.currentTimeMillis();
        pause(10);
        ended = System.currentTimeMillis();
        assertEquals(TIME_SENSITIVE,
                ended-started, s.currentlyElapsedMilliseconds());
        s.stop();
        assertEquals(0, s.currentlyElapsedSeconds());
        assertEquals(TIME_SENSITIVE, firstPart+ended-started, s.elapsed());
        String ms = Long.toString(firstPart+ended-started);
        assertEquals(TIME_SENSITIVE, "00:00:00.0"+ms, s.toString());
    }
}
