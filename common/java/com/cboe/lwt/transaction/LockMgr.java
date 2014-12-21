package com.cboe.lwt.transaction;



public class LockMgr
{
    static final boolean metricsEnabled = Boolean.getBoolean( "lwt.transaction.metrics" );
    static final int     lockWaitPause  = Integer.getInteger( "lwt.transaction.lockWaitPause", 500 ).intValue();

    static int waitCount;
    Object     lockedBy = null;


    public final synchronized void lock( final Object p_locker ) 
        throws InterruptedException
    {
        while ( lockedBy != null )
        {
            if ( metricsEnabled )
            {
                System.out.println( "Wait # " + ++waitCount + " for lock from : " + p_locker.toString() );
            }
            
            wait( lockWaitPause );
        }
        
        lockedBy = p_locker;
    }


    public final synchronized void unlock( final Object p_locker ) 
    {
        if ( lockedBy != p_locker )
        {
            throw new RuntimeException( Thread.currentThread().toString() + " -- Asked to unlock, but not lock owner : " + p_locker.toString() );
        }
        lockedBy = null;
        
        notify();
    }
    
}