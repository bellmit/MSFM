package com.cboe.lwt.pool;

import com.cboe.lwt.collection.Stack;
import com.cboe.lwt.eventLog.Logger;


/** 
 * A pool of objects to promote better memory management.  When an object is no longer
 * needed, it is checked in to the pool.  When an object is needed, it is checked out
 * from the pool.
 *
 * Notes:
 *      If no entries are available on check out, null is returned.  
 *      If no room is available in the pool on check in, the object is discarded
 *
 * This object <B>is Thread-Safe</B>, and requires no external synchronization
 *      
 * @author dotyl
 */
public final class LocalReclaimingObjectPool 
{
    int   maxSize;
    Stack pool;
    Stack checkedOut;

    
    /* private constructor accessible only to static factory methods */
    private LocalReclaimingObjectPool( int p_maxSize )
    {
        assert ( p_maxSize > 0 ) : "Illegal Object Pool size of (" + p_maxSize + ")!  Must be > 0";
        
        pool = new Stack( p_maxSize );
        checkedOut = new Stack( p_maxSize );
        
        Logger.trace( "Tracking Local Pool established with max size = " + p_maxSize );
    }
    
    
    /**
     * Static factory method to acquire an instance of an unused MeteredObjectPool 
     * 
     * @param p_initialSize The initial size of the pool
     * @param p_maxDepth The maximum size of the pool
     *
     * @return An unused MeteredObjectPool
     */
    public static LocalReclaimingObjectPool getInstance( int p_maxSize )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new LocalReclaimingObjectPool( p_maxSize );  
    }
    
    
    /**
     * Returns the number of objects that are currently available for checkout
     *
     * NOTE: since this method depends on volatile data, it may be inaccurate
     * on some SMP platforms
     */
    public int available() 
    {
        return pool.available();
    }
    
    
    /**
     * @return The maximum size of the pool.  including all full and empty spaces.  
     *
     * NOTE: this number may not the current capacity, since the internal queues
     * may expand on demand.  To determine current capacity, call getCurrentCapacity()
     *
     * NOTE: since this method depends on volatile data, it may be inaccurate
     * on some SMP platforms
     */
    public int capacity()
    {
        return pool.capacity();
    }
     
     
    /**
     * Checks all items checked out of this pool back in
     * 
     * @return the number of items checked back in
     */
    public int checkIn()
    {
        return pool.pushFrom( checkedOut );
    }

    
    /**
     * @return An unused object from the pool for (re)use by the client
     *
     * NOTE: if there are no objects available in the pool, null will be returned
     */
    public Object checkOut()
    {
        Object result = pool.pop();
        
        if ( result != null )
        {
            checkedOut.push( result );
        }
        
        return result;
    }
     
     
    public String getStats()
    {
        StringBuffer sb = new StringBuffer( "\nPool usage Stats unavailable for local pools" );
        
        return sb.toString();
    }
}
