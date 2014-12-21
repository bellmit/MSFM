package com.cboe.domain.iec;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelListenerProxy;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.proxy.ProxyThreadCommand;

/**
 * 
 * @author josephg
 *
 */
public class InstrumentedConcurrentEventChannelListenerProxy extends InstrumentedEventChannelListenerProxy {

    public InstrumentedConcurrentEventChannelListenerProxy(InstrumentedEventChannelListener aListener, ConcurrentEventChannelAdapter adapter, InstrumentedThreadPool threadPool)
    {
        super(aListener, adapter, threadPool);
    }

    public void channelUpdate(ChannelEvent event)
    {
        getDelegateListener().channelUpdate(event);
    }

    
    protected ProxyThreadCommand initializeThreadCommand()
    {
        return new InstrumentedConcurrentProxyThreadCommandImpl(this);
    }

 
}
