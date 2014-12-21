package com.cboe.domain.iec;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.cboe.util.channel.ChannelEvent;

/**
 * ConcurrentQueue provides concurrent insert and get operations
 * by making use of ConcurrentLinkedQueue class. Since the latter 
 * does not provide a constant time size operation, this class keeps two 
 * AtomicIntegers to keep track of the current count and maximum count. 
 * 
 * The counters currentCount and maxCount cannot guarantee 100% accuracy due to 
 * concurrency related limitations. That is acceptable in this case as these are 
 * statistics counters.
 * 
 * @author josephg
 *
 */
public class ConcurrentQueue
{
    protected ConcurrentLinkedQueue<ChannelEvent> queue;
    protected AtomicInteger currentCount;
    protected AtomicInteger maxCount;


    /**
     * Constructor
     */
    public ConcurrentQueue()
    {
        currentCount = new AtomicInteger(0);
        maxCount = new AtomicInteger(0);
        initializeQueue();
    }

    /**
     * Adds a new event to the queue.
     * Also updates the counter(s).
     *
     * @param ChannelEvent event  -- the event to be placed in the queue
     * @return ChannelEvent -- the size of the queue at or around the time of insert. 
     */
    public int insertEvent( ChannelEvent event )
    {
    	// since the size is maintained separately by the currentCount and that is not synchronized with queue add/delete,
    	// there is always a possibility that the size of the queue could be out of synch momentarily. 
    	// Also, with multiple threads calling dispatch, the return value from the method need not be the size at the exact 
    	// time insert into the queue.     	
        queue.add(event);
        int cnt;
        if ((cnt = currentCount.incrementAndGet()) > maxCount.get())
        {
            maxCount.set(cnt);
        }
        return cnt;
    }

    /**
     * This method will get the next available ChannelEvent.
     * 
     * @return ChannelEvent -- the head of the queue if there are elements; else null.
     */
    public ChannelEvent getNextEvent()
    {
    	// Be very careful here. We're assuming a single dequeuer so this
    	// actually works. If not, you would have to synchronize on the queue
    	// to make it thread safe. With this implementation the count isn't
    	// managed by the queue itself. We're doing that artificially and 
    	// there are some potential problems lurking with multiple dequeuers. 
    	
    	ChannelEvent event = null;
        if(null != queue.peek())
        {
            currentCount.decrementAndGet();
            event = queue.poll();
        }
        return event;
    }

    public Enumeration<ChannelEvent> clearQueue()
    {
        Enumeration<ChannelEvent> clearedCommands = new QueueEnumerator<ChannelEvent>( queue );
        initializeQueue();
        return clearedCommands;
    }

    /**
     * initializes queue to an empty state
     */
    private void initializeQueue()
    {
        queue = new ConcurrentLinkedQueue<ChannelEvent>();
        currentCount.set(0);
    }
    
    public boolean isEmpty()
    {
        return queue.peek() == null;
    }
    
	public int getQueueSize()
	{
        return currentCount.get();
	}

    public int getMaxQueueSize()
    {
        return maxCount.get();
    }

    /**
     * Create a class for generating an enumeration of the data
     */
    protected class QueueEnumerator<T> implements Enumeration<T>
    {
        private Iterator<T> iter = null;

        public QueueEnumerator(ConcurrentLinkedQueue<T> queue)
        {
            iter = queue.iterator();
        }

        public boolean hasMoreElements()
        {
            return iter.hasNext();
        }

        public T nextElement()
        {
            return iter.next();
        }
    }
}
