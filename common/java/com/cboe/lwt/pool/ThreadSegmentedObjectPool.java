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
public final class ThreadSegmentedObjectPool 
{
    private ThreadLocal thr;
    int     maxSize;

    /* private constructor accessible only to static factory methods */
    private ThreadSegmentedObjectPool( String p_name, int p_maxSize )
    {
        assert ( p_maxSize > 0 ) : "Illegal Object Pool size of (" + p_maxSize + ")!  Must be > 0";
        
        thr   = new ThreadLocal();
        
        Logger.trace( "Thread Local Pool [" + p_name + "] established with max size = " + p_maxSize + " per thread" );
    }
    
    
    private final Stack getPool()
    {
        Stack result = (Stack)thr.get();
        if ( result == null )
        {
            result = new Stack( maxSize );
            thr.set( result );
        }
        
        return result;
    }
    
    
    /**
     * Static factory method to acquire an instance of an unused ThreadSegmentedObjectPool 
     * 
     * @param p_initialSize The initial size of the pool
     * @param p_maxDepth The maximum size of the pool
     *
     * @return An unused ThreadSegmentedObjectPool
     */
    public static ThreadSegmentedObjectPool getInstance( String p_name, int p_maxSize )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new ThreadSegmentedObjectPool( p_name, p_maxSize );  
    }
    
    
    /**
     * Returns the number of objects that are currently available for checkout
     *
     * NOTE: since this method depends on volatile data, it may be inaccurate
     * on some SMP platforms
     */
    public int available() 
    {
        return getPool().available();
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
        return getPool().capacity();
    }
     
     
    /**
     * Checks a newly unused object back into the pool for later checkout
     *
     * NOTE: if the pool is full, the object will be discarded
     * 
     * @param p_obj The object to be placed back in the pool
     */
    public boolean checkIn( Object p_obj )
    {
        if ( p_obj == null )
        {
            Logger.error( "Pooling error!  <<<<programming error>>>> Checking in null object" );
            return true;
        }
        
        Stack pool = getPool();
        if ( ! pool.isFull() )
        {
            pool.push( p_obj );
            return true;
        }
        
        return false;
    }

    
    /**
     * @return An unused object from the pool for (re)use by the client
     *
     * NOTE: if there are no objects available in the pool, null will be returned
     */
    public Object checkOut()
    {
        return getPool().pop();
    }
     
     
    public String getStats()
    {
        StringBuffer sb = new StringBuffer( "\nPool usage Stats unavailable for thread local pools" );
        
        return sb.toString();
    }
}
