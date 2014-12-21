/**
 * InterThreadQueue.java
 *
 * Created on February 26, 2002, 11:47 AM
 */

package com.cboe.lwt.queue;


import java.util.Timer;
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
 * Implementation notes: 2 queues (inQueue and outQueue) operate like this:
 *
 * @author dotyl
 */
public final class InterThreadQueue
{
    public  static final int INFINITE_TIMEOUT = -1;
    public  static final int NO_TIMEOUT       = 0;
    private static final boolean IS_DAEMON    = true;
    
    
    static Timer flushTimer = new Timer( IS_DAEMON );
    
    class FlushTask extends TimerTask
    {
        public void run()
        {
            attemptFlush();
        }
    };


    private int blockSize;  // how big is each sub-queue in the ITC

    // NOTE: the reason there are 2 discrete queues is to decrease contention between threads
    private FixedQueue    inQueue;
    private FixedQueue    outQueue;
    private CircularQueue midQueues;
    private CircularQueue queuePool;
    private int           numElementsInMidQueues;

    private int defaultTimeout;

    // these need to be separate objects because the objects they monitor (inQueue/outQueue)
    // are being flipped (double-buffer idiom)
    private Object inQueueMonitor;   // synchronization object for the logical input queue
    private Object outQueueMonitor;  // synchronization object for the logical output queue
    private Object midQueueMonitor;  // synchronization object for the midqueues

    String baseName;

    private QueueInstrumentor qi;

    /* private constructor accessible only to static factory methods */
    private InterThreadQueue( String p_baseName,
                              int    p_blockSize,
                              int    p_numBlocks,
                              int    p_flushInterval_MS )
    {
        baseName = p_baseName;

        blockSize = p_blockSize;

        defaultTimeout = INFINITE_TIMEOUT;

        if ( p_numBlocks < 3 )
        {
            Logger.error( "InterThreadQueue: " + p_numBlocks + " is less than minimum p_numBlocks of 3... resetting p_numBlocks to 3" );
            p_numBlocks = 3;
        }

        int midQueueSize = p_numBlocks - 2;

        queuePool = new CircularQueue( p_numBlocks );
        for ( int i = 0; i < queuePool.getCapacity(); ++i )
        {
            queuePool.enqueue( new FixedQueue( blockSize ) );
        }
        inQueue   = getEmptyQueue();
        outQueue  = getEmptyQueue();
        midQueues = new CircularQueue( midQueueSize );

        numElementsInMidQueues = 0;

        // monitors
        inQueueMonitor  = new Object();
        outQueueMonitor = new Object();
        midQueueMonitor = midQueues;

        qi = InstrumentorHome.findQueueInstrumentorFactory().getInstance( p_baseName, null );
        
        qi.setEnqueueLockObject( midQueueMonitor );
        qi.setLockObject       ( midQueueMonitor );
        qi.setDequeueLockObject( midQueueMonitor );
        
        if ( p_flushInterval_MS > 0 )
        {
            flushTimer.scheduleAtFixedRate( new FlushTask(), p_flushInterval_MS, p_flushInterval_MS );
        }
    }


    public final static InterThreadQueue getInstance( String p_baseName )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new InterThreadQueue( p_baseName, 32, 32, 0 );
    }



    public final static InterThreadQueue getInstance( String p_baseName,
                                                      int    p_blockSize,
                                                      int    p_numBlocks )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new InterThreadQueue( p_baseName, p_blockSize, p_numBlocks, 0 );
    }


    public final static InterThreadQueue getInstance( String p_baseName,
                                                      int    p_blockSize,
                                                      int    p_numBlocks,
                                                      int    p_flushInterval_MS )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new InterThreadQueue( p_baseName, p_blockSize, p_numBlocks, p_flushInterval_MS );
    }


    /**
     * Enqueues an object
     *
     * @param p_obj The object to be written to the stream
     */
    public final void enqueue( Object p_obj )
        throws QueueException
    {
        enqueue( p_obj, defaultTimeout );
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
    public final void enqueue( Object p_obj, int p_timeout_MS )
        throws QueueFullException,
               QueueInterruptedException
    {
        synchronized ( inQueueMonitor )
        {
            if ( ! inQueue.isFull() )
            {
                inQueue.enqueue( p_obj );  // enqueue into the inQueue
                return;
            }

            if ( p_timeout_MS != INFINITE_TIMEOUT )
            {
                if ( ! pushDataToMidQueues( p_timeout_MS ) )
                {
                    throw new QueueFullException( "Enqueue timed out on full queue" );
                }

                inQueue.enqueue( p_obj );  // enqueue into the inQueue
                return;
            }
        }

        // if here, then infinite timeout  (this is separate because must release outQueueMonitor so clear can work)
        assert ( p_timeout_MS == INFINITE_TIMEOUT ) : "Programming error: should only reach this code with infinite timeout";

        while( true )
        {
            synchronized ( inQueueMonitor )
            {
                if ( pushDataToMidQueues( 2000 ) )
                {
                    inQueue.enqueue( p_obj );  // enqueue into the inQueue
                    break;
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

    
    /** Add as many of the given <code>p_objs</code> to the rear of the queue as possible.  
     * If the queue is full before p_objs is depleted, then simply return false and p_objs
     * will still hold the remaining unenqueued objects
     *
     * @param p_objs - the objects to enqueue.  If the ITQ is too full, then p_objs will contain
     * any unenqueued objects after this call completes
     * 
     * @return true if all elements in p_objs have been enqueued, false otherwise
     */
    public final boolean attemptEnqueueMultiple( CircularQueue p_objs )
    {
        synchronized ( inQueueMonitor )
        {
            p_objs.transferTo( inQueue );
            
            if ( p_objs.isEmpty() )
            {
                return true;
            }
        
            try
            {
                pushDataToMidQueues( NO_TIMEOUT );
            }
            catch( QueueInterruptedException ex )
            {
                Logger.error( "Impossible exception... timeout when NO_TIMEOUT was specified", ex );
                return false;
            } 

            p_objs.transferTo( inQueue );
        }
        
        return p_objs.isEmpty();
    }


    /**
     * dequeues an object
     *
     * If no objects are available, the call blocks until an object becoms available
     *
     * @return The next object in the stream
     *
     */
    public final Object dequeue()
        throws QueueInterruptedException
    {
        Object result = dequeue( defaultTimeout );

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
     * @return Object the data object of the front element in the queue, or null if the queue was empty
     *                   after the given wait period.
     * @exception QueueInterruptedException Queue operation interrupted.
     *
     */
    public final Object dequeue( int p_timeout_MS )
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

                return outQueue.dequeue();  // either the midqueues had data or not-- if not, return null
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
                    return outQueue.dequeue();
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
     * @return Object The 'n' data object of the topmost queue element, or <code>null</code> if
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

                return extractOutQueue();
            }
        }

        // if here, then infinite timeout  (this is separate because must release outQueueMonitor so clear can work)
        assert ( p_timeout_MS == INFINITE_TIMEOUT ) : "Programming error: reached this code without infinite timeout";

        while( true )
        {
            synchronized ( outQueueMonitor )
            {
                getDataFromMidQueues( 2000 );

                if ( ! outQueue.isEmpty() ) // need to retest since outQueueMonitor has been reaquired
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


    /**  Return the data object of the front of the queue without affecting the queue's state at all.  This peek is always executed with a NO_TIMEOUT.
     *
     *  @param p_timeout_MS - timeout value to wait: this can be any positive
     *         number, or either constant NO_TIMEOUT or INFINITE_TIMEOUT.
     * @return Object - the data object of the front of the queue, or null if the queue is empty.
     *
     */
    public final Object peek()
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
     * @return Object - the data object of the front of the queue, or null if the queue is empty.
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
     * @return Object Queued object or null if the queue is empty.
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


    /** @deprecated - use available() instead
     * 
     *  Get the number of elements currently in the queue.
     *
     *  @return int - the number of elements currently in the queue.
     *
     *  NOTE: should be called "available", since "size" is ambiguous with the capacity
     */
    public final int size()
    {
        return  available();
    }


    /**  Get the number of elements currently in the queue.
     *
     *  @return int - the number of elements currently in the queue.
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


    /** @deprecated - use getCapacity() instead
     * 
     *  Get the maximum allowable depth of the queue.
     *
     *  @return int - the maximum depth of the queue
     *
     */
    public final int getMaxQueueDepth()
    {
        return getCapacity();
    }


    /**  Get the maximum allowable depth of the queue.
     *
     *  @return int - the maximum depth of the queue
     *
     */
    public final int getCapacity()
    {
        return ( inQueue.getCapacity() * 2 )  // for inqueue + outqueue
             + ( blockSize * midQueues.getCapacity() ); // for midqueues
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
            if ( midQueues.isFull() )
            {
                midQueueMonitor.notifyAll();
            }

            while ( ! midQueues.isEmpty() )
            {
                releaseQueue( (FixedQueue)midQueues.dequeue() );
            }

            numElementsInMidQueues = 0;
			// NOTE: Currently, the three locks allowed in QueueInstrumentor have been
			// set to the midQueueMonitor.  The next steps take advantage of that.
            qi.incFlushed( numElem );
			qi.setEnqueued( numElem );
			qi.setDequeued( numElem );
			qi.setCurrentSize( 0 );
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
        if ( ! midQueues.isEmpty() )
        {
            return false;
        }

        return true;
    }


    /** @returns true if the queue is currently holding it's masimum ammount of
     * objects, false otherwise (true ==> cant enqueue)
     *
     */
    public final boolean isFull()
    {
        synchronized( midQueueMonitor ) // only synch on midQueues to prevent blocking
        {
            if ( ! midQueues.isFull() )
            {
                return false;
            }

            if ( ! inQueue.isFull() )
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
        try
        {
            pushDataToMidQueues( INFINITE_TIMEOUT );  // if midqueues if full, wait until it isn't
        }
        catch ( QueueInterruptedException ex )
        {
            Logger.warning( "Flush cancelled by interrupt", ex );
        }
    }


    /** signals that a flip is available if the outQueue is empty and the inQueue is not,
     * no-op otherwise
     *
     */
    public final void attemptFlush()
    {
        try
        {
            pushDataToMidQueues( NO_TIMEOUT );  // if midqueues if full, don't wait
        }
        catch ( QueueInterruptedException ex )
        {
            Logger.critical( "Impossible exception thrown for NO_TIMEOUT invocation of pushDataToMidQueues", ex );
        }
    }


    /**
     * @param p_timeout_MS
     * @return true if the inQueue is empty at the end of the operation, false otherwise
     * @throws QueueInterruptedException
     */
    boolean pushDataToMidQueues( int p_timeout_MS )
        throws QueueInterruptedException
    {
        synchronized ( inQueueMonitor )
        {
            if ( inQueue.isEmpty() )
            {
                return true;
            }
            
            synchronized ( midQueueMonitor )
            {
                if ( midQueues.isEmpty() )
                {
                    midQueueMonitor.notify();
                }

                if ( midQueues.isFull() ) // if no room to enqueue
                {
                    if ( p_timeout_MS == NO_TIMEOUT ) // don't wait
                    {
                        return false;
                    }

                    try
                    {
                        qi.incEnqueueWaits( 1 );
                        
                        if ( p_timeout_MS == INFINITE_TIMEOUT ) // wait forever
                        {
                            while ( midQueues.isFull() )
                            {
                                midQueueMonitor.notify();
                                midQueueMonitor.wait( 0 );  // 0 is the jdk wait() INFINITE_TIMEOUT
                            }
                        }
                        else // wait only for a finite time
                        {
                            long endTime = System.currentTimeMillis() + p_timeout_MS; // Wait is over after the time.
                            long timeRemaining = p_timeout_MS;

                            while ( midQueues.isFull() )
                            {
                                if ( timeRemaining <= 0 )
                                {
                                    break;  // failure due to timeout
                                }

                                midQueueMonitor.notify();
                                midQueueMonitor.wait( timeRemaining );
                                timeRemaining = endTime - System.currentTimeMillis();
                            }
                        }
                    }
                    catch ( InterruptedException ex )
                    {
                        throw new QueueInterruptedException();
                    }

                    if ( midQueues.isFull() )
                    {
                        return false;
                    }
                }

                // now midQueues has space
                qi.incFlips( 1 );
                qi.incEnqueued( inQueue.available() );
				// NOTE: setting the current size as follows depends upon the fact
				// that all QueueInstrumentor locks are set to midQueueMonitor, which
				// is locked at this point already.
				qi.setCurrentSize( qi.getEnqueued() - qi.getDequeued() );

                midQueues.enqueue( inQueue );  // enqueue into the inQueue
                numElementsInMidQueues += inQueue.available();
                inQueue = getEmptyQueue();
            }
        }

        return true;
    }



    private void getDataFromMidQueues( int p_timeout_MS )
        throws QueueInterruptedException
    {
        synchronized ( outQueueMonitor )
        {
            if ( ! outQueue.isEmpty() )
            {
                return;
            }

            synchronized ( midQueueMonitor )
            {
                if ( midQueues.isEmpty() ) // if nothing available for dequeue
                {
                    if ( p_timeout_MS == NO_TIMEOUT ) // don't wait
                    {
                        qi.incDequeueTimeouts( 1 );
                        return;
                    }
                    
                    try
                    {
                        qi.incDequeueWaits( 1 );

                        if ( p_timeout_MS == INFINITE_TIMEOUT ) // wait forever
                        {
                            while ( midQueues.isEmpty() )
                            {
                                midQueueMonitor.notify();
                                midQueueMonitor.wait( 0 );  // 0 is the jdk wait() INFINITE_TIMEOUT
                            }
                        }
                        else // wait only for a finite time
                        {
                            long endTime = System.currentTimeMillis() + p_timeout_MS; // Wait is over after the time.
                            long timeRemaining = p_timeout_MS;

                            while ( midQueues.isEmpty() )
                            {
                                if ( timeRemaining <= 0 )
                                {
                                    // failure due to timeout
                                    qi.incDequeueTimeouts( 1 );
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

                // now outQueue is empty and midqueues holds data to be moved to outQueue
                releaseQueue( outQueue );
                outQueue = (FixedQueue)midQueues.dequeue();  // get new outQueue from midQueues
                int numFlipped = outQueue.available();
                numElementsInMidQueues -= numFlipped;
                qi.incDequeued( numFlipped );
				// NOTE: setting the current size as follows depends upon the fact
				// that all QueueInstrumentor locks are set to midQueueMonitor, which
				// is locked at this point already.
				qi.setCurrentSize( qi.getEnqueued() - qi.getDequeued() );
            }
        }
    }
    
    
    private FixedQueue extractOutQueue()
    {
        synchronized ( outQueueMonitor )
        {
            if ( outQueue.isEmpty() )
            {
                return null;
            }
            
            FixedQueue result = outQueue;    
            
            synchronized ( midQueueMonitor )
            {
                if ( midQueues.isEmpty() ) // no data in midqueue... do nothing
                {
                    outQueue = getEmptyQueue();  
                }
                else
                {
                    outQueue = (FixedQueue)midQueues.dequeue();  // get new outQueue from midQueues
                    int numFlipped = outQueue.available();
                    numElementsInMidQueues -= numFlipped;
                    qi.incDequeued( numFlipped );
                }
            }

            return result;
        }
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
            if ( p_queue.getCapacity() == blockSize )
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

}
