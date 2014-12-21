package com.cboe.domain.instrumentedChannel.event;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.util.ThreadPool;

public class InProcessInstrumentedEventChannelAdapter extends InstrumentedEventChannelAdapter
{
    public InProcessInstrumentedEventChannelAdapter()
    {
        super();
    }

    protected synchronized ThreadPool getThreadPool()
    {
        if (threadPool == null)
        {
            threadPool = new InstrumentedThreadPool(200, "InProcessInstrumentedIECThreadPool");
	    System.out.println("New InProcessInstrumentedEventChannelAdapter threadpool size "+200) ;
        }
        return threadPool;
    }

}
