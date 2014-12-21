/*
 * ObjectPool.java
 *
 * Created on February 26, 2002, 11:47 AM
 */

package com.cboe.lwt.pool;

import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
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
public class ObjectPool 
{
    // NOTE: the reason there are 2 discrete queues is to decrease contention between threads
    private Stack inPool;      // incoming (checked in) objects
    private Stack outPool;     // outgoing (to be checked out) objects
    
    // these need to be separate objects because the objects they monitor (inQueue/outQueue)
    // are being flipped (double-buffer idiom)
    private Object inPoolMonitor;   // synchronization object for the logical input queue
    private Object outPoolMonitor;  // synchronization object for the logical output queue
    
    private CountInstrumentor allocationMeter;
    private CountInstrumentor discardMeter;
    private CountInstrumentor checkinMeter;
    private CountInstrumentor checkoutMeter;
    private CountInstrumentor flipMeter;

   
    /* private constructor accessible only to static factory methods */
    private ObjectPool( String p_name, int p_maxSize )
    {
        int subQueueSize = p_maxSize / 2;
        
        assert ( subQueueSize > 0 ) : "Illegal Object Pool size of (" + p_maxSize + ")!  Must be > 2";
        
        inPool      = new Stack( subQueueSize );
        outPool     = new Stack( subQueueSize );
            
        inPoolMonitor  = new Object();
        outPoolMonitor = new Object();
        	
        allocationMeter = InstrumentorHome.getCountInstrumentor( p_name + "Allocations", outPoolMonitor );
        checkoutMeter   = InstrumentorHome.getCountInstrumentor( p_name + "Checkouts",   outPoolMonitor );
        checkinMeter    = InstrumentorHome.getCountInstrumentor( p_name + "Checkins",    inPoolMonitor );
        discardMeter    = InstrumentorHome.getCountInstrumentor( p_name + "Discards",    outPoolMonitor );
        flipMeter       = InstrumentorHome.getCountInstrumentor( p_name + "Flips",       inPoolMonitor );

        Logger.trace( "Pool [" + p_name + "] established with max size = " + ( subQueueSize * 2 ) );
    }
    
    
    /**
     * Static factory method to acquire an instance of an unused ObjectPool 
     * 
     * @param p_initialSize The initial size of the pool
     * @param p_maxDepth The maximum size of the pool
     *
     * @return An unused ObjectPool
     */
    public static ObjectPool getInstance( String p_name, int p_maxSize )
    {
        // divide by 2 translates from desired pool size to internal queue size
        return new ObjectPool( p_name, p_maxSize );  
    }
    
    
    /**
     * Returns the number of objects that are currently available for checkout
     *
     * NOTE: since this method depends on volatile data, it may be inaccurate
     * on some SMP platforms
     */
    public final int available() 
    {
        return inPool.available() + outPool.available();
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
        return inPool.capacity() + outPool.capacity();
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
        
        // try the InQueue
        synchronized ( inPoolMonitor )
        {
            if ( ! inPool.isFull() )
            {
                inPool.push( p_obj );
                checkinMeter.incCount( 1 );
                return true;
            }
        }
        
        synchronized ( outPoolMonitor )  // lock out ONLY other consumers of the pooled resource
        {
            if ( outPool.isFull() )
            {
                discardMeter.incCount( 1 );
                return false;
            }
            
            // if we're here, the out pool had room...
            synchronized ( inPoolMonitor )  // lock out producers of pooled resource until after potential buffer swap
            {
                swapQueues();
                
                inPool.push( p_obj );
                checkinMeter.incCount( 1 );
            }
        }
        
        return true;
    }


    private void swapQueues()
    {
        Stack temp = outPool;
        outPool = inPool;
        inPool  = temp;
        flipMeter.incCount( 1 );
    }

    
    /**
     * @return An unused object from the pool for (re)use by the client
     *
     * NOTE: if there are no objects available in the pool, null will be returned
     */
    public final Object checkOut()
    {
        synchronized ( outPoolMonitor )  // lock out ONLY other consumers of the pooled resource
        {
            if ( ! outPool.isEmpty() )
            {
                checkoutMeter.incCount( 1 );
                return outPool.pop();
            }
            
            // if we're here, the out queue was empty...
            synchronized ( inPoolMonitor )          // lock out producers of pooled resource until after potential buffer swap
            {
                if ( ! inPool.isEmpty() )   // if swap would help
                {
                    swapQueues();
                
                    checkoutMeter.incCount( 1 );
                    return outPool.pop();
                } 
            }
            
            // failed to retrieve object
            allocationMeter.incCount( 1 );
        }

        return null;  // allow for external creation or other handling if there are no pool entries available
    }
     
     
    public final String getStats()
    {
        StringBuffer sb = new StringBuffer( "\nPool usage Stats" );
        
        sb.append( "\nCheckins          : " ).append( checkinMeter.getCount() )
          .append( "\nDiscards          : " ).append( discardMeter.getCount() )
          .append( "\nCheckouts         : " ).append( checkoutMeter.getCount() )
          .append( "\nAllocations       : " ).append( allocationMeter.getCount() )
          .append( "\nCurrent Available : " ).append( available() );
          
        return sb.toString();
    }
}
