/**
 * <CODE>ChannelAdapter</CODE> is responsible for maintaining the list
 * of registered channels and the listeners for each channel.  It also
 * directs a new event to the matching channel when raised.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Jeff Illian
 * @author Derek T. Chambers-Boucher
 *
 * @version 03/19/1999
 */
package com.cboe.util.channel;

import java.util.*;
import com.cboe.util.ThreadPool;

public abstract class ChannelAdapter extends Thread
{
    // Defaults
    // default channel adapter hashtable size
    protected final static int DEFAULT_SIZE = 100;
    // default thread pool size
    protected final static int DEFAULT_POOL_SIZE = 70;

    // Reference Counter
    protected ChannelAdapterReferenceCounter channels;
    // Event Queue
    protected ChannelEventRingQueue queue;
    // channelEvent object pooling cache
    protected ChannelEventCache channelEventCache;
    // The thread pool
    protected volatile ThreadPool threadPool;
    // Class type of the listeners
    protected Class proxyClass;

    // boolean property controls lazy addition of channels through the
    // addChannelListener method -- if false channels must be added
    // via the <CODE>addChannel</CODE> method.
    private volatile boolean dynamicChannels = true;
    // boolean property controls listener removal from the adapter
    // in the event of a communication failure within the listeners
    // callback method.
    private volatile boolean listenerCleanup = true;
    protected volatile boolean isFinished = false;


    /**
     * Constructor.
     */
    public ChannelAdapter()
    {
        super();
        initializeProxyClass();
        channels = new ChannelAdapterReferenceCounter();
        queue = new ChannelEventRingQueue();
        channelEventCache = ChannelEventCache.getChannelEventCache();
        threadPool = getThreadPool();
        registerChannelAdapter();
        String objname = getName();
        String classname = getClass().toString();
        StringBuilder name = new StringBuilder(objname.length()+classname.length()+15);
        name.append(classname).append("@").append(hashCode()).append(" : ").append(objname);
        setName(name.toString());
    }

    /*
     * This constructor leaves all the initialization to the subclass.
     */
    protected ChannelAdapter(int dummy)
    {
    }
    
    public ChannelAdapter(boolean initializeThreadPool)
    {
        super();
        initializeProxyClass();
        channels = new ChannelAdapterReferenceCounter();
        queue = new ChannelEventRingQueue();
        channelEventCache = ChannelEventCache.getChannelEventCache();
        if(initializeThreadPool)
        {
            threadPool = getThreadPool();
        }
        registerChannelAdapter();
        String objname = getName();
        String classname = getClass().toString();
        StringBuilder name = new StringBuilder(objname.length()+classname.length()+15);
        name.append(classname).append("@").append(hashCode()).append(" : ").append(objname);
        setName(name.toString());
    }

    protected void registerChannelAdapter()
    {
        ChannelAdapterRegistrar.registerChannelAdapter(this);
    }

    protected void unregisterChannelAdapter()
    {
        ChannelAdapterRegistrar.unregisterChannelAdapter(this);
    }

    public ChannelEvent getChannelEvent(Object source, Object channel, Object data)
    {
        ChannelEvent channelEvent = channelEventCache.getChannelEvent();
        channelEvent.setData(source, channel, data);
        channelEvent.setChannelAdapter(this);
        return channelEvent;
    }

    public ThreadPool getPool()
    {
       return threadPool;
    }

    public synchronized void stopChannelAdapter()
    {
        isFinished = true;
        notify();
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

    /**
     * Sets the boolean property to allow listener removal from the
     * <CODE>ChannelAdapter</CODE> in the case of a communication
     * failure on the listeners callback method.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param value the boolean value to set the property.
     */
    public void setListenerCleanup(boolean value)
    {
        listenerCleanup = value;
    }

    /**
     * Returns the value of the ListenerCleanup property.
     *
     * @see ChannelAdapter::setListenerCleanup
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return the boolean value of the property.
     */
    public boolean isListenerCleanup()
    {
        return listenerCleanup;
    }

    /**
     * Sets the boolean property to allow dynamic addition of channels to the
     * ChannelAdapter through the <code>addChannelListener</code> method
     * instead of adding it first through the <code>addChannel</code> method.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param value the boolean value to set the property.
     */
    public void setDynamicChannels(boolean value)
    {
        dynamicChannels = value;
    }

    /**
     * Gets the value of the boolean DynamicChannels property.
     *
     * @see ChannelAdapter::setDynamicChannels
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return the boolean value of the property.
     */
    public boolean getDynamicChannels()
    {
        return dynamicChannels;
    }

    /**
     * addChannel creates a new event channel for a given key with no
     * registered listeners
     *
     * @author Jeff Illian
     *
     * @param key java.lang.Object
     */
    public synchronized void addChannel(Object key)
    {
        if (!channels.isChannel(key))
        {
            channels.addChannel(key);
        }
    }

    /**
     * addChannelListener adds a listener to a particular event channel keyed by "key".
     * If needed, a proxy is wrapped around the listener. This method throws an
     * "EventChannelNotFound" exception if there is an attempt to add a listener
     * to a channel that has not been registered.
     *
     * >>> NOTE <<<
     * This method may be overridden by its subclasses to provide specific functionality
     * such as proxied listeners, etc.
     *
     * @author Derek T. Chambers-Boucher
     * @author Jeff Illian
     *
     * @param listener com.cboe.util.channel.ChannelListener
     * @param key java.lang.Object
     */
    public synchronized ChannelListenerProxy addChannelListener(Object group, ChannelListener listener, Object key)
    {
        ChannelListenerProxy proxy = null;
        if (validListener(listener))
        {
            // Check the dynamicChannels property for truth and add the channel
            // if not already added.
            if (getDynamicChannels() && !channels.isChannel(key))
            {
                addChannel(key);
            }

            // Wrap in a proxy, if needed
            if (!channels.previouslyProxied(listener))
            {
                // Make sure the reference counter knows about the proxy
                // NOTE: The proxy MUST be added to the reference counter before
                //       the listener is added.
                channels.assignProxyToListener(listener, getListenerProxy(listener));
            }

            // Check for the existence of the key and add the listener if not
            // already listening on this channel.
            if (channels.isChannel(key))
            {
                proxy = channels.addChannelListener(group, listener, key);
            }
            else
            {
                // throw an exception - to be added
                System.out.println("*******ChannelAdapter: addChannelListener : Could not find channel: " + key.toString());
            }
        }
        else
        {
            throw new IllegalArgumentException("com.cboe.util.channel.ChannelListener is not valid.");
        }

        return proxy;
    }

    public ChannelListenerProxy getProxyForDelegate(ChannelListener listener)
    {
        return channels.getProxy(listener);
    }

    /**
     * dispatch places a new event in the event channel queue for processing
     *
     * @author Derek T. Chambers-Boucher
     * @author Jeff Illian
     *
     * @param event the channel event to dispatch.
     */
    public synchronized void dispatch(ChannelEvent event)
    {
        queue.insertEvent(event);
        notify();
    }

    /**
     * isChannel is used to determine if an event channel keyed by "key"
     * has been registered.
     *
     * @author Derek T. Chambers-Boucher
     * @author Jeff Illian
     *
     * @return true if registered, false otherwise
     *
     * @param key the channel key to check registration of.
     */
    public boolean isChannel(Object key)
    {
        return channels.isChannel(key);
    }

    /**
     * removeChannel removes an existing event channel.
     * This method throws an "EventChannelNotFound" exception if there is an attempt to add
     * a listener to a channel that has not been registered.
     *
     * @author Derek T. Chambers-Boucher
     * @author Jeff Illian
     *
     * @param key java.lang.Object
     */
    public synchronized void removeChannel(Object key)
    {
        channels.removeChannel(key);
    }

    /**
     * Removes the given listener from every channel on this adapter.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param listener com.cboe.application.sbtApplications.event.EventChannelListener
     */
    public synchronized ChannelListenerProxy removeChannelListener(ChannelListener listener)
    {
        return channels.removeChannelListener(listener);
    }

    /**
     * <CODE>removeChannelListener</CODE> removes a listener to a particular
     * event channel keyed by "key".  This method throws an "EventChannelNotFound"
     * exception if there is an attempt to remove a listener from a channel that
     * has not been registered.
     *
     * >>> NOTE <<<
     * This method may be overridden by its subclasses to provide specific functionality
     * such as proxied listeners, etc.
     *
     * @author Derek T. Chambers-Boucher
     * @author Jeff Illian
     *
     * @param listener com.cboe.application.sbtApplications.event.EventChannelListener
     * @param key java.lang.Object
     */
    public synchronized ChannelListenerProxy removeChannelListener(Object group, ChannelListener listener, Object key)
    {
        return channels.removeChannelListener(group, listener, key);
    }

    /**
     * Removes the given listener from every group on this adapter.
     *
     * @author Jeff Illian
     *
     * @param listenerGroup java.lang.Object
     */
    public synchronized void removeListenerGroup(Object listenerGroup)
    {
        channels.removeChannelGroup(listenerGroup);
    }

    public synchronized Map getRegisteredChannels()
    {
        return channels.getProxyChannels();
    }

    public int getQueueSize()
    {
        return queue.getQueueSize();
    }

    public int getMaxQueueSize()
    {
        return queue.getMaxQueueSize();
    }

    public boolean isListener(ChannelListener listener)
    {
        return channels.previouslyProxied(listener);
    }

    /**
     * The run method checks the queue to see if there are any events that
     * need to be processed.  When the queue is empty, the thread will go
     * to sleep until notified.
     * For each event in the event queue, the thread will dispatch the event to
     * all of the listeners on the channel represented by the event.
     *
     * @author Derek T. Chambers-Boucher
     * @author Jeff Illian
     */
    public synchronized void run()
    {
        List proxies = null;
        ChannelEvent event = null;

        while (! isFinished)
        {
            try
            {
                if (queue.isEmpty())
                {
                    wait();
                }
                while (queue.getQueueSize() > 0)
                {
                    event = queue.getNextEvent();
                    Object channelKey = event.getChannel();

                    if (isChannel(channelKey))
                    {
                        proxies = channels.getProxiesForChannel(channelKey);

                        if (proxies == null || proxies.size() <= 0 )
                        {
                            event.releaseAll();
                        }
                        else
                        {
                            event.setListenerCount(proxies.size());

                            // call the abstract implementation method.
                            process(proxies, event);
                        }
                    }
                    else
                    {
                        event.releaseAll();
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception in Event Queue thread " + e.toString());
                e.printStackTrace();
            }
        }

        unregisterChannelAdapter();
        channels.cleanUp();
        clearQueue();
        clearChannelEventCache();
        threadPool = null;
    }

    protected void initializeProxyClass()
    {
        try
        {
            proxyClass = Class.forName(getListenerClassName());
        }
        catch(ClassNotFoundException e)
        {
            throw new RuntimeException("Class not found: " + e.getMessage());
        }
    }

    protected boolean validListener(ChannelListener listener)
    {
        if (listener != null)
        {
            if (!proxyClass.isAssignableFrom(listener.getClass()))
            {
                throw new ClassCastException("listener not assignable from " + proxyClass.getName());
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    protected abstract String getListenerClassName();

    /**
     * <CODE>process()</CODE> is an abstract method that must be implemented by
     * all inheriters of <CODE>ChannelAdapter</CODE>.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param listeners the collection of listeners to publish the given event to.
     * @param event the <CODE>ChannelEvent</CODE> to send the listeners.
     */
    protected abstract void process(List listeners, ChannelEvent event);

    /**
     * <CODE>getThreadPool()</CODE> is an abstract method that must be implemented by
     * all inheriters of <CODE>ChannelAdapter</CODE>.
     *
     * @author Jeff Illian
     *
     * @returns the thread pool that will be used by the proxies of this adapter
     */
    protected abstract ThreadPool getThreadPool();

    /**
     * <CODE>getListenerProxy()</CODE> is an abstract method that must be implemented by
     * all inheriters of <CODE>ChannelAdapter</CODE>.
     *
     * @author Jeff Illian
     *
     * @param listener the listener to wrap with a proxy
     * @returns returns the proxy used to dispatch to the listener
     */
    protected abstract ChannelListenerProxy getListenerProxy(ChannelListener listener);
    
    protected void clearQueue()
    {
        if (queue != null)
        {
            queue.clearQueue();
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
