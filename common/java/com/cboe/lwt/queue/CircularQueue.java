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
public class CircularQueue 
{
    private int      curSize;    // how many elements are currently in the queue
    private Object[] queue;      // underlying storage  
    private int      tail;      // indexes the entry that will next receive an enqued object (generally last used index - 1)
    private int      head;      // indexes the entry before the oldest, and next-to-be-removed entry in the list (generally oldest occupied index - 1)
                                // NOTE: if head == tail then queue is empty
    
    
    /**
     * constructs a Queue that will accept p_size number of objects before needing to 
     * be drained
     *
     * @param p_size The number of Object references that can be contained in the queue
     * at any given time
     */
    public CircularQueue( int p_size )
    {
        queue = new Object[ p_size ];
        
        clear();
    }
    
    
    /**
     * @returns true if the queue has no elements, false otherwise
     */
    public boolean isEmpty()
    {
        return ( curSize == 0 );
    }
    
    
    /**
     * @returns true if the queue is currently holding it's masimum ammount of 
     * objects, false otherwise
     */
    public boolean isFull()
    {
        return ( curSize == queue.length );
    }
    
    
    /**
     * returns the number of elements available to be dequeued
     */
    public int available()
    {
        return curSize;
    }
    
    
    /** @return The number of slots in the Queue, including all full
     * and empty slots.
     */
    public int getCapacity()
    {
        return queue.length;
    }
    
    
    /**
     * @returns true if the queue is currently holding it's masimum ammount of 
     * objects, false otherwise
     */
    public final int remainingCapacity()
    {
        return queue.length - curSize;
    }

    
    /**
     * removes an element from the head of the queue
     *
     * NOTE: it is the responsibility of the client to check if the queue is 
     * empty before attempting to add an element.
     *  
     * @returns the oldest element in the queue (FIFO)
     */
    public Object dequeue()
    {
        if ( curSize == 0 )
        {
            return null;
        }
        
        if ( ++head == queue.length ) 
        {
            head = 0;  // wrap
        }
        Object temp  = queue[ head ];  // insert object
        queue[ head ] = null;           // remove reference to removed object
        
        --curSize;                 
        
        return temp;
    }
    
    
    /**
     * @returns an element from the head of the queue without removing it from the queue, 
     * or null if there is no available elements
     */
    public Object peek()
    {
        if ( curSize == 0 )
        {
            return null;
        }
        
        int peekHead = head + 1;
        
        if ( peekHead == queue.length ) 
        {
            peekHead = 0;  // wrap
        }
        return queue[ peekHead  ];  // insert object
    }
    
    
    /**
     * dequeues all elements, returns them (in order) as an array
     *
     * @returns all elements from the queue without as an array
     */
    public Object[] flushToArray()
    {
        if ( curSize == 0 )
        {
            return new Object[0];
        }
        
        Object[] result = new Object[ curSize ];
        
        
        for ( int i = 0; i < result.length; ++i ) 
        {
            result[i] = dequeue();
        }
        
        return result;
    }

    
    /**
     * removes all elements from the queue
     */
    public void clear()
    {
        while ( curSize > 0 )
        {
            if ( ++tail == queue.length )
            {
                tail = 0;
            }
            
            queue[ tail ] = null;
            --curSize;
        }
        
        tail = -1;
        head = -1;
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
    public void enqueue( Object p_obj )
    {
        assert( p_obj != null ) : "enqueueing a null object!";
        assert( curSize < queue.length ) : "Called enqueue without first calling isFull()";
        
        if ( ++tail == queue.length ) 
        {
            tail = 0;  // wrap
        }
        queue[ tail ] = p_obj;     // add object
        
        ++curSize;                 
    }
    
    
    public final int transferTo( FixedQueue p_dest )
    {
        int numToTransfer = ( p_dest.remainingCapacity() > available() )
                            ? available()
                            : p_dest.remainingCapacity();
        
        for ( int i = 0; i < numToTransfer; ++i )
        {
            p_dest.enqueue( dequeue() );
        }
        
        return numToTransfer;
    }
    
    
    public final int transferFrom( FixedQueue p_src )
    {
        int numToTransfer = ( remainingCapacity() > p_src.available() )
                            ? p_src.available()
                            : remainingCapacity();
        
        for ( int i = 0; i < numToTransfer; ++i )
        {
            enqueue( p_src.dequeue() );
        }
        
        return numToTransfer;
    }
    
    
    /** propagates all enqueues to any remote destination (where applicable)
     *
     *  Not applicable here... no op.
     */
    public void flush()
    {
    }
    

    public Object peekLastEnqueue()
    {
        if ( isEmpty() )
        {
            return null;
        }
        
        return queue[ tail ];
    }
    
        
    public CircularQueue shallowCopy()
    {
        CircularQueue result = new CircularQueue( getCapacity() );
        
        result.curSize = curSize;
        System.arraycopy( queue, 0, result.queue, 0, queue.length );
        result.tail = tail;     
        result.head = head;     
        
        return result;
    }

}
