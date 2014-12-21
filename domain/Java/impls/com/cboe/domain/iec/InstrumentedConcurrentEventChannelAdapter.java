package com.cboe.domain.iec;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelListenerProxy;

/**
 * InstrumentedConcurrentEventChannelAdapter extends ConcurrentEventChannelAdapter to provide  
 * an instrumented version. 
 * 
 * @author josephg
 *
 */
public class InstrumentedConcurrentEventChannelAdapter extends ConcurrentEventChannelAdapter 
{
    /**
     * Constructor.
     */
    public InstrumentedConcurrentEventChannelAdapter(InstrumentedConcurrentQueue queue, ChannelEventCache channelEventCache, InstrumentedThreadPool threadPool)
    {
        super(queue,channelEventCache, threadPool);
    }

    protected ChannelListenerProxy getListenerProxy(ChannelListener listener)
    {
        return new InstrumentedConcurrentEventChannelListenerProxy((InstrumentedEventChannelListener)listener, this, (InstrumentedThreadPool)getThreadPool());
    }


}
