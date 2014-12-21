/**
 * This class acts as a proxy to the actual listener object.  The
 * proxy object works in conjunction with a <CODE>ChannelThreadCommand</CODE>
 * object to send the received events to the listener.  The
 * thread command gets scheduled with a thread pool to perform the
 * work of the command - calling <CODE>channelUpdate()</CODE> on the listener
 * object in this case.
 *
 * @author Derek T. Chambers-Boucher
 * @version 04/18/1999
 */
package com.cboe.util.channel.proxy;

import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelAdapter;
import java.util.Enumeration;

public abstract class BaseChannelListenerProxy implements ChannelListenerProxy
{
    // list of all registered channels for this listener object.  This
    // will make it easier for deregistration of this listener in the
    // event of a communication failure.
    protected ChannelListener listener;
    protected ChannelAdapter adapter;
    protected ProxyThreadCommand command;

    /**
     * Deafult constructor.
     */
    public BaseChannelListenerProxy()
    {
        listener = null;
        adapter = null;
        command = null;
    }

    public BaseChannelListenerProxy(ChannelListener listener, ChannelAdapter adapter, ThreadPool threadPool)
    {
        this.listener = listener;
        this.adapter = adapter;
        command = initializeThreadCommand();
        command.setThreadPool(threadPool);
    }

    public synchronized void cleanUp()
    {
        command.release();
        adapter = null;
        listener = null;
    }

    protected ProxyThreadCommand initializeThreadCommand()
    {
        return new ProxyThreadCommandImpl(this);
    }

    /**
     * This method adds an event to the proxy's EventChannelThreadCommand
     * event queue for processing.
     *
     * @param event the event to send to the listener.
     */
    public synchronized void addEvent(ChannelEvent event)
    {
        // add the event to the existing ChannelThreadCommand's event queue
        command.addEvent(event);
    }

    public synchronized ChannelListener getDelegateListener()
    {
        return listener;
    }

    public int getQueueSize()
    {
        return command.getQueueSize();
    }

    public int getMaxQueueSize()
    {
        return command.getMaxQueueSize();
    }

    /**
     * This method returns the reference of the <CODE>ChannelAdapter</CODE>
     * to the caller.
     *
     * @return a reference to the proxies <CODE>ChannelAdapter</CODE>.
     */
    public ChannelAdapter getChannelAdapter()
    {
        return adapter;
    }

    /**
     * This method returns the set status of the ThreadPool property.
     *
     * @return true if the <CODE>ThreadPool</CODE> has been set; false if it is null.
     */
    public boolean isThreadPool()
    {
        return command.isThreadPool();
    }

    /** Empty the queue of all ChannelEvent objects. Call release() on each
     * object except the last in the queue.
     *
     * @return The last ChannelEvent object in the queue,
     *     or null if queue was empty.
     */
    public ChannelEvent flushQueue()
    {
        Enumeration e = command.flushQueue();
        ChannelEvent ev = null;
        if (e.hasMoreElements())
        {
            ev = (ChannelEvent) e.nextElement();
        }
        while (e.hasMoreElements())
        {
            ev.release();
            ev = (ChannelEvent) e.nextElement();
        }
        return ev;
    }




    /**
     * This abstract method must be implemented by the deriving proxy class.  It
     * should contain a call to the listeners callback method passing the properly
     * cast data from the ChannelEvent object.
     *
     * @param event the ChannelEvent that contains the callback data for the listener.
     */
    public abstract void channelUpdate(ChannelEvent event);
}
