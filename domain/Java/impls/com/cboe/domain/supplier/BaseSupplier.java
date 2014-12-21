package com.cboe.domain.supplier;

import java.util.*;

import com.cboe.util.channel.*;
import com.cboe.domain.iec.ChannelEventCache;
import com.cboe.domain.iec.ChannelEventCacheFactory;
import com.cboe.domain.supplier.proxy.SupplierChannelListenerProxy;
import com.cboe.domain.supplier.proxy.BaseSupplierProxy;
import com.cboe.util.ThreadPool;

import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.supplier.SupplierChannelListener;

/**
 * LastSaleSummarySupplier extends the ChannelAdapter framework to provide a
 * multithreaded, multichanneled event dispatcher functionality to the CAS
 * callback supplier.  This object is expecting an appropriately typed
 * consumer proxy object to be registered and published to.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/24/1999
 */

public abstract class BaseSupplier extends ChannelAdapter
{
	protected ChannelEventCache channelEventCache;
	
    public BaseSupplier()
    {
        super();
        super.channelEventCache = null;
        this.channelEventCache = ChannelEventCacheFactory.createChannelEventCache();
        start();
    }

    public BaseSupplier(boolean initializeThreadPool)
    {
        super(initializeThreadPool);
        super.channelEventCache = null;
        this.channelEventCache = ChannelEventCacheFactory.createChannelEventCache();
    }

    protected ThreadPool getThreadPool()
    {
        return getGlobalThreadPool();
    }

    public ChannelListenerProxy getListenerProxy(ChannelListener listener)
    {
        return new SupplierChannelListenerProxy(listener, this, getThreadPool());
    }

    public synchronized ChannelListenerProxy addChannelListener(Object group, ChannelListener listener, Object key, Object userData)
    {
        ChannelListenerProxy proxy = addChannelListener(group, listener, key);
        if(proxy != null && userData != null)
        {
            ((SupplierChannelListener)proxy).addListenerUserData(userData.toString());
        }
        return proxy;
    }

    public synchronized ChannelListenerProxy removeChannelListener(Object group, ChannelListener listener, Object key, Object userData)
    {
        ChannelListenerProxy proxy = removeChannelListener(group, listener, key);
        //if proxy is null, it would mean that the listner was either not subscribed before or has already been removed.  in this case, do nothing with userData.
        if(proxy != null && userData != null)
        {
            ((SupplierChannelListener)proxy).removeListenerUserData(userData.toString());
        }
        return proxy;
    }

    protected synchronized ThreadPool createGlobalThreadPool()
    {
		if (threadPool == null)
		{
	    	try {
	            	com.cboe.interfaces.domain.GlobalThreadPoolHome home =
	                    (com.cboe.interfaces.domain.GlobalThreadPoolHome)HomeFactory.getInstance()
	                        .findHome(com.cboe.interfaces.domain.GlobalThreadPoolHome.HOME_NAME);
	            	threadPool = home.find();
	    	}
	    	catch (CBOELoggableException e)
			{
	            	Log.exception(e);
	            	// a really ugly way to get around the missing exception in the interface...
	            	throw new NullPointerException("Could not find GlobalThreadPoolHome.");
	    	}
		}
		return threadPool;
    }

    protected ThreadPool getGlobalThreadPool()
    {
        ThreadPool tp = threadPool;
        if (tp == null)
        {
        	tp = createGlobalThreadPool();
        }
        return tp;
    }

    protected boolean validListener(ChannelListener listener)
    {
        if (listener != null)
        {
            if (!proxyClass.isAssignableFrom(listener.getClass()))
            {
                throw new ClassCastException("listener not assignable from " + proxyClass.getName());
            }
            SupplierChannelListenerProxy proxy = (SupplierChannelListenerProxy)channels.getProxy(listener);
            if (proxy != null) {
                if (((BaseSupplierProxy)listener).getQueuePolicy() != proxy.getQueuePolicy()) {
                    throw new IllegalArgumentException("Listener already has a queue policy = " + proxy.getQueuePolicy());
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * The <CODE>process</CODE> method is called by the <CODE>ChannelAdapter
     * </CODE>'s run method and publishes the given event to the given list of
     * listeners which must be implementers of the <CODE>SupplierListener</CODE>
     * interface.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param listeners the enumeration of listeners to receive the given event.
     * @param event the event to publish.
     */
    protected void process(List listeners, ChannelEvent event)
    {
        for (int i = 0; i < listeners.size(); i++) {
        	try
        	{
	            // call addEvent on the list of listener proxies.
	            ((ChannelListenerProxy)listeners.get(i)).addEvent(event);
        	}
        	catch (Exception e)
        	{
        		Log.exception (e);
        	}
        }
    }
    
    public ChannelEvent getChannelEvent(Object source, Object channel, Object data)
    {
        ChannelEvent channelEvent = channelEventCache.getChannelEvent();
        channelEvent.setData(source, channel, data);
        channelEvent.setChannelAdapter(this);
        return channelEvent;
    }

    public void returnChannelEvent(ChannelEvent channelEvent)
    {
        channelEvent.setListenerCount(0);
        channelEvent.setData(null, null, null);
        channelEvent.setChannelAdapter(null);
        if (!isFinished)
        {
            channelEventCache.returnChannelEvent(channelEvent);
        }
    }
    protected void clearChannelEventCache()
    {
    	if (channelEventCache != null)
    	{
    		channelEventCache.clear();
    	}
    }
    
}
