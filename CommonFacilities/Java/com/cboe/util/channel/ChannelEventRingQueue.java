package com.cboe.util.channel;

/**
 * This class implements a fifo queue for ThreadCommands
 * Note that it has package access.
 *
 * This version makes use of a fifo. By using a ring buffer
 * insertions and deletions are O(1), however, it does not support
 * priorities.
 *
 * @author Craig Murphy
 */

import java.util.Enumeration;

public class ChannelEventRingQueue
{
    protected ChannelEvent queue[];
    protected boolean shuttingDown;
    protected int capacity;
    protected int capacityIncrement;
    protected int currentCount;
    protected int maxCount;
    protected int mask; // Used for wrapping around end of array
    protected int head; // Next place to retrieve a command from
    protected int tail; // Next place to put a command into
    // If head == tail, the queue is either full or empty. That is
    // why we keep the currentCount.


    /**
     * Constructor
     */
    public ChannelEventRingQueue()
    {
        capacityIncrement = 1 << 10;
        initializeQueue();
        shuttingDown = false;
    }

    /**
     * Adds a new command to the queue
     *
     * @param ThreadCommand command  -- the command to place in the queue
     */
    public void insertEvent( ChannelEvent event )
    {
        if ( currentCount == capacity )
        {
            int newCapacity = capacity << 1;
            ChannelEvent newQueue[] = new ChannelEvent[ newCapacity ];
            int elementsAtHead = capacity - head;
            // Assert head == tail
            // Assert 0 != elementsAtHead
            System.arraycopy( queue,           // source
                              head,            // source position
                              newQueue,        // destination
                              0,               // destination offset
                              elementsAtHead   // length
                             );
            if ( 0 != tail )
            {
                System.arraycopy( queue,           // source
                                  0,               // source position
                                  newQueue,        // destination
                                  elementsAtHead,  // destination offset
                                  tail             // length
                                 );
            }
            queue = newQueue;
            head = 0;
            tail = capacity;
            capacity = newCapacity;
            mask = capacity - 1;
        }
        queue[ tail++ ] = event;
        tail &= mask;
        currentCount++;
        if (currentCount > maxCount)
        {
            maxCount = currentCount;
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
        ChannelEvent retVal = null;
        if ( 0 < currentCount )
        {
            retVal = queue[ head ];
            queue[ head++ ] = null; // give GC a chance to work
            head &= mask;
            currentCount--;
        }
        return retVal;
    }

    public Enumeration clearQueue()
    {
        Enumeration clearedCommands = new QueueEnumerator( queue, currentCount, capacity, head, tail );
        initializeQueue();
        return clearedCommands;
    }
    /**
     * initializes queue to an empty state
     */
    private void initializeQueue()
    {
        queue = new ChannelEvent[ capacity = capacityIncrement ];
        mask = capacity - 1;
        head = tail = 0;
        currentCount = 0;
    }
	 public boolean isEmpty()
	 {
		return 0 == currentCount;
	}
	public int getQueueSize()
	{
		return currentCount;
	}

    public int getMaxQueueSize()
    {
        return maxCount;
    }
    /**
     * Create a class for generating an enumeration of the data
     */
    protected class QueueEnumerator implements Enumeration
    {
        private ChannelEvent[] data;
        private int index;
        private int elementCount;

        public QueueEnumerator( ChannelEvent[] queue, int elementCount, int capacity, int head, int tail )
        {
            data = new ChannelEvent[ elementCount ];
            this.elementCount = elementCount;
            index = 0;
            if ( 0 < elementCount )
            {
                if ( head < tail ) // The current queue hasn't wrapped around
                {
                    // Assert: elementCount = tail - head
                    System.arraycopy( queue,           // source
                                      head,            // source position
                                      data,            // destination
                                      0,               // destination offset
                                      elementCount     // length
                                     );
                }
                else
                {
                    // Assert: elementCount = tail - head + tail
                    int elementsAtHead = capacity - head;
                    System.arraycopy( queue,           // source
                                      head,            // source position
                                      data,            // destination
                                      0,               // destination offset
                                      elementsAtHead   // length
                                     );
                    if ( 0 != tail )
                    {
                        System.arraycopy( queue,           // source
                                          0,               // source position
                                          data,            // destination
                                          elementsAtHead,  // destination offset
                                          tail             // length
                                         );
                    }
                }
            }
        }
        public boolean hasMoreElements()
        {
            return index < elementCount;
        }
        public Object nextElement()
        {
            return data[ index++ ];
        }
    }
}
