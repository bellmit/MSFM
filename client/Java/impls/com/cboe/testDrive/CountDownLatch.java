//
// -----------------------------------------------------------------------------------
// Source file: CountDownLatch.java
//
// PACKAGE: com.cboe.testDrive
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.testDrive;

/**
 * Very simple, generic counter that can be used with worker threads to
 * simulate a java.util.concurrent.CountDownLatch, which isn't available until
 * JDK 1.5.
 *<P>
 * The controlling thread can spawn 'initialThreadCount' worker threads which
 * would each be responsible for calling countDown() to decrement this
 * CountDownLatch's count when they finish their execution.
 *<P>
 * The controlling thread should call await(), which will not return until the
 * count reaches zero.  If the count is already at zero, await() will return
 * immediately.
 */
public class CountDownLatch
{
    public static final long DEFAULT_SLEEP_TIME = 100;
    private long sleepTime;
    private int numPendingThreads;

    /**
     * Intialize with the number of times the countDown() method will have to
     * be called before the await() method returns.
     * @param initialThreadCount
     */
    public CountDownLatch(int initialThreadCount)
    {
        this(initialThreadCount, DEFAULT_SLEEP_TIME);
    }

    /**
     * This constructor sets the length of time that the current thread will
     * sleep in the await() method, between checks to see if the count has
     * reached zero.
     * @param initialCount
     * @param sleepTime
     */
    public CountDownLatch(int initialCount, long sleepTime)
    {
        numPendingThreads = initialCount;
        this.sleepTime = sleepTime;
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is interrupted.
     * @throws InterruptedException
     */
    public void await()
            throws InterruptedException
    {
        while(getCount() > 0)
        {
            Thread.sleep(sleepTime);
        }
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is interrupted, or the specified waiting time
     * elapses.
     *<P>
     * @param timeoutMillis the maximum time to wait in milliseconds
     * @return <tt>true</tt> if the count reached zero and <tt>false</tt>
     * if the waiting time elapsed before the count reached zero.
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    public boolean await(long timeoutMillis)
            throws InterruptedException
    {
        long startTime = System.currentTimeMillis();
        while(getCount() > 0)
        {
            Thread.sleep(sleepTime);
            if(System.currentTimeMillis() - startTime > timeoutMillis)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Every call to countDown() will decrement this latch's current count.
     * @return the new latch count
     */
    public int countDown()
    {
        return --numPendingThreads;
    }

    /**
     * Returns the latch's current count.
     * @return
     */
    public int getCount()
    {
        return numPendingThreads;
    }
}
