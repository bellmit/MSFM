/*
 * ObjectPool.java
 *
 * Created on February 26, 2002, 11:47 AM
 */

package com.cboe.lwt.pool;

import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.queue.FixedQueue;


/** 
 * A pool of objects to promote better memory management.  When an object is no longer
 * needed, it is checked in to the pool.  When an object is needed, it is checked out
 * from the pool.
 *
 * Notes:
 *      If no entries are available on check out, null is returned.  
 *      If no room is available in the pool on check in, the object is discarded
 *
 * This object <B>IS Thread-Safe</B>, and requires no external synchronization
 *      
 * @author dotyl
 */
public class LocalCacheObjectPool 
{
    private final ThreadLocal threadLocalCacheRef = new ThreadLocal();
    private       int         localCacheSize = 0;
    
    private       ObjectPool  pool;
    

    /* private constructor accessible only to static factory methods */
    private LocalCacheObjectPool( String p_name, 
                                  int    p_maxPoolSize, 
                                  int    p_localCacheSize )
    {
        localCacheSize = p_localCacheSize;
        int blockedUnderlyingPoolSize = p_maxPoolSize / p_localCacheSize;
        
        pool = ObjectPool.getInstance( p_name + ".pool", blockedUnderlyingPoolSize );
            
        Logger.trace( "Pool [" 
                      + p_name 
                      + "] established with max size = " 
                      + p_maxPoolSize
                      + ", which implies a blocked underlying pool size of : " 
                      + blockedUnderlyingPoolSize
                      + ", and thread-local cache size = " 
                      + p_localCacheSize );
    }
    
    
    /**
     * Static factory method to acquire an instance of an unused ObjectPool 
     * 
     * @param p_initialSize The initial size of the pool
     * @param p_maxDepth The maximum size of the pool
     *
     * @return An unused ObjectPool
     */
    public static LocalCacheObjectPool getInstance( String p_name, 
                                                    int    p_maxPoolSize, 
                                                    int    p_localCacheSize )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new LocalCacheObjectPool( p_name, p_maxPoolSize, p_localCacheSize );  
    }
    
    
    /**
     * Returns the number of objects that are currently available for checkout
     *
     * NOTE: since this method depends on volatile data, it may be inaccurate
     * on some SMP platforms
     */
    public final int available() 
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
    public final int capacity()
    {
        return pool.capacity() + ( 2 * localCacheSize );
    }
     
     
    /**
     * Checks a newly unused object back into the pool for later checkout
     *
     * NOTE: if the pool is full, the object will be discarded
     * 
     * @param p_obj The object to be placed back in the pool
     */
    public final boolean checkIn( Object p_obj )
    {
        assert ( p_obj != null ) : "Pooling error!  <<<<programming error>>>> Checking in null object";
        
        LocalCache localCache = getLocalCache();
        FixedQueue localIn    = localCache.checkInCache;
         
        if ( ! localIn.isFull() )
        {
            localIn.enqueue( p_obj );

            return true;
        }
    
        if ( pool.checkIn( localIn ) )  // if the checkin worked...
        {
            localCache.checkInCache = new FixedQueue( localCacheSize );
            localCache.checkInCache.enqueue( p_obj );
            
            return true;
        }
        
        // local cache was full, shared pool full, so discard all entries in localIn
        // this is done in anticipation of further ofverflow checkins
        localIn.clear();
        return false;         
    }

    
    /**
     * @return An unused object from the pool for (re)use by the client
     *
     * NOTE: if there are no objects available in the pool, null will be returned
     */
    public final Object checkOut()
    {
        LocalCache localCache = getLocalCache();
        FixedQueue localOut = localCache.checkOutCache;
         
        if ( localOut.isEmpty() )
        {
            localOut = (FixedQueue)pool.checkOut();
            
            if ( localOut == null )
            {
                return null;  // local cache empty and the shared pool empty
            }
            
            localCache.checkOutCache = localOut;  // set new local cache from the shared pool
        }
        
        return localOut.dequeue();
    }
     
     
    public final String getStats()
    {
        return pool.getStats();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    // implementation
    
    private static class LocalCache
    {
        public LocalCache( int p_localCacheSize )
        {
            checkInCache  = new FixedQueue( p_localCacheSize );
            checkOutCache = new FixedQueue( 0 );  // this guy will be discarded with the first successful checkout
        }
        
        public FixedQueue checkInCache;
        public FixedQueue checkOutCache;
    }
    
    
    private final LocalCache getLocalCache()
    {
        LocalCache result = (LocalCache)threadLocalCacheRef.get();
        if ( result == null )
        {
            result = new LocalCache( localCacheSize );
            threadLocalCacheRef.set( result );
        }
        return result;
    }

}
