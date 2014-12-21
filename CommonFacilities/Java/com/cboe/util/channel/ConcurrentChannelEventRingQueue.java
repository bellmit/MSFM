package com.cboe.util.channel;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Created by IntelliJ IDEA.
 * User: lowery
 * Date: Aug 18, 2006
 * Time: 5:12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConcurrentChannelEventRingQueue
{
    protected ConcurrentLinkedQueue<ChannelEvent> queue;
//    protected ChannelEvent queue[];
        protected boolean shuttingDown;
        protected int capacity;
        protected int capacityIncrement;
        protected AtomicInteger currentCount;
        protected AtomicInteger maxCount;
        protected int mask; // Used for wrapping around end of array
        protected int head; // Next place to retrieve a command from
        protected int tail; // Next place to put a command into
        // If head == tail, the queue is either full or empty. That is
        // why we keep the currentCount.


        /**
         * Constructor
         */
        public ConcurrentChannelEventRingQueue()
        {
            currentCount = new AtomicInteger(0);
            maxCount = new AtomicInteger(0);
            capacityIncrement = 1 << 10;
            initializeQueue();
            shuttingDown = false;
        }

        /**
         * Adds a new command to the queue
         *
         */
        public void insertEvent( ChannelEvent event )
        {
            queue.add(event);
            if(currentCount.incrementAndGet() > maxCount.get())
            {
                maxCount.set(currentCount.get());
            }
        }

        /**
         * This method will get the next available command.
         *  If there are no commands available, wait for one.
         *  If we are shutting down, return null
         * @author Craig Murphy
         * @return ThreadCommand the command to be executed.
         */
        public ChannelEvent getNextEvent()
        {
            if(null != queue.peek())
            {
                currentCount.decrementAndGet();
            }
            return queue.poll();
        }

        public Enumeration clearQueue()
        {
            Enumeration clearedCommands = new QueueEnumerator( queue );
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
            return null == queue.peek();
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
        protected class QueueEnumerator implements Enumeration
        {
            private Iterator iter = null;

            public QueueEnumerator(ConcurrentLinkedQueue<ChannelEvent> queue)
            {
                iter = queue.iterator();
            }

            public boolean hasMoreElements()
            {
                return iter.hasNext();
            }

            public Object nextElement()
            {
                return iter.next();
            }

        }
}
