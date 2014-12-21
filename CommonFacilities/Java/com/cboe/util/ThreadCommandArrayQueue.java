package com.cboe.util;

/**
 * This class implements a command queue for ThreadCommands
 * Note that it has package access.
 *
 * @author Craig Murphy
 */

import java.util.Enumeration;

class ThreadCommandArrayQueue
{
    private ThreadCommand queue[];
    private int capacity;
    private int capacityIncrement;
    private int currentCount;


    /**
     * Constructor
     */
    public ThreadCommandArrayQueue( )
    {
        capacityIncrement = 1024;
        initializeQueue();
    }

    /**
     * Tells whether there is anything on the queue or not
     * @return true == nothing on queue
     */
    public boolean isEmpty()
    {
        return 0 == currentCount;
    }
    /**
     * Adds a new command to the queue, placing it in the proper place
     * for its priority.
     *
     * @param ThreadCommand command  -- the command to place in the queue
     */
    public void insertCommand( ThreadCommand command )
    {
        if ( currentCount == capacity )
        {
            ThreadCommand[] oldQueue = queue;
            queue = new ThreadCommand[ capacity += capacityIncrement ];
            System.arraycopy( oldQueue, 0, queue, 0, currentCount );
        }
        queue[ currentCount ] = command;
        currentCount++;
    }
    /**
     * This method will get the next available command.  
     * If there are no commands waiting, the method returns null.
     * @author David Wegener
     * @author Craig Murphy
     * @return ThreadCommand the command to be executed.
     */
    public ThreadCommand getNextCommand() 
    {
        ThreadCommand retVal = null;
        if ( 0 < currentCount )
        {
            int index;
            for ( index = 0; index < currentCount; index++ )
            {
                try
                {
                    if ( queue[ index ].isAvailable() )
                    {
                        break;
                    }
                }
                catch ( Throwable t )
                {
                    System.err.println( "Received throwable getting next command: " );
                    t.printStackTrace( System.err );
                     int j = currentCount - index - 1;
                     if ( 0 < j ) 
                     {
                          System.arraycopy(queue, index + 1, queue, index, j);
                     }
                     currentCount--;
                     queue[ currentCount ] = null; /* to let gc do its work */
                     index--; // Recheck the current position
                }
            }
            if ( index < currentCount )
            {
                retVal = queue[ index ];
                int j = currentCount - index - 1;
                if ( 0 < j ) 
                {
                    System.arraycopy(queue, index + 1, queue, index, j);
                }
                currentCount--;
                queue[ currentCount ] = null; /* to let gc do its work */
            }
        }
        return retVal;
    }

    public Enumeration clearQueue()
    {
        Enumeration clearedCommands = new QueueEnumerator( queue, currentCount );
        initializeQueue();
        return clearedCommands;
    }

    /**
     * initializes queue to an empty state
     */
    private void initializeQueue()
    {
        queue = new ThreadCommand[ capacity = capacityIncrement ];
        currentCount = 0;
    }
    synchronized int getQueueSize()
    {
        return currentCount;
    }
    /**
     * Create a class for generating an enumeration of the data
     */
    private class QueueEnumerator implements Enumeration
    {
        private ThreadCommand[] data;
        private int index;
        private int elementCount;

        QueueEnumerator( ThreadCommand[] data, int elementCount )
        {
            this.data = data;
            this.elementCount = elementCount;
            index = 0;
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
