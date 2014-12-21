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
public class PriorityQueue 
{
    private CircularQueue[] queues; // holds the next outgoing objects        
 
    private int          capacityPerPriority;
    private int          numPriorities;
    
    
    /**
     * constructs a Queue that will accept p_size number of objects before needing to 
     * be drained
     *
     * @param p_size The number of Object  references that can be contained in the queue
     * at any given time
     */
    public PriorityQueue( int p_capacityPerPriority,
                          int p_numPriorities )
    {
        capacityPerPriority = p_capacityPerPriority;
        numPriorities       = p_numPriorities;
        
        queues = new CircularQueue[ p_numPriorities ]; 
        
        for ( int i = 0; i < numPriorities; ++i )
        {
            queues[ i ] = new CircularQueue( capacityPerPriority );
        }
    }
    
    
    /**
     * @returns true if the queue has no elements of any priority, false otherwise
     */
    public final boolean isEmpty()
    {
        for ( int i = 0; i < numPriorities; ++i )
        {
            if ( ! queues[ i ].isEmpty() )
            {
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * @returns true if the queue has no elements of the specified priority, false otherwise
     */
    public final boolean isEmpty(  int p_priority )
    {
        return queues[ p_priority ].isEmpty();
    }
    
    
    /**
     * @returns true if the queue is currently holding it's masimum ammount of 
     * objects, false otherwise
     */
    public final boolean isFull( int p_priority )
    {
        return queues[ p_priority ].isFull();
    }
    
    
    /**
     * returns the number of elements available to be dequeued
     */
    public final int available()
    {
        int result = 0;
        
        for ( int i = 0; i < numPriorities; ++i )
        {
            result += queues[ i ].available();
        }
        
        return result;
    }

    
    /** @return The number of slots in the Queue for each priority level, 
     * including all full and empty slots.
     */
    public final int getCapacity()
    {
        return capacityPerPriority;
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
        
        for ( int i = 0; i < numPriorities; ++i )
        {
            result = queues[ i ].dequeue();
            if ( result != null ) 
            {
                break;
            }
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
        
        for ( int i = 0; i < numPriorities; ++i )
        {
            result = queues[ i ].peek();
            if ( result != null ) 
            {
                break;
            }
        }
        
        return result;
    }
    
    
    /**
     * dequeues all elements, returns them (in order) as an array
     *
     * @returns all elements from the queue without as an array
     */
    final int flushPriorityQueueTo( int p_priority, FixedQueue p_dest )
    {
        CircularQueue q = queues[ p_priority ];
        int numFlushed = q.transferTo( p_dest );
        if ( q.isEmpty() )
        {
            q.clear();
        }
        
        return numFlushed;
    }
    
    
    /**
     * removes all elements from the queue
     */
    public final void clear()
    {
        for ( int i = 0; i < numPriorities; ++i )
        {
            queues[ i ].clear();
        }
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
    public final void enqueue( Object p_obj, int p_priority )
    {
        assert( p_priority <= numPriorities ) 
            : "Illegal priority of : " + p_priority + ", when max priority for this queue is : " + numPriorities;
        assert( ! isFull( p_priority ) ) 
            : "Called enqueue without first calling isFull( priority = " + p_priority + " )";
        
        queues[ p_priority ].enqueue( p_obj );     // add object
    }
    
}
