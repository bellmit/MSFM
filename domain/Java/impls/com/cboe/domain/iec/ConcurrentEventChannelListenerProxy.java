/**
 * 
 */
package com.cboe.domain.iec;

import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.proxy.ProxyThreadCommand;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelListenerProxy;
import com.cboe.util.channel.ChannelListener;

/**
 * @author josephg
 *
 */
public class ConcurrentEventChannelListenerProxy extends EventChannelListenerProxy {
	
    public ConcurrentEventChannelListenerProxy(EventChannelListener aListener, EventChannelAdapter adapter, ThreadPool threadPool)
    {
        super(aListener, adapter, threadPool);
    }

    protected ProxyThreadCommand initializeThreadCommand()
    {
        return new ConcurrentProxyThreadCommandImpl(this);
    }

    public void channelUpdate(ChannelEvent event)
    {
        getDelegateListener().channelUpdate(event);
    }

    /**
     *
     * @param event the event to send to the listener.
     */
    public void addEvent(ChannelEvent event)
    {
        // add the event to the existing ChannelThreadCommand's event queue
        command.addEvent(event);
    }

    public ChannelListener getDelegateListener()
    {
        return listener;
    }


}
