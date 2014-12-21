package com.cboe.domain.instrumentedChannel.event;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.util.ThreadPool;

public class InProcessInstrumentedConcurrentQuoteEventChannelAdapter extends InstrumentedEventChannelAdapter
{
    public static final String MAX_FIX_CONCURRENT_QUOTES_PER_CLASS = "maxFixConcurrentQuotesPerClass";
    private static int maxFixConcurrentQuotesPerClass;
    public InProcessInstrumentedConcurrentQuoteEventChannelAdapter()
    {
        super();
    }

    protected synchronized ThreadPool getThreadPool()
    {
        try {
            maxFixConcurrentQuotesPerClass = Integer.parseInt(System.getProperty(MAX_FIX_CONCURRENT_QUOTES_PER_CLASS, "200"));
        } catch (NumberFormatException e) {
            maxFixConcurrentQuotesPerClass = 200;
        }

        if (threadPool == null)
        {
            threadPool = new InstrumentedThreadPool(maxFixConcurrentQuotesPerClass, "InProcessInstrumentedConcurrentQuoteIECThreadPool");
	    System.out.println("New InProcessInstrumentedConcurrentQuoteEventChannelAdapter threadpool size "+maxFixConcurrentQuotesPerClass) ;
        }
        return threadPool;
    }

}
