package com.cboe.client.util;

/**
 * Stopwatch.java
 *
 * @author Dmitry Volpyansky
 *
 */

public class Stopwatch
{
    long start;
    long elapsed;

    public Stopwatch()
    {
        start();
    }

    public void start()
    {
        start   = System.currentTimeMillis();
        elapsed = 0L;
    }

    public void stop()
    {
        elapsed += System.currentTimeMillis() - start;
    }

    public void pause()
    {
        elapsed += System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
    }

    public void resume()
    {
        start = System.currentTimeMillis();
    }

    public long elapsed()
    {
        return elapsed;
    }

    public int currentlyElapsedMilliseconds()
    {
        return (int) (System.currentTimeMillis() - start);
    }

    public int currentlyElapsedSeconds()
    {
        return (int) ((System.currentTimeMillis() - start) / 1000);
    }

    public String toString()
    {
        return StringHelper.newString(DateHelper.makeHHMMSSsss(elapsed, DateHelper.TIMEZONE_OFFSET_UTC));
    }
}
