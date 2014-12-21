package com.cboe.domain.instrumentedChannel.event;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.util.ThreadPool;

public class InProcessInstrumentedConcurrentOrderEventChannelAdapter extends InstrumentedEventChannelAdapter
{
    public static final String MAX_FIX_CONCURRENT_ORDERS_PER_CLASS = "maxFixConcurrentOrders";
    private static int maxFixConcurrentOrders;
    public InProcessInstrumentedConcurrentOrderEventChannelAdapter()
    {
        super();
    }

    protected synchronized ThreadPool getThreadPool()
    {
        try {
            maxFixConcurrentOrders = Integer.parseInt(System.getProperty(MAX_FIX_CONCURRENT_ORDERS_PER_CLASS, "200"));
        } catch (NumberFormatException e) {
            maxFixConcurrentOrders = 200;
        }

        if (threadPool == null)
        {
            threadPool = new InstrumentedThreadPool(maxFixConcurrentOrders, "InProcessInstrumentedConcurrentOrderIECThreadPool");
	    System.out.println("New InProcessInstrumentedConcurrentOrderEventChannelAdapter threadpool size "+maxFixConcurrentOrders) ;
        }
        return threadPool;
    }

}
