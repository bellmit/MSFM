/**
 * This class acts as a proxy to the actual listener object.  The
 * proxy object works in conjunction with an EventChannelThreadCommand
 * object (which it contains - only when events need to be sent).  The
 * thread command gets scheduled with a thread pool to perform the
 * work of the command - calling channelUpdate() on the listener object
 * in this case.
 *
 * @author Jing Chen
 */
package com.cboe.domain.instrumentedChannel.event;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.instrumentedChannel.proxy.InstrumentedBaseChannelListenerProxy;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;

public class InstrumentedEventChannelListenerProxy extends InstrumentedBaseChannelListenerProxy
{

    /**
     * Usual constructor creates a new EventChannelListenerProxy for listener and
     * using the given ThreadPool to schedule all its work
     */
    public InstrumentedEventChannelListenerProxy(InstrumentedEventChannelListener aListener, EventChannelAdapter adapter, InstrumentedThreadPool threadPool)
    {
        super(aListener, adapter, threadPool);
    }

    /**
     * This method updates the listener with the given ChannelEvent.
     */
    public void channelUpdate(ChannelEvent event)
    {
        getDelegateListener().channelUpdate(event);
    }
    
}
