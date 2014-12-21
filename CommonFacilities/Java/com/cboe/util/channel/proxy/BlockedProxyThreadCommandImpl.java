/**
 * 
 */
package com.cboe.util.channel.proxy;

//import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelEventRingQueue;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.event.ChannelEventBlocker;

/**
 * This class extends ProxyThreadCommandImpl to implement blocking.
 * Added as part of CSI project.
 * 
 * @author Gijo Joseph
 *
 */
public class BlockedProxyThreadCommandImpl extends ProxyThreadCommandImpl {

    // max number of events that can go in a block.
//    protected final static int MAX_BLOCK_EVENTS = 30;
    
    private ChannelEventBlocker eventBlocker;
    private boolean blockWhenSizeIsOne = false;
    
    // FOR DEBUG/TESTING -- start
//    private int sleepTime = 0;
    // FOR DEBUG/TESTING -- end
	
    public BlockedProxyThreadCommandImpl(ChannelListenerProxy proxy)
    {
        super(proxy);
        try 
        {
        	this.blockWhenSizeIsOne = Boolean.parseBoolean(System.getProperty("blockWhenSizeIsOne"));
        	
//        	this.sleepTime = Integer.parseInt(System.getProperty("sleepTimeBeforeBlocking")); // FOR TESTING ONLY
        }
        catch (Exception e)
        {        	
        }
    }

    public void setEventBlocker(ChannelEventBlocker eventBlocker)
    {
    	this.eventBlocker = eventBlocker;    
    }
    
    
    public void execute()
    {
    	
        ChannelEventRingQueue eventQueue;
        ChannelListenerProxy theProxy;
        boolean blocked = false;

        // FOR DEBUG/TESTING -- start

//        if (sleepTime > 0)
//        {
//        	try 
//        	{
//        		Thread.currentThread().sleep(sleepTime);
//        	}
//        	catch (InterruptedException ie)
//        	{        		
//        	}
//        }
        // FOR DEBUG/TESTING -- end

        synchronized(this)
        {
            eventQueue = this.eventQueue;
            theProxy = this.theProxy;
        }
        if (!released && eventQueue != null && theProxy != null)
        {
            int blockSize = 0;
            ChannelEvent event = null;
    		ChannelEvent events[] = null; // temp

            synchronized(this) {
            	// the blockSize will be the min of (max event in the queue, max block size permitted).
            	blockSize = (eventBlocker.getMaxBlockSize() < eventQueue.getQueueSize()) ? eventBlocker.getMaxBlockSize() : eventQueue.getQueueSize();
            	if (blockSize < 1) // safety check; nothing in queue
            		return;
        		events = new ChannelEvent[blockSize];
        		for (int i=0; i < blockSize; i++)
        		{
        			events[i] = eventQueue.getNextEvent();
        		}
            }
        	if (blockSize > 1 || blockWhenSizeIsOne) 
        	{
        		try 
        		{
        			event = eventBlocker.blockEvents(events);
        			blocked = true;
        		}
        		catch (Exception e)
        		{
        			e.printStackTrace();
//            			Log.information("Unable to block the events!");
//            			Log.exception(e);
        		}
        	}
            if (blocked)
            {
            	notifyEvent(theProxy, event);            
            	for (int i=0; i < blockSize; i++)
            	{
            		events[i].release();
            	}
            }
            else
            {
            	// This case is mainly for the blockSize=1. 
        		// It will also handle a case when the block attempt fails for any reason (very unlikely though)! 
            	for (int i=0; i < blockSize; i++)
            	{
            		notifyEvent(theProxy, events[i]);
            	}            		
            }
        }        
    }
    
    private void notifyEvent(ChannelListenerProxy theProxy, ChannelEvent event)
    {
    	if (event == null)
    		return;
        try
        {
            theProxy.channelUpdate(event);
        }
        catch(Exception e)
        {
            ChannelAdapter adapter = theProxy.getChannelAdapter();
            if (adapter != null)
            {
                if (adapter.isListenerCleanup())
                {
                    e.printStackTrace();
                    adapter.removeChannelListener(theProxy.getDelegateListener());
                }
                else
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                throw new RuntimeException(e);
            }
        }
        finally
        {
        	event.release();
        }
    }
}
