/**
 * EventChannelAdapter is responsible for maintaining the list of
 * registered channels and the listeners for each channel.  It is also
 * directing a new event to the matching channel when raised.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Jeff Illian
 * @author Derek T. Chambers-Boucher
 */
package com.cboe.util.event;

import java.util.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelEvent;

public class EventChannelAdapter extends ChannelAdapter
{
    // EventChannelAdapter Name
    protected String name;

    /**
     * Constructor.
     */
    public EventChannelAdapter()
    {
        super();
        start();
    }

    /*
     * This constructor leaves all the initialization to the subclass.
     */
    protected EventChannelAdapter(int dummy)
    {
    	super(dummy);
    }
    
    public EventChannelAdapter(boolean isThreadStart)
    {
        super();

        if (isThreadStart)
        {
            start();
        }
    }


    protected synchronized ChannelListenerProxy getListenerProxy(ChannelListener listener)
    {
        return new EventChannelListenerProxy((EventChannelListener)listener, this, getThreadPool());
    }

    protected synchronized ThreadPool getThreadPool()
    {
        if (threadPool == null)
        {
            name = this.getClass() + "-WorkerThread";
            threadPool = new ThreadPool(DEFAULT_POOL_SIZE, name);
        }
        return threadPool;
    }

    protected String getListenerClassName()
    {
        return EventChannelListener.class.getName();
    }

    /**
     * For each listener in the listeners collection , the thread will dispatch
     * the event to all of the listener proxies on the channel.  This is the
     * implementation of the <CODE>ChannelAdapter</CODE> abstract method.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param listeners the collection of <CODE>ChannelListener</CODE>s for this event.
     * @param event the <CODE>ChannelEvent</CODE> to send to the collection of listeners.
     */
    protected final void process(List listeners, ChannelEvent event)
    {
        for (int i = 0; i < listeners.size(); i++) {
            // call addEvent on the list of listener proxies.
        	try
        	{
        		((ChannelListenerProxy)listeners.get(i)).addEvent(event);
        	}
        	catch (Exception e)
        	{
        		e.printStackTrace();
        	}
        }
    }
}
