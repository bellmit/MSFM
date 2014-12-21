/**
 * 
 */
package com.cboe.domain.iec;

import java.util.List;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelAdapterReferenceCounter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelListener;

/**
 * ConcurrentEventChannelAdapter extends EventChannelAdapter to provide concurrent 
 * dispatch, get, etc. with minimal synchronization.
 * 
 * @author josephg
 *
 */
public class ConcurrentEventChannelAdapter extends EventChannelAdapter {

	protected ConcurrentQueue queue;
	protected ChannelEventCache channelEventCache;
    
	public ConcurrentEventChannelAdapter(ConcurrentQueue queue, ChannelEventCache channelEventCache, ThreadPool threadPool)
	{
        super(0);
		this.queue = queue;
		this.channelEventCache = channelEventCache;
		this.threadPool = threadPool;
		initialize();
        start();
	}

    private void initialize()
    {
        // do other inits needed.
        initializeProxyClass();
        channels = new ChannelAdapterReferenceCounter();
        registerChannelAdapter();
        setName(getClass() + "@" + hashCode() + " : " + getName());
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

    public void stopChannelAdapter()
    {
        isFinished = true;
        this.interrupt();
    }

    public boolean isChannel(Object key)
    {
        return channels.isChannel(key);
    }
    
	public void dispatch(ChannelEvent event) 
	{
        int cnt = queue.insertEvent(event);
    	if(cnt < 2)
    	{
    		synchronized(queue)
    		{
    			queue.notify();    			
    		}
    	}
	}

    /**
     * This is an optimized version of the parent run() function. We move the 
     * synchronization to the queue instead of the entire adapter object.
     */
    public void run()
    {
        List proxies = null;
        ChannelEvent event = null;
    	Log.information("ConcurrentEventChannelAdapter thread started. Thread=" + getName());                
        while (! isFinished)
        {
            try
            {
				if(queue.isEmpty())
				{
		            synchronized(queue)
					{
		            	if (queue.isEmpty())
		            	{
							// need a safety net of 10ms as there is
							// the chance of missing the check
							// for the notify() on the dispatch()
		            		queue.wait(10);
		            	}
					}
				}
                while ((event = queue.getNextEvent()) != null)
                {
                    Object channelKey = event.getChannel();

                    if (isChannel(channelKey))
                    {
                        proxies = channels.getProxiesForChannel(channelKey);

                        if (proxies == null || proxies.size() <= 0)
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
            catch (InterruptedException ie)
            {            	
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try 
        {
        	unregisterChannelAdapter();
        }
        catch (Exception e) {}
        try 
        {
        	channels.cleanUp();
        }
        catch (Exception e) {}
        clearQueue();
	    clearChannelEventCache();
        threadPool = null;
    	Log.information("ConcurrentEventChannelAdapter thread finished. Thread=" + getName());                
    }

    public int getQueueSize()
    {
        return queue.getQueueSize();
    }

    public int getMaxQueueSize()
    {
        return queue.getMaxQueueSize();
    }
    

    protected ThreadPool getThreadPool()
    {
    	return threadPool;
    }

    protected ChannelListenerProxy getListenerProxy(ChannelListener listener)
    {
        return new ConcurrentEventChannelListenerProxy((EventChannelListener)listener, this, threadPool);
    	
    }
    
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
