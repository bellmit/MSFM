/**
 * Circularqueue.java
 *
 * Created on March 19, 2002, 11:23 AM
 */

package com.cboe.lwt.transaction;

import java.util.Arrays;



/**
 * Statically sized stack of TLinks.  
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
public final class TLinkVector 
{
    private TLink[] vector;  // underlying storage  
    private int     top;     // indexes the entry before the newest, and next-to-be-removed entry in the stack 
                             // NOTE: if top == -1 then stack is empty
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer( 64 );
        
        sb.append( "\nTop : " )
          .append( top )
          .append( "\nVector:" );
        
        for( int i = 0; i <= top; ++i )
        {
            sb.append( "\n    i = " )
              .append( i )
              .append( ", id = " )
              .append( vector[i].getId().objectId );
        }
        
        return sb.toString();
    }
    
    
    /**
     * constructs a stack that will accept p_size number of TLinks before needing to 
     * be drained
     *
     * @param p_size The number of TLink references that can be contained in the stack
     * at any given time
     */
    ////////////////////////////////////////////////////////////////////////////
    // private constructor

    private TLinkVector( int p_size )
    {
        vector = new TLink[ p_size ];
        top = -1;
    }
    
    // private constructor
    ////////////////////////////////////////////////////////////////////////////
    // factory method
    
    static TLinkVector getInstance( int p_initialSize )
    {
        return new TLinkVector( p_initialSize );
    }
    
    // factory method
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * @returns true if the stack has no elements, false otherwise
     */
    public final boolean isEmpty()
    {
        return ( top == -1 );
    }
    
    /**
     * @returns true if the stack is currently holding it's masimum ammount of 
     * TLinks, false otherwise
     */
    public final boolean isFull()
    {
        return ( top == ( vector.length - 1 ) );
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
        return vector.length;
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
        
        TLink[] temp = vector;
        
        vector = new TLink[ p_newCapacity ];
        
        System.arraycopy( temp, 0, vector, 0, available() );
    }
    
    
    /**
     * removes an element from the top of the stack
     *
     * NOTE: it is the responsibility of the client to check if the stack is 
     * empty before attempting to add an element, or a null TLink may be returned
     *  
     * @returns the oldest element in the stack (FIFO)
     */
    public final TLink removeEnd()
    {
        if ( top < 0 )
        {
            return null;
        }
        
        TLink result = vector[ top ];
        vector[ top ] = null;           // remove reference to removed TLink
        
        --top;                 
        
        return result;
    }
    
    
    /**
     * @returns an element from the head of the stack without removing it from the stack, 
     * or null if there is no available elements
     */
    public final TLink peekEnd()
    {
        if ( top < 0 )
        {
            return null;
        }
        
        return vector[ top ]; 
    }
    
    
    /**
     * @returns an element from the head of the stack without removing it from the stack, 
     * or null if there is no available elements
     */
    public final TLink peek( int p_index )
    {
        if ( p_index > top )
        {
            return null;
        }
        
        return vector[ p_index ];  
    }
    
    
    /**
     * @returns an element from the head of the stack without removing it from the stack, 
     * or null if there is no available elements
     */
    public final void replace( int p_index, TLink p_newLink )
    {
        if ( p_index > top )
        {
            throw new ArrayIndexOutOfBoundsException( "Illegal index of : " + p_index );
        }
        
        vector[ p_index ] = p_newLink;  
    }
    
    
    /**
     * removes all elements from the stack
     */
    public final void clear()
    {
        if ( top >= 0 )
        {
            Arrays.fill( vector, 0, top + 1, null );
            top = -1;
        }
    }
    

    /**
     * inserts an element at the top of the stack
     *
     * NOTE: it is the responsibility of the client to check if the stack is 
     * full before attempting to add an element.  
     * An Error will be thrown if an enqueue is attempted on a full stack
     *
     * @param The element to insert into the stack (LIFO)
     * @return the index the TLink was pushed into
     */
    public final int addEnd( TLink p_link )
    {
        if ( top == vector.length - 1 )
        {
            resize( 2 * top );
        }
        
        vector[ ++top ] = p_link;     // add TLink
        
        return top;
    }
    
    
    public TLink[] exposeValues()
    {
        return vector;
    }
    
    
    public final TLink find( Uid p_id )
    {
        for ( int i = 0; i <= top; ++i )
        {
            if ( vector[ i ].getId().isEqualTo( p_id ) )
            {
                return vector[ i ];
            }
        }
        
        return null;
    }
    
    
    final void addAll( TLinkVector p_copyFrom )
    {
        int copyLength = p_copyFrom.available();
        
        if ( copyLength == 0 )
        { // copyfrom is empty
            return;
        }
        
        int newTop = top + copyLength;
        
        if ( newTop >= vector.length )
        {
            resize( newTop * 2 );
        }
        
        for ( int i = 0; i < copyLength; ++i )
        {
            vector[ i ] = NestedTLink.createVersion( p_copyFrom.vector[ i ] );
        }
        
        top += copyLength;
    }
    
}
