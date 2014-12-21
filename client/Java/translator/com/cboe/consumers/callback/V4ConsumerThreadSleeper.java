//
// -----------------------------------------------------------------------------------
// Source file: V4ConsumerThreadSleeper.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.AcceptTimeDelay;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * This maintains/increments a counter for how many times waitDelay() has been
 * called from each thread.  It will cause the current thread to sleep every
 * 'sleepFrequency' times that thread calls waitDelay().
 *
 * The count is maintained individually for each thread so that while one
 * thread is sleeping, other threads can continue to increment their own
 * counts, and sleep when necessary.
 */
public class V4ConsumerThreadSleeper implements AcceptTimeDelay
{
    private final static V4ConsumerThreadSleeper V4_CONSUMER_DELAY = new V4ConsumerThreadSleeper();

    private final int sleepFrequency;
    private final long sleepMillis;
    private ThreadLocal<Counter> threadLocal;

    private V4ConsumerThreadSleeper()
    {
        threadLocal = new ThreadLocal<Counter>();
        sleepFrequency = ConsumerPropertyManager.getInstance().getIntValue(TIME_DELAY_PROPERTY_SECTION,
                                                                           V4_CONSUMER_DELAY_FREQ_PROPERTY_NAME,
                                                                           V4_CONSUMER_DELAY_FREQ_PROPERTY_NAME,
                                                                           100);
        sleepMillis = ConsumerPropertyManager.getInstance().getIntValue(TIME_DELAY_PROPERTY_SECTION,
                                                                        V4_CONSUMER_DELAY_PROPERTY_NAME,
                                                                        V4_CONSUMER_DELAY_PROPERTY_NAME,
                                                                        100);
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            GUILoggerHome.find().debug(getClass().getName(), GUILoggerBusinessProperty.MARKET_QUERY," sleepFrequency="+ sleepFrequency+", sleepMillis="+
                                       sleepMillis);
        }
    }

    /**
     * Causes the current thread to sleep for 'sleepMillis' milliseconds every 'sleepFrequency' times this is called.
     */
    protected void waitDelay()
    {
        Counter counter = threadLocal.get();
        if(counter == null)
        {
            counter = new Counter();
            threadLocal.set(counter);
        }
        if(counter.increment() % sleepFrequency == 0)
        {
            try
            {
                Thread.sleep(sleepMillis);
            }
            catch(InterruptedException e)
            {
            }
        }
    }

    protected static V4ConsumerThreadSleeper find()
    {
        return V4_CONSUMER_DELAY;
    }

    private class Counter
    {
        private long count = 0;

        long increment()
        {
            // overkill???
            if(count == Long.MAX_VALUE)
            {
                count = 0;
            }
            return ++count;
        }
    }
}
