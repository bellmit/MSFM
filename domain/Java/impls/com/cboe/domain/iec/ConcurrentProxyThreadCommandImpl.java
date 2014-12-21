package com.cboe.domain.iec;

import java.util.Enumeration;

import com.cboe.util.InvalidThreadPoolStateException;
import com.cboe.util.ThreadCommand;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.proxy.ProxyThreadCommand;

public class ConcurrentProxyThreadCommandImpl extends ThreadCommand implements ProxyThreadCommand 
{
    // thread command state values.
    protected final static int SCHEDULED = 1;
    protected final static int IDLE = 2;

    // max number of events to fire in a single schedule.
    // (NOTE: This may become a configurable value.
    protected final static int FIRE_EVENT_MAX = 100;

    // local state flag signifies executing state.  Allows the thread command to
    // schedule itself with the thread command when necessary.
    private volatile int state;
    protected volatile boolean released = false;

    // the ThreadPool this thread command should schedule itself with.
    protected ThreadPool threadPool;

    // the event queue for this listener
    protected ConcurrentQueue eventQueue;

    // the listener proxy object the ChannelThreadCommand communicates through.
    protected ChannelListenerProxy proxy = null;

	
    protected ConcurrentProxyThreadCommandImpl()
    {
        super();
        state = IDLE;
    }

    public ConcurrentProxyThreadCommandImpl(ChannelListenerProxy proxy)
    {
        super();

        this.proxy = proxy;
        state = IDLE;
        eventQueue = new ConcurrentQueue();
    }


	/**
	 * This method is called when the command completes.  It will
	 * always be called regardless of whether the execute command threw
	 * an exception or not.
	 * @author David Wegener
	 */
	protected void complete()
	{
        if (!released && eventQueue.getQueueSize() > 0)
        {
            try
            {
                state = SCHEDULED;
                threadPool.schedule(this);
            }
            catch (InvalidThreadPoolStateException e)
            {
            	state = IDLE; // change the state back to IDLE
                throw new RuntimeException(e.getMessage());
            }
            catch (Exception e2)
            {
            	// catching all exception here to not to leave the state at SCHEDULED.
            	state = IDLE; // change the state back to IDLE
            }
        }
        else
        {
            state = IDLE;
        }		
	}
	
	/**
	   This is the abstract method that ThreadCommand
	   subclasses must implement to carry out the execution
	   of the command.
	   @author David Wegener
	 */
	public void execute()
	{
        if (!released && eventQueue != null && proxy != null)
        {
            /*
             * To prevent thread manipulation by heavily published listeners
             * only fire FIRE_EVENT_MAX events to the listener in a single
             * thread schedule.  If more events are in the queue the complete
             * method will reschedule the thread command.
             */
            ChannelEvent event;
            int countFire = eventQueue.getQueueSize();
           	if (countFire > FIRE_EVENT_MAX)
           	{
           		countFire = FIRE_EVENT_MAX;
           	}

            while (countFire-- > 0)
            {
                event = eventQueue.getNextEvent();

                if (event != null)
                {
                    try
                    {
                        proxy.channelUpdate(event);
                    }
                    catch(Exception e)
                    {
                        ChannelAdapter adapter = proxy.getChannelAdapter();
                        if (adapter != null)
                        {
                            if (adapter.isListenerCleanup())
                            {
                                e.printStackTrace();
                                adapter.removeChannelListener(proxy.getDelegateListener());
                            }
                            else
                            {
                                throw new RuntimeException(e.toString());
                            }
                        }
                        else
                        {
                            e.printStackTrace();
                            throw new RuntimeException(e.toString());
                        }
                    }
                    finally
                    {
                        event.release();
                    }
                }
                else
                {
                    // Someone else dispatched all the events! Shouldn't be possible, but let's be sure...
                    countFire = 0;
                }
            }
        }
    }
	
    /**
     * Sets the reference of the threadPool that this thread command should
     * schedule itself with.
     *
     * @param threadPool the ThreadPool reference.
     */
    public void setThreadPool(ThreadPool threadPool)
    {
    	this.threadPool = threadPool;
    }

    public void release()
    {
    	if (!released)
    	{
	        released = true;
	        if(eventQueue != null) {
	            int queueSize = eventQueue.getQueueSize();
	            while (queueSize-- > 0) {
	                ChannelEvent event = eventQueue.getNextEvent();
	                event.release();
	            }
	            eventQueue = null;
	        }
	        threadPool = null;
	        proxy = null;
    	}
    }

    /**
     * Returns whether the thread pool has been set for this thread command
     */
    public boolean isThreadPool()
    {
        return threadPool != null;
    }

    /**
     * This method adds the passed ChannelEvent to the ChannelThreadCommand's
     * event queue.  This will assure serialized delivery of events to the associated
     * EventChannelListener.
     * @param event the ChannelEvent to send to the registered listener.
     */
    public void addEvent(ChannelEvent event)
    {
        if (!released && event != null)
        {
            eventQueue.insertEvent(event);

            if (state == IDLE)
            {
                try
                {
                    state = SCHEDULED;
                    threadPool.schedule(this);
                }
                catch (InvalidThreadPoolStateException e)
                {
                	state = IDLE; // change the state back to IDLE
                    throw new RuntimeException(e.getMessage());
                }
                catch (Exception e2)
                {
                	// catching all exception here to not to leave the state at SCHEDULED.
                	state = IDLE; // change the state back to IDLE
                }
            }
        }
        else
        {
            throw new NullPointerException("null ChannelEvent received");
        }    	
    }

    public int getQueueSize()
    {
    	if (!released)
    		return eventQueue.getQueueSize();
    	else
    		return 0;
    }

    public int getMaxQueueSize()
    {
    	if (!released)
    		return eventQueue.getMaxQueueSize();
    	else
    		return 0;    	
    }

    /** Empty the queue of ChannelEvent objects.
     *
     * @return ChannelEvent objects that were in the queue before this call.
     */
    public Enumeration flushQueue()
    {
    	return eventQueue.clearQueue();
    }

}
