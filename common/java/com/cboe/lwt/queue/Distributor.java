/*
 * Created on Feb 18, 2005
 */
package com.cboe.lwt.queue;

import java.util.ArrayList;

import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.thread.ThreadTask;

/**
 * @author dotyl
 */
public abstract class Distributor extends ThreadTask
{
    private InterThreadQueue  srcQueue;
    private ArrayList         activeDests;
    private ArrayList         inactiveDests;
    private int               destinationFailureTimeout;
    private int               inputTimeout_MS;
    private CountInstrumentor inMeter;
    
    private Object            destMonitor;
    
    
    
    protected Distributor( String           p_name,
                           InterThreadQueue p_srcQueue,
                           int              p_destinationFailureTimeout,
                           int              p_inputTimeout_MS )
    {
        super( p_name );
        
        Logger.trace( "Creating " + name );

        srcQueue = p_srcQueue;
        
        activeDests    = new ArrayList();
        inactiveDests  = new ArrayList();

        destinationFailureTimeout = p_destinationFailureTimeout;
        inputTimeout_MS = p_inputTimeout_MS;
        
        inMeter = InstrumentorHome.getCountInstrumentor( name + "MsgsIn", new Object() );
        
        destMonitor = new Object();
    }
    
    
    protected int getDestinationFailureTimeout()
    {
        return destinationFailureTimeout;
    }
   
    
    public final void disableDest( InterThreadQueue p_dest )
    {
        int disableIndex;
        
        synchronized ( destMonitor )
        {
            disableIndex = activeDests.indexOf( p_dest );
    
            if ( disableIndex < 0 )
            {
                Logger.error( name + " : Distributor: Can't disable " + p_dest.getQueueName() + ", because it is not enabled" );
                return;  // already disabled
            }
            
            activeDests.remove( disableIndex );
    
            inactiveDests.add( p_dest );
        }
        
        Logger.info( name + " : Distributor: Disabling " + p_dest.getQueueName() + " at index : " + disableIndex + ", active size is now : " + activeDests.size() );
    }
   
    
    public final void enableDest( InterThreadQueue p_dest )
    {
        StringBuffer sb = new StringBuffer( name );
        
        synchronized ( destMonitor )
        {
            if ( activeDests.indexOf( p_dest ) >= 0 )
            {
                Logger.error( name + " : Distributor: Can't enable " + p_dest.getQueueName() + ", because it is already enabled" );
                return;  // already enabled
            }
    
            sb.append( " : " );
            
            int enableIndex = inactiveDests.indexOf( p_dest );
            if ( enableIndex >= 0 )
            {
                sb.append( "Reactivating " );
                inactiveDests.remove( enableIndex );
            }
    
            activeDests.add( p_dest );
            destMonitor.notifyAll();
        }
        
        sb.append( " : Activating object " )
          .append( p_dest.getQueueName() )
          .append( ", Now active : " )
          .append( activeDests.size() );
        
        Logger.info( sb.toString() );
    }


    protected void doTask() 
        throws InterruptedException
    {
        FixedQueue msgs = null;
        
        while( true )
        {
            if ( msgs != null )  // release an empty queue
            {
                if ( msgs.isEmpty() )
                {
                    srcQueue.releaseQueue( msgs );
                    msgs = null;
                }
            }
            
            if ( msgs == null ) // get messages if we're not processing a previously fetched queue
            {
                try
                {                                  
                    msgs = srcQueue.dequeueMultiple( inputTimeout_MS );
                    
                    if ( msgs == null )
                    {
                        flushDestinationQueues();
                        try
                        {
                            srcQueue.flush();  // free up any entries in the srcQueue's
                                               // inQueue
                            
                            msgs = srcQueue.dequeueMultiple( inputTimeout_MS ); 
                        }
                        catch ( QueueException ex )
                        {
                            Logger.error( name + " : Distributor: Exception during dequeue from inbound message queue (after pack)", ex );
                        }
                    }
                    
                    if ( msgs == null )
                    {
                        // still null... failure --> check thread state and loop again
                        return;
                    }
                    
                    inMeter.incCount( msgs.available() );
                }
                catch( QueueInterruptedException ex )
                {
                    InterruptedException ex2 = new InterruptedException( name + " : Interrupted during Replicator Src Dequeue" );
                    ex2.initCause( ex );
                    throw ex2;
                }
            }
            
            try
            {
                Object[] dests;
                synchronized ( destMonitor )
                {
                    while ( activeDests.size() == 0 )
                    {                
                        Logger.error( name + ": Distributor pausing -- no active destinations" );
                        destMonitor.wait();
                    }
                    dests = activeDests.toArray();
                }
                
                processMsgs( msgs, dests );
            }
            catch( QueueInterruptedException ex )
            {
                throw new InterruptedException( name + " : Interrupted during Replication" );
            }
            catch( QueueException ex )
            {
                Logger.info( name + " : Enqueue failure for line", ex );
            }
        }
    }


    protected abstract void processMsgs( FixedQueue p_msgs, Object[] p_dests )
        throws InterruptedException,
               QueueException;


    private void flushDestinationQueues() 
    {
        Object[] dests;
        
        synchronized ( destMonitor )
        {
            dests = activeDests.toArray();
        }
        
        if ( dests != null )
        {
            for ( int i = 0; i < dests.length; ++i )
            {
                ( (InterThreadQueue)dests[i] ).flush();
            }
        }
    }    
    
    
    public final void appendSystemStatus( StringBuffer p_sb )
    {
        p_sb.append( "\n" )
            .append( name )
            .append( "\n\nInbound Queue Depth : " )
            .append( srcQueue.available() )
            .append( "\n\nNumber of Active Destinations : " )
            .append( activeDests.size() );
    }
    
}
