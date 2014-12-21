/**
 * Circularqueue.java
 *
 * Created on March 19, 2002, 11:23 AM
 */

package com.cboe.lwt.collection;

import java.util.Arrays;


/**
 * Statically sized stack of objects.  
 *
 * The semantics for an push are:
 *      test if full
 *      if not full, push
 *
 * The semantics for a pop are:
 *      test if empty
 *      if not empty, pop
 *
 * USAGE NOTES:
 *  -   This class is <b>IN NO WAY SYNCHRONIZED:</b> all users must provide their own synchronization
 *  -   Fullness or emptiness of the stack should be checked before using the enqueue()/dequeue()
 *      methods.  Failure to do this will result in an Error being thrown.
 *
 * @author  dotyl
 */
public class Stack 
{
    public static class Metrics
    {
        // negative numbers imply a non-startable task
        public static final boolean ENABLED  = true; 
        public static final boolean DISABLED = false; 
    }
    
    
    Object[] stack;  // underlying storage  
    private int      top;    // indexes the entry before the newest, and next-to-be-removed entry in the stack 
                             // NOTE: if top == -1 then stack is empty
    
    
    /**
     * constructs a stack that will accept p_size number of objects before needing to 
     * be drained
     *
     * @param p_size The number of Object references that can be contained in the stack
     * at any given time
     */
    public Stack( int p_size )
    {
        this( p_size, Metrics.DISABLED, null );
    }
    
    
    public Stack( int p_size, boolean p_metricsEnabled, String p_meterName )
    {
        if ( p_size < 1 ) 
        {
            p_size = 1;
        }
        
        stack = new Object[ p_size ];
        top = -1;
    }
    
    /**
     * @returns true if the stack has no elements, false otherwise
     */
    public final boolean isEmpty()
    {
        return ( top == -1 );
    }
    
    /**
     * @returns true if the stack is currently holding it's masimum ammount of 
     * objects, false otherwise
     */
    public final boolean isFull()
    {
        return ( top == ( stack.length - 1 ) );
    }
    
    
    /**
     * returns the number of elements available to be dequeued
     */
    public final int available()
    {
        return top + 1;
    }
    
    
    /** @return The number of slots in the Stack, including all full
     * and empty slots.
     */
    public final int capacity()
    {
        return stack.length;
    }
    
    
    /** resizes the internal array to hold p_newCapacity elements
     * @param p_newCapacity
     */
    public final void resize( int p_newCapacity )
    {
        if ( p_newCapacity < available() )
        {
            throw new IllegalArgumentException( "trying to shrink stack below its current capacity" );
        }
        
        Object[] temp = stack;
        
        stack = new Object[ p_newCapacity ];
        
        System.arraycopy( temp, 0, stack, 0, available() );
    }
    
    
    /**
     * removes an element from the top of the stack
     *
     * NOTE: it is the responsibility of the client to check if the stack is 
     * empty before attempting to add an element, or a null object may be returned
     *  
     * @returns the oldest element in the stack (FIFO)
     */
    public final Object pop()
    {
        if ( top < 0 )
        {
            return null;
        }
        
        Object result = stack[ top ];
        stack[ top ] = null;           // remove reference to removed object
        
        --top;                 
        
        return result;
    }
    
    
    /**
     * @returns an element from the head of the stack without removing it from the stack, 
     * or null if there is no available elements
     */
    public final Object peek()
    {
        if ( top < 0 )
        {
            return null;
        }
        
        return stack[ top ]; 
    }
    
    
    /**
     * @returns an element from the head of the stack without removing it from the stack, 
     * or null if there is no available elements
     */
    public final Object peek( int p_index )
    {
        if ( p_index > top )
        {
            return null;
        }
        
        return stack[ p_index ];  
    }
    
    
    /**
     * @returns an element from the head of the stack without removing it from the stack, 
     * or null if there is no available elements
     */
    public final void replace( int p_index, Object p_newObj )
    {
        if ( p_index > top )
        {
            throw new ArrayIndexOutOfBoundsException( "Illegal index of : " + p_index );
        }
        
        stack[ p_index ] = p_newObj;  
    }
    
    
    /**
     * removes all elements from the stack
     */
    public final void clear()
    {
        if ( top >= 0 )
        {
            Arrays.fill( stack, 0, top + 1, null );
            top = -1;
        }
    }
    

    /**
     * inserts an element at the top of the stack
     *
     * @param The element to insert into the stack (LIFO)
     * @return the index the object was pushed into
     */
    public final int push( Object p_obj )
    {
        if ( top == stack.length - 1 )
        {
            resize( 2 * top );
        }
        
        stack[ ++top ] = p_obj;     // add object
        
        return top;
    }
    

    /**
     * transfers all elements of the specified stack onto this stack
     * 
     * NOTE: the elements from p_from are kept in their ORIGINAL LIFO order (not 
     * pulled from the top of p_from and inserted LIFO into this stack)
     * 
     * NOTE: when this operation is complete, p_from will be empty
     *
     * @param The stack to push onto this stack
     * @return the number of entries transferred
     */
    public final int pushFrom( Stack p_from )
    {
        int copyLength = p_from.available();
        
        if ( copyLength > capacity() - available() )
        {
            resize( capacity() + p_from.capacity() );
        }
        
        System.arraycopy( p_from.stack, 0, stack, top + 1, copyLength );
        top += copyLength;
        
        p_from.clear();
        
        return copyLength;
    }
    
    
    public Object[] exposeValues()
    {
        return stack;
    }
    
        
    public final class PeekIterator
    {
        private int peekTop;
        
        
        PeekIterator( int p_top )
        {
            peekTop = p_top;
        }
        
        
        public final Object peekNext()
        {
            if ( peekTop < 0 )
            {
                return null;
            }
            
            return stack[ peekTop-- ];
        }
    }
    
    
    /**
     * removes an element from the top of the stack
     *
     * NOTE: it is the responsibility of the client to check if the stack is 
     * empty before attempting to add an element, or a null object may be returned
     *  
     * @returns the oldest element in the stack (FIFO)
     */
    public final PeekIterator peekIterator()
    {
        return new PeekIterator( top );
    }

    
}
