/**
 * This class acts as a proxy to the actual listener object.  The
 * proxy object works in conjunction with an EventChannelThreadCommand
 * object (which it contains - only when events need to be sent).  The
 * thread command gets scheduled with a thread pool to perform the
 * work of the command - calling channelUpdate() on the listener object
 * in this case.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/19/1999
 */
package com.cboe.util.event;

import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.proxy.BaseChannelListenerProxy;

public class EventChannelListenerProxy extends BaseChannelListenerProxy
{
    /**
     * Usual constructor creates a new EventChannelListenerProxy for listener and
     * using the given ThreadPool to schedule all its work
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param alistener com.cboe.application.event.EventChannelListener
     * @param threadPool the assigned threadPool scheduling this proxies work.
     */
    protected EventChannelListenerProxy(EventChannelListener aListener, EventChannelAdapter adapter, ThreadPool threadPool)
    {
        super(aListener, adapter, threadPool);
    }

    /**
     * This method updates the listener with the given ChannelEvent.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param event com.cboe.application.event.ChannelEvent
     */
    public void channelUpdate(ChannelEvent event)
    {
        getDelegateListener().channelUpdate(event);
    }

    /**
     * This method implements the equals method to allow this object to be hashable
     *
     * @author Jeff Illian
     *
     * @param Object obj
     */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof EventChannelListenerProxy))
        {
            return (getDelegateListener().equals(((EventChannelListenerProxy) obj).getDelegateListener()));
        }
        return false;
    }

    /**
     * This method implements the hashcode method to allow this object to be hashable
     *
     * @author Jeff Illian
     *
     * @param Object obj
     */
    public int hashCode() {
        return getDelegateListener().hashCode();
    }
}
