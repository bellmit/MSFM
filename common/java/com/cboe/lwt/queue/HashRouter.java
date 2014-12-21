/*
 * HashRouter.java
 * 
 * Created on February 7, 2002, 12:21 PM
 */

package com.cboe.lwt.queue;



public class HashRouter 
    extends Distributor
{
    public HashRouter( InterThreadQueue p_srcQueue )
    {
        this( "Hash Router", 
              p_srcQueue, 
              InterThreadQueue.INFINITE_TIMEOUT, 
              InterThreadQueue.INFINITE_TIMEOUT );
    }
    
    
    public HashRouter( InterThreadQueue p_srcQueue,
                       int              p_destinationFailureTimeout )
    {
        this( "Hash Router", 
              p_srcQueue, 
              p_destinationFailureTimeout, 
              InterThreadQueue.INFINITE_TIMEOUT );
    }
    
    
    public HashRouter( String           p_name,
                       InterThreadQueue p_srcQueue,
                       int              p_destinationFailureTimeout,
                       int              p_routerInputTimeout_MS )
    {
        super( p_name,
               p_srcQueue,
               p_destinationFailureTimeout,
               p_routerInputTimeout_MS );
    }
    
    
    protected void processMsgs( FixedQueue p_msgs, Object[] p_dests )
        throws QueueException
    {
        while( ! p_msgs.isEmpty() )
        {
            Object msg = p_msgs.dequeue();

            int destIndex = ( msg.hashCode() & 0x7fffffff ) % p_dests.length;

            InterThreadQueue dest = (InterThreadQueue)p_dests[ destIndex ];

            assert ( dest != null ) : "Invalid destIndex :" + destIndex + ", numActiveDests: " + p_dests.length;

            dest.enqueue( msg,
                          getDestinationFailureTimeout() );
        }
    }

}
