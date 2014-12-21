/**
 * The ChannelThreadCommand class contains the logic a worker thread will
 * execute to update a ChannelListener through its proxy object.
 *
 * @author Derek T. Chambers-Boucher
 * @version 04/18/1999
 */
package com.cboe.util.channel.proxy;

import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.ChannelEventRingQueue;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.ThreadCommand;
import com.cboe.util.InvalidThreadPoolStateException;
import java.util.Enumeration;

public class ProxyThreadCommandImpl extends ThreadCommand implements ProxyThreadCommand
{
    // thread command state values.
    protected final static int SCHEDULED = 1;
    protected final static int IDLE = 2;

    // max number of events to fire in a single schedule.
    // (NOTE: This may become a configurable value.
    protected final static int FIRE_EVENT_MAX = 50;

    // local state flag signifies executing state.  Allows the thread command to
    // schedule itself with the thread command when necessary.
    protected int state;
    protected boolean released = false;

    // the ThreadPool this thread command should schedule itself with.
    protected ThreadPool threadPool;

    // the event queue for this listener
    protected ChannelEventRingQueue eventQueue;

    // the listener proxy object the ChannelThreadCommand communicates through.
    protected ChannelListenerProxy theProxy = null;

    public ProxyThreadCommandImpl()
    {
        super();
        state = IDLE;
        eventQueue = new ChannelEventRingQueue();
    }

    /**
     * This constructor calls the ThreadCommand constructor passing the proxy
     * object to associate with this thread command.
     *
     * @param proxy the ChannelListenerProxy this thread command will communicate
     * through.
     */
    public ProxyThreadCommandImpl(ChannelListenerProxy proxy)
    {
        super();

        theProxy = proxy;
        state = IDLE;
        eventQueue = new ChannelEventRingQueue();
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


    /**
     * Returns whether the thread pool has been set for this thread command
     */
    public boolean isThreadPool()
    {
        return threadPool != null;
    }

    public int getQueueSize()
    {
        return eventQueue.getQueueSize();
    }

    public int getMaxQueueSize()
    {
        return eventQueue.getMaxQueueSize();
    }

    /** Empty the queue of ChannelEvent objects.
     *
     * @return ChannelEvent objects that were in the queue before this call.
     */
    public synchronized Enumeration flushQueue()
    {
        return eventQueue.clearQueue();
    }

    public synchronized void release()
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
        theProxy = null;
    }

    /**
     * This method adds the passed ChannelEvent to the ChannelThreadCommand's
     * event queue.  This will assure serialized delivery of events to the associated
     * EventChannelListener.
     *
     * @param event the ChannelEvent to send to the registered listener.
     */
    synchronized public void addEvent(ChannelEvent event)
    {
        if (!released && event != null)
        {
            eventQueue.insertEvent(event);

            if (state == IDLE)
            {
                try
                {
                    threadPool.schedule(this);
                    state = SCHEDULED;
                }
                catch (InvalidThreadPoolStateException e)
                {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        else
        {
            throw new NullPointerException("null ChannelEvent received");
        }
    }

    /**
     * Check to make sure the event queue is really empty (i.e. no events
     * are still in the queue after calling the last <code>channelUpdate()<\code>.
     * If events are still in the queue the thread command is rescheduled.
     */
    synchronized protected void complete()
    {
        if (!released && eventQueue.getQueueSize() > 0)
        {
            try
            {
                threadPool.schedule(this);
                state = SCHEDULED;
            }
            catch (InvalidThreadPoolStateException e)
            {
                throw new RuntimeException(e.getMessage());
            }
        }
        else
        {
            state = IDLE;
        }
    }

    /**
     * This method contains the actual work the worker thread will carry out.
     */
    public void execute()
    {
        ChannelEventRingQueue eventQueue;
        ChannelListenerProxy theProxy;

        synchronized(this)
        {
            eventQueue = this.eventQueue;
            theProxy = this.theProxy;
        }
        if (!released && eventQueue != null && theProxy != null)
        {
            /*
             * To prevent thread manipulation by heavily published listeners
             * only fire FIRE_EVENT_MAX events to the listener in a single
             * thread schedule.  If more events are in the queue the complete
             * method will reschedule the thread command.
             */
            int countFire;
            ChannelEvent event;

            synchronized(this) {
                countFire = (FIRE_EVENT_MAX < eventQueue.getQueueSize()) ? FIRE_EVENT_MAX : eventQueue.getQueueSize();
            }

            while (countFire-- > 0)
            {
                synchronized(this) {
                    event = eventQueue.getNextEvent();
                }

                if (event != null)
                {
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
                else
                {
                    // Someone else dispatched all the events! Shouldn't be possible, but let's be sure...
                    countFire = 0;
                }
            }
        }
    }
}
