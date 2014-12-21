/**
 * InterThreadQueue.java
 *
 * Created on February 26, 2002, 11:47 AM
 */

package com.cboe.lwt.queue;


import java.util.TimerTask;

import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.lwt.eventLog.Logger;


/**
 * Simple Inter-Thread Communication (ITC) construct for sending and receiving
 * objects between threads.
 *
 * This object <B>is Thread-Safe</B>, and requires no external synchronization
 *
 * @author dotyl
 */
public final class InterThreadPriorityQueue
{
    public static final int MIN_NUMBLOCKS    = 18;
    public static final int INFINITE_TIMEOUT = -1;
    public static final int NO_TIMEOUT       = 0;
   
    
    class FlushTask extends TimerTask
    {
        public void run()
        {
            flush();
        }
    };

    
    private int blockSize;  // how big is each sub-queue in the ITC

    // NOTE: the reason there are 2 discrete queues is to decrease contention between threads
    private PriorityQueue   inQueue;
    private FixedQueue      outQueue;
    private CircularQueue[] midQueues;
    private CircularQueue   queuePool;
    private int             numBlocks;
    private int             numPriorities;
    private int             numElementsInMidQueues;

    private int defaultTimeout;

    // these need to be separate objects because the objects they monitor (inQueue/outQueue)
    // are being flipped (double-buffer idiom)
    private Object inQueueMonitor;   // synchronization object for the logical input queue
    private Object outQueueMonitor;  // synchronization object for the logical output queue
    private Object midQueueMonitor;  // synchronization object for the midqueues

    String baseName;


    // metrics
    private QueueInstrumentor instrumentor;

    /* private constructor accessible only to static factory methods */
    private InterThreadPriorityQueue( String p_baseName,
                                      int    p_blockSize,
                                      int    p_numBlocks,
                                      int    p_numPriorities,
                                      int    p_flushInterval_MS )
    {
        baseName       = p_baseName;
        blockSize      = p_blockSize;
        numBlocks      = p_numBlocks;
        numPriorities  = p_numPriorities;
        defaultTimeout = INFINITE_TIMEOUT;

        if ( p_numBlocks < MIN_NUMBLOCKS )
        {
            Logger.error( "InterThreadQueue: " + p_numBlocks + " is less than minimum p_numBlocks of " + MIN_NUMBLOCKS + "... resetting p_numBlocks to " + MIN_NUMBLOCKS );
            p_numBlocks = MIN_NUMBLOCKS;
        }

        int midQueueSize = p_numBlocks - 2;

        queuePool = new CircularQueue( p_numBlocks );
        for ( int i = 0; i < queuePool.getCapacity(); ++i )
        {
            queuePool.enqueue( new FixedQueue( blockSize ) );
        }
        inQueue   = new PriorityQueue( blockSize, numPriorities );
        outQueue  = getEmptyQueue();
        midQueues = new CircularQueue[ p_numPriorities ];
        for ( int i = 0; i < p_numPriorities; ++i )
        {
            midQueues[ i ] = new CircularQueue( midQueueSize );
        }

        numElementsInMidQueues = 0;

        // monitors
        inQueueMonitor  = new Object();
        outQueueMonitor = new Object();
        midQueueMonitor = midQueues;

        instrumentor = InstrumentorHome.getQueueInstrumentor( p_baseName, null );
        
        instrumentor.setEnqueueLockObject( inQueueMonitor );
        instrumentor.setLockObject       ( midQueueMonitor );
        instrumentor.setDequeueLockObject( outQueueMonitor );
        
        if ( p_flushInterval_MS > 0 )
        {
            InterThreadQueue.flushTimer.scheduleAtFixedRate( new FlushTask(), p_flushInterval_MS, p_flushInterval_MS );
        }
    }


    public final static InterThreadPriorityQueue getInstance( String p_baseName, 
                                                              int    p_numPriorities )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new InterThreadPriorityQueue( p_baseName, 32, 32, p_numPriorities, 0 );
    }



    public final static InterThreadPriorityQueue getInstance( String p_baseName,
                                                              int    p_blockSize,
                                                              int    p_numBlocks,
                                                              int    p_numPriorities )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new InterThreadPriorityQueue( p_baseName, 
                                             p_blockSize,
                                             p_numBlocks,
                                             p_numPriorities, 
                                             0 );
    }


    public final static InterThreadPriorityQueue getInstance( String p_baseName,
                                                              int    p_blockSize,
                                                              int    p_numBlocks,
                                                              int    p_numPriorities, 
                                                              int    p_flushInterval_MS )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new InterThreadPriorityQueue( p_baseName, p_blockSize, p_numBlocks, p_numBlocks, p_flushInterval_MS );
    }
    
    
    /**
     * Enqueues an object
     *
     * @param p_obj The object to be written to the stream
     */
    public final void enqueue( Object p_obj, int p_priority )
        throws QueueException
    {
        enqueue( p_obj, p_priority, defaultTimeout );
    }


    /** Add the given <code>p_obj</code> to the rear of the queue.  If the queue is
     * full, then wait for up to <code>p_timeout_MS</code> milliseconds for the queue to
     * become non-full.
     *
     * If the queue is in wait mode and the operation cannot complete in the specified (DEFAULT VALAUE)
     * amount of time an exception is thrown.
     *
     * @param p_obj - the object to enqueue.
     * @param p_timeout_MS - maximum wait time to use, if the queue is currently full.
     *
     * @exception QueueFullException if the queue is already full and the timeout expires.
     * @exception QueueInterruptedException Queue operation interrupted.
     * @exception QueueException serious errors.
     *
     */
    public final void enqueue( Object  p_obj, int p_priority, int p_timeout_MS )
        throws QueueException
    {
        long remainingTimeout = p_timeout_MS;
     
        try
        {
            while ( true )
            {
                synchronized ( inQueueMonitor )
                {
                    if ( ! inQueue.isFull( p_priority ) )
                    {   
                        inQueue.enqueue( p_obj, p_priority );  // enqueue into the inQueue
                        return;
                    }
                    
                    synchronized ( midQueueMonitor )
                    {
                        pushDataToMidQueues();
        
                        if ( ! inQueue.isFull( p_priority ) )
                        {   
                            inQueue.enqueue( p_obj, p_priority );  // enqueue into the inQueue
                            return;
                        }
        
                        if ( p_timeout_MS == INFINITE_TIMEOUT )
                        {
                            midQueueMonitor.notify();
                            midQueueMonitor.wait( 2000 );  // allow for lock release every 2 seconds
                            continue;
                        }
                    
                        if ( remainingTimeout <= NO_TIMEOUT )
                        {
                            throw new QueueFullException( "Enqueue timed out on full queue" );
                        }
    
                        long waitStartTimeout = System.currentTimeMillis();
                        midQueueMonitor.notify();
                        midQueueMonitor.wait( remainingTimeout );  
                        
                        remainingTimeout += waitStartTimeout;
                        remainingTimeout -= System.currentTimeMillis();
                    }
                }
            }
        }
        catch ( InterruptedException ex )
        {
            throw new QueueInterruptedException( "Interrupted during enqueue", ex );
        }
    }


    /**
     * dequeues an object
     *
     * If no objects are available, the call blocks until an object becoms available
     *
     * @return The next object in the stream
     *
     */
    public final Object  dequeue()
        throws QueueInterruptedException
    {
        Object  result = dequeue( defaultTimeout );

        return result;
    }


    /** Dequeue the front element of the queue and return it's data object to the caller.
     * If the queue is empty, then wait for <code>p_timeout_MS</code> milliseconds or for
     * an item to be enqueued.
     *
     * If the queue is in wait mode and the operation cannot complete in the specified
     * amount of time a NULL value will be returned to the caller.
     *
     * NOTE: The dequeue operation may not wait for the complete specified amount of time IF
     *          there are multiple readers and the queue is in wait mode with a finite wait-time.
     *
     * @param p_timeout_MS int specifying the maximum time to wait for the queue to become non-empty.
     * @return Object  the data object of the front element in the queue, or null if the queue was empty
     *                   after the given wait period.
     * @exception QueueInterruptedException Queue operation interrupted.
     *
     */
    public final Object  dequeue( int p_timeout_MS )
        throws QueueInterruptedException
    {
        synchronized ( outQueueMonitor )
        {
            if ( ! outQueue.isEmpty() )
            {
                return outQueue.dequeue();
            }

            if ( p_timeout_MS != INFINITE_TIMEOUT )
            {
                getDataFromMidQueues( p_timeout_MS );

                if ( ! outQueue.isEmpty() )
                {
                    return outQueue.dequeue();
                }

                return null;
            }
        }

        // if here, then infinite timeout  (this is separate because must release outQueueMonitor so clear can work)
        assert ( p_timeout_MS == INFINITE_TIMEOUT ) : "Programming error: reached this code without infinite timeout";

        while( true )
        {
            synchronized ( outQueueMonitor )
            {
                if ( ! outQueue.isEmpty() )
                {
                    return outQueue.dequeue();
                }

                getDataFromMidQueues( 2000 );
            }
        }
    }


    /** Remove the front element from the queue and return it's data object to the caller.
     * If the queue is in wait mode and the operation cannot complete in the specified (DEFAULT VALAUE)
     * amount of time a NULL value will be returned to the caller.
     *
     * NOTE: The dequeue operation may not wait for the complete specified amount of time IF
     *          there are multiple readers and the queue is in wait mode with a finite wait-time.
     *
     * @return Object[] The 'n' data object of the topmost queue element, or <code>null</code> if
     *                 the queue is empty.
     *
     * @exception QueueInterruptedException Queue operation interrupted.
     *
     */
    public final FixedQueue dequeueMultiple()
        throws QueueInterruptedException
    {
        return dequeueMultiple( defaultTimeout );
    }


    /** Remove the front element from the queue and return it's data object to the caller.
     * If the queue is in wait mode and the operation cannot complete in the specified (DEFAULT VALAUE)
     * amount of time a NULL value will be returned to the caller.
     *
     * NOTE: The dequeue operation may not wait for the complete specified amount of time IF
     *          there are multiple readers and the queue is in wait mode with a finite wait-time.
     *
     * @param p_timeout_MS int specifying the maximum time to wait for the queue to become non-empty.
     * @return Object  The 'n' data object of the topmost queue element, or <code>null</code> if
     *                 the queue is empty.
     *
     * @exception QueueInterruptedException Queue operation interrupted.
     *
     */
    public final FixedQueue dequeueMultiple( int p_timeout_MS )
        throws QueueInterruptedException 
    {
        synchronized ( outQueueMonitor )
        {
            if ( ! outQueue.isEmpty() )
            {
                return extractOutQueue();
            }

            if ( p_timeout_MS != INFINITE_TIMEOUT )
            {
                getDataFromMidQueues( p_timeout_MS );

                if ( ! outQueue.isEmpty() )
                {
                    return extractOutQueue();
                }

                return null;
            }
        }

        // if here, then infinite timeout  (this is separate because must release outQueueMonitor so clear can work)
        assert ( p_timeout_MS == INFINITE_TIMEOUT ) : "Programming error: reached this code without infinite timeout";

        while( true )
        {
            synchronized ( outQueueMonitor )
            {
                getDataFromMidQueues( 2000 );

                if ( ! outQueue.isEmpty() )
                {
                    return extractOutQueue();
                }
            }

            try
            {
                Thread.sleep( 1 );
            }
            catch (InterruptedException e)
            {
                throw new QueueInterruptedException();
            }
        }
    }
    
    
    private FixedQueue extractOutQueue()
    {
        FixedQueue result = outQueue;
        
        outQueue = getEmptyQueue();

        return result;
    }


    /**  Return the data object of the front of the queue without affecting the queue's state at all.  This peek is always executed with a NO_TIMEOUT.
     *
     *  @param p_timeout_MS - timeout value to wait: this can be any positive
     *         number, or either constant NO_TIMEOUT or INFINITE_TIMEOUT.
     * @return Object  - the data object of the front of the queue, or null if the queue is empty.
     *
     */
    public final Object  peek()
        throws QueueInterruptedException
    {
        return peek( NO_TIMEOUT );
    }


    /**  Return the data object of the front of the queue without affecting the queue's state at all.  This peek will wait for up to p_timeout_MS milliseconds for
     *  the queue to become non-empty.
     *
     *  @param p_timeout_MS - timeout value to wait: this can be any positive
     *         number, or either constant NO_TIMEOUT or INFINITE_TIMEOUT.
     *
     * @return Object  - the data object of the front of the queue, or null if the queue is empty.
     *
     */
    public final Object peek( int p_timeout_MS )
        throws QueueInterruptedException
    {
        synchronized ( outQueueMonitor )
        {
            if ( ! outQueue.isEmpty() )
            {
                return outQueue.peek();
            }

            if ( p_timeout_MS != INFINITE_TIMEOUT )
            {
                getDataFromMidQueues( p_timeout_MS );

                if ( ! outQueue.isEmpty() )
                {
                    return outQueue.peek();
                }

                return null;
            }
        }

        // if here, then infinite timeout  (this is separate because must release outQueueMonitor so clear can work)
        assert ( p_timeout_MS == INFINITE_TIMEOUT ) : "Programming error: reached this code without infinite timeout";

        while( true )
        {
            synchronized ( outQueueMonitor )
            {
                getDataFromMidQueues( 2000 );

                if ( ! outQueue.isEmpty() )   // need to retest since outQueueMonitor has been reaquired
                {
                    return outQueue.peek();
                }
            }

            try
            {
                Thread.sleep( 1 );
            }
            catch (InterruptedException e)
            {
                throw new QueueInterruptedException();
            }
        }
    }


    /**  Return the data object of the front of the queue without affecting the queue's state at all.
     * This peek is always executed with a NO_TIMEOUT.
     *
     * @return Object[] - the 'n' data object of the front of the queue, or null if the queue is empty.
     *
     */
    public final Object[] peekMultiple()
        throws QueueInterruptedException
    {
        return peekMultiple( NO_TIMEOUT );
    }


    /** Dequeues an element from the queue.
     * See _peek for docuemtnation.
     * @param int the wait mode timeout (if 0 is pecified then we are in no wait mode).
     * @return Object  Queued object or null if the queue is empty.
     * @exception InterruptedException
     */
    public final Object[] peekMultiple( int p_timeout_MS )
        throws QueueInterruptedException
    {
        Object[] result = null;

        synchronized ( outQueueMonitor )
        {
            if ( ! outQueue.isEmpty() )
            {
                result = outQueue.peekArray();
                return result;
            }

            if ( p_timeout_MS != INFINITE_TIMEOUT )
            {
                getDataFromMidQueues( p_timeout_MS );

                if ( ! outQueue.isEmpty() )
                {
                    result = outQueue.peekArray();
                    return result;
                }

                return null;
            }
        }

        // if here, then infinite timeout  (this is separate because must release outQueueMonitor so clear can work)
        assert ( p_timeout_MS == INFINITE_TIMEOUT ) : "Programming error: reached this code without infinite timeout";

        while( true )
        {
            synchronized ( outQueueMonitor )
            {
                getDataFromMidQueues( 2000 );

                if ( ! outQueue.isEmpty() )    // need to retest since outQueueMonitor has been reaquired
                {
                    result = outQueue.peekArray();
                    return result;
                }
            }

            try
            {
                Thread.sleep( 1 );
            }
            catch (InterruptedException e)
            {
                throw new QueueInterruptedException();
            }
        }
    }


    /**  Get the number of elements currently in the queue.
     *
     *  @return int - the number of elements currently in the queue.
     *
     *  NOTE: should be called "available", since "size" is ambiguous with the capacity
     */
    public final int available()
    {
        int result = 0;

        result += inQueue.available();
        result += numElementsInMidQueues;
        result += outQueue.available();

        return  result;
    }


    /**  Set the timeout value to use when <code>enqueue(Object)</code> or
     *  <code>dequeue()</code> are called.
     *
     *  @param p_timeout_MS - the timeout value in milliseconds.  The local
     *         constants NO_TIMEOUT and INFINITE_TIMEOUT can be specified to
     *         "never wait" or to "wait indefinitely", respectively.
     */
    public final void setDefaultTimeout( int p_timeout_MS )
    {
        defaultTimeout = p_timeout_MS;
    }


    /**  Set the maximum allowable depth of the queue.
     *
     *  @param maxQueueDepth - the maximum depth of the queue
     *
     */
    public final void setMaxQueueDepth( int maxQueueDepth )
    {
        // no-op
    }


    /**  Get the timeout value used when <code>enqueue(Object)</code> or
     *  <code>dequeue()</code> are called.  Not that the local constants
     *   NO_TIMEOUT and INFINITE_TIMEOUT may be returned, indicating "never wait"
     *   and "infinite wait" policies, respectively.
     *
     */
    public final int getDefaultTimeout()
    {
        return defaultTimeout;
    }


    /**  Get the maximum allowable depth of the queue.
     *
     *  @return int - the maximum depth of the queue
     *
     */
    public final int getCapacity()
    {
        return ( blockSize * numBlocks * numPriorities ); 
    }


    public final int getBlockSize()
    {
        return blockSize;
    }


    /**  Return the queue's name, or null if it is a transient queue.
     *
     */
    public final String getQueueName()
    {
        return baseName;
    }


    /**
     * clears all Objects from the stream
     *
     */
    public final void clear()
    {
        int numElem = available();

        synchronized ( midQueueMonitor )  // lock out other readers
        {
            midQueueMonitor.notifyAll();

            for ( int i = 0; i < numPriorities; ++i )
            {
                while ( ! midQueues[ i ].isEmpty() )
                {
                    releaseQueue( (FixedQueue)midQueues[ i ].dequeue() );
                }
            }

            numElementsInMidQueues = 0;
            instrumentor.incFlushed( numElem );
        }

        synchronized ( inQueueMonitor )  // lock out other writers
        {
            inQueue.clear();
        }

        synchronized ( outQueueMonitor )  // lock out other writers
        {
            outQueue.clear();
        }

        Logger.info (baseName + " Queue cleared: " + numElem + " elements") ;
    }


    /** @returns true if the queue has no elements, false otherwise
     *
     */
    public final boolean isEmpty()
    {
        if ( ! inQueue.isEmpty() )
        {
            return false;
        }
        if ( ! outQueue.isEmpty() )
        {
            return false;
        }
        if ( numElementsInMidQueues > 0 )
        {
            return false;
        }

        return true;
    }


    /** @returns true if the queue is currently holding it's masimum ammount of
     * objects, false otherwise (true ==> cant enqueue)
     *
     */
    public final boolean isFull( int p_priority )
    {
        synchronized( midQueueMonitor ) // only synch on midQueues to prevent blocking
        {
            if ( ! midQueues[ p_priority ].isFull() )
            {
                return false;
            }

            if ( ! inQueue.isFull( p_priority ) )
            {
                return false;
            }

            // don't care about outQueue... even if it is empty, the queue will behave as if full
        }

        return true;
    }


    /** signals that a flip is available if the outQueue is empty and the inQueue is not,
     * no-op otherwise
     *
     */
    public final void flush()
    {
        synchronized ( inQueueMonitor )
        {
            while ( ! inQueue.isEmpty())
            {
                pushDataToMidQueues();
            }
        }
    }


    /**
     * @param p_timeout_MS
     * @return true if the inQueue is empty at the end of the operation, false otherwise
     * @throws QueueInterruptedException
     */
    void pushDataToMidQueues()
    {
        synchronized ( inQueueMonitor )
        {
            synchronized ( midQueueMonitor )
            {
                int numXfered = 0;
                
                for ( int i = 0; i < numPriorities; ++i )
                {
                    if ( ! inQueue.isEmpty( i ) ) // if inQ not empty of priority i 
                    {
                        FixedQueue mq = (FixedQueue)midQueues[ i ].peekLastEnqueue();
                        if ( mq != null )
                        {
                            numXfered += inQueue.flushPriorityQueueTo( i , mq );
                        }
                        
                        if ( ! inQueue.isEmpty( i ) ) // if inQ still not empty for priority i
                        {
                            if ( ! midQueues[ i ].isFull() )
                            {
                                FixedQueue xfer = getEmptyQueue();
                                numXfered += inQueue.flushPriorityQueueTo( i, xfer );
                                midQueues[ i ].enqueue( xfer );
                            }
                        }
                    }
                }
                
                if ( numXfered > 0 )
                {
                    instrumentor.incFlips( 1 );
                    instrumentor.incEnqueued( numXfered );
                    numElementsInMidQueues += numXfered;
                }
            }
        }
    }
                    
                    
    private void getDataFromMidQueues( int p_timeout_MS )
        throws QueueInterruptedException
    {
        assert ( outQueue.isEmpty() ) : "Bad call to getDataFromMidQueues";

        synchronized ( midQueueMonitor )
        {
            if ( numElementsInMidQueues == 0 ) // if nothing available for dequeue
            {
                if ( p_timeout_MS != NO_TIMEOUT ) // don't wait
                {
                    try
                    {
                        instrumentor.incDequeueWaits( 1 );

                        if ( p_timeout_MS == INFINITE_TIMEOUT ) // wait forever
                        {
                            while ( numElementsInMidQueues == 0 )
                            {
                                midQueueMonitor.notify();
                                midQueueMonitor.wait( 0 );  // 0 is the jdk wait() INFINITE_TIMEOUT
                            }
                        }
                        else // wait only for a finite time
                        {
                            long endTime = System.currentTimeMillis() + p_timeout_MS; // Wait is over after the time.
                            long timeRemaining = p_timeout_MS;

                            while ( numElementsInMidQueues == 0 )
                            {
                                if ( timeRemaining <= 0 )
                                {
                                    // failure due to timeout
                                    instrumentor.incDequeueTimeouts( 1 );
                                    return;
                                }

                                midQueueMonitor.notify();
                                midQueueMonitor.wait( timeRemaining );
                                timeRemaining = endTime - System.currentTimeMillis();
                            }
                        }
                    }
                    catch( InterruptedException ex )
                    {
                        throw new QueueInterruptedException();
                    }
                }
            }

            fillOutQueueFromMidQueues();
        }
    }


    private void fillOutQueueFromMidQueues()
    {
        assert ( outQueue != null )   : "null outQueue";
        assert ( outQueue.isEmpty() ) : "replaceOutQueueFromMidQueues called with non-empty outQueue";
        
        boolean shouldSignalMidQueues = false;
        
        outQueue.clear();  // fixed queues require a clear before they can be Refilled

        int numFlipped = 0;

        for( int i = 0; i < numPriorities && ( ! outQueue.isFull() ); ++i )
        {
            for( FixedQueue q = (FixedQueue)midQueues[ i ].peek(); 
                 q != null && ( ! outQueue.isFull() ); 
                 q = (FixedQueue)midQueues[ i ].peek() )
            {
                if ( q.isFull() )  // may be emptying a priority queue to allow enqueue to continue
                {
                    shouldSignalMidQueues = true;
                }
                
                numFlipped += q.transferTo( outQueue );

                if( q.isEmpty() )
                {
                    q = (FixedQueue)midQueues[ i ].dequeue();
                    releaseQueue( q );
                }
            }
        }
        
        if ( shouldSignalMidQueues && numFlipped > 0 )// may be emptying a priority queue to allow enqueue to continue
        {
            midQueueMonitor.notify();
        }
        
        numElementsInMidQueues -= numFlipped;
        instrumentor.incDequeued( numFlipped );
    }


    private final FixedQueue getEmptyQueue()
    {
        FixedQueue result = null;
        
        synchronized ( queuePool )
        {
            if ( ! queuePool.isEmpty() )
            {
                result = (FixedQueue)queuePool.dequeue();
            }
            else
            {
                result = new FixedQueue( blockSize );
            }
        }
        return result;
    }


    public final void releaseQueue( FixedQueue p_queue )
    {
        if ( p_queue != null )
        {    
            p_queue.clear();

            synchronized ( queuePool )
            {
                if ( ! queuePool.isFull() )
                {    
                    queuePool.enqueue( p_queue );
                } // else discard p_queue
            }
        }
    }

}
