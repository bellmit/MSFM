/**
 * EventChannelAdapter is responsible for maintaining the list of
 * registered channels and the listeners for each channel.  It is also
 * directing a new event to the matching channel when raised.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Jing Chen
 */
package com.cboe.domain.instrumentedChannel.event;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.event.EventChannelAdapter;

public class InstrumentedEventChannelAdapter extends EventChannelAdapter
{
	
	public static final String INSTRUMENTOR_NAME = "InstrumentedIECThreadPool";
	
    /**
     * Constructor.
     */
    public InstrumentedEventChannelAdapter()
    {
        super();
    }

    public InstrumentedEventChannelAdapter(boolean isThreadStart)
    {
        super(isThreadStart);
    }

    protected synchronized ChannelListenerProxy getListenerProxy(ChannelListener listener)
    {
        return new InstrumentedEventChannelListenerProxy((InstrumentedEventChannelListener)listener, this, (InstrumentedThreadPool)getThreadPool());
    }

    protected synchronized ThreadPool getThreadPool()
    {
        if (threadPool == null)
        {
            threadPool = new InstrumentedThreadPool(DEFAULT_POOL_SIZE, INSTRUMENTOR_NAME);
        }
        return threadPool;
    }

}
