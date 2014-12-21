/**
 * Circularqueue.java
 *
 * Created on March 19, 2002, 11:23 AM
 */

package com.cboe.lwt.queue;



/**
 * Statically sized circular queue of objects.  
 *
 * The semantics for an enqueue are:
 *      test if full
 *      if not full, enqueue
 *
 * The semantics for a dequeue are:
 *      test if empty
 *      if not empty, dequeue
 *
 * USAGE NOTES:
 *  -   This class is <b>IN NO WAY SYNCHRONIZED:</b> all users must provide their own synchronization
 *  -   Fullness or emptiness of the queue should be checked before using the enqueue()/dequeue()
 *      methods.  Failure to do this will result in an Error being thrown.
 *
 * @author  dotyl
 */
public class FixedQueue 
{
    private Object[] queue; // holds the next outgoing objects        
    private int      nextEnqueueIndex;  // points to empty slot for next enqueue, if == queue.length, then queue is full 
    private int      nextDequeueIndex;  // points to the next full slot to dequeue, if == nextEnqueueIndex, then queue is empty
    /**
     * constructs a Queue that will accept p_size number of objects before needing to 
     * be drained
     *
     * @param p_size The number of Object references that can be contained in the queue
     * at any given time
     */
    public FixedQueue( int p_size )
    {
        queue = new Object[ p_size ]; 
        nextEnqueueIndex = 0;
        nextDequeueIndex = 0;
    }
    
    /**
     * @returns true if the queue has no elements, false otherwise
     */
    public final boolean isEmpty()
    {
        return nextEnqueueIndex == nextDequeueIndex;
    }
    
    /**
     * @returns true if the queue is currently holding it's masimum ammount of 
     * objects, false otherwise
     */
    public final boolean isFull()
    {
        return nextEnqueueIndex == queue.length;
    }
    
    /**
     * @returns true if the queue is currently holding it's masimum ammount of 
     * objects, false otherwise
     */
    public final int remainingCapacity()
    {
        return queue.length - nextEnqueueIndex;
    }
    
    /**
     * returns the number of elements available to be dequeued
     */
    public final int available()
    {
        return nextEnqueueIndex - nextDequeueIndex;
    }
    
    /** @return The number of slots in the Queue, including all full
     * and empty slots.
     */
    public final int getCapacity()
    {
        return queue.length;
    }
    
    /**
     * removes an element from the head of the queue
     *
     * NOTE: it is the responsibility of the client to check if the queue is 
     * empty before attempting to add an element.
     *  
     * @returns the oldest element in the queue (FIFO)
     */
    public final Object dequeue()
    {
        Object result = null;
        
        if ( ! isEmpty() )  // if outqueue is NOT empty
        {
            result = queue[ nextDequeueIndex ];
            queue[ nextDequeueIndex ] = null;
            ++nextDequeueIndex;
        }
        
        return result;
    }
    
    /**
     * @returns an element from the head of the queue without removing it from the queue, 
     * or null if there is no available elements
     */
    public final Object peek()
    {
        Object result = null;
        
        if ( ! isEmpty() )  // if outqueue is NOT empty
        {
            result = queue[ nextDequeueIndex ];
        }
        
        return result;
    }
    
    
    /**
     * dequeues all elements, returns them (in order) as an array
     *
     * @returns all elements from the queue without as an array
     */
    public final Object[] flushToArray()
    {
        Object[] result = new Object[ available() ];
        
        flushToArray( result, 0 );
        
        return result;
    }
    
    
    /**
     * dequeues all elements, returns them (in order) as an array
     *
     * @returns all elements from the queue without as an array
     */
    public final int flushToArray( Object[] p_toArray, int p_offset )
    {
        int length = available();
        
        assert ( length <= ( p_toArray.length - p_offset ) ) 
            : "Array of size " + p_toArray.length
            + ", is not big enough to hold available : " + length 
            + ", at offset : " + p_offset;
        
        System.arraycopy( queue, nextDequeueIndex, p_toArray, p_offset, length );
        
        while ( nextEnqueueIndex != nextDequeueIndex )
        {
            queue[ nextDequeueIndex++ ] = null;
        }

        nextEnqueueIndex = 0;
        nextDequeueIndex = 0;
        
        return length;
    }
    
    
    /**
     * dequeues all elements, returns them (in order) as an array
     *
     * @returns all elements from the queue without as an array
     */
    public final Object[] peekArray()
    {
        Object[] result = new Object[ available() ];
        
        if ( result.length > 0 )
        {
            System.arraycopy( queue, nextDequeueIndex, result, 0, result.length );
        }
        
        return result;
    }
    
    
    /**
     * removes all elements from the queue
     */
    public final void clear()
    {
        while ( nextEnqueueIndex != nextDequeueIndex ) 
        {
            queue[nextDequeueIndex++] = null;
        }

        nextEnqueueIndex = 0;
        nextDequeueIndex = 0;
    }
    
    
    /**
     * inserts an element at the tail of the queue
     *
     * NOTE: it is the responsibility of the client to check if the queue is 
     * full before attempting to add an element.  
     * An Error will be thrown if an enqueue is attempted on a full queue
     *
     * @param The element to insert into the queue (FIFO)
     */
    public final void enqueue( Object p_obj )
    {
        assert( p_obj != null ) : "enqueueing a null object!";
        assert( ! isFull() ) : "Called enqueue without first calling isFull()";
        
        queue[ nextEnqueueIndex ] = p_obj;     // add object
        ++nextEnqueueIndex;                 
    }
    
    
    /**
     * inserts elements at the tail of the queue
     *
     * NOTE: it is the responsibility of the client to check if the queue is 
     * full before attempting to add an element.  
     * An Error will be thrown if an enqueue is attempted on a full queue
     *
     * @param The element to insert into the queue (FIFO)
     */
    public final void enqueue( Object[] p_src, int p_index, int p_length )
    {
        assert( remainingCapacity() >= p_length ) : "Called enqueue without first checking capacity";
        
        System.arraycopy( p_src, p_index, queue, nextEnqueueIndex, p_length );
        nextEnqueueIndex += p_length;                 
    }
    
    
    public final int transferTo( FixedQueue p_dest )
    {
        int srcAvail = available();
        int destSpace = p_dest.remainingCapacity();
        
        int numToTransfer = ( p_dest.remainingCapacity() > srcAvail )
                            ? srcAvail
                            : destSpace;
        
        if ( numToTransfer > 0 )
        {
            p_dest.enqueue( queue, nextDequeueIndex, numToTransfer );
            
            for ( int i = 0; i < numToTransfer; ++i )
            {
                queue[ nextDequeueIndex++ ] = null;
            }
        }
        
        return numToTransfer;
    }
    
}
