/*
 * Created on Jan 5, 2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.thread;


import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.pool.ObjectPool;

/**
 * @author dotyl
 * 
 */
final class WorkerThread
    implements Runnable
{
    // //////////////////////////////////////////////////////////////////////////
    // Static
    // //////////////////////////////////////////////////////////////////////////

    private static ObjectPool threadPool = null;


    static ObjectPool getPoolForTestsOnly() // package-private for tests only
    {
        return threadPool;
    }


    /**
     * Establishes an object pool of Message objects with the specified buffer
     * size, initial, and maximum pool sizes.
     * 
     * @param p_maxPoolSize
     *            the maximum number of entries the pool can expand to hold
     * @param p_initialPoolSize
     *            the initial size of the pool, and the number of threads to
     *            initially populate the pool with
     */
    static ObjectPool establishThreadPool( int p_maxPoolSize,
                                           int p_initialPoolSize )
    {
        assert ( threadPool == null ) : "programming error: Pool already established";

        if ( p_maxPoolSize < p_initialPoolSize )
        {
            Logger.error( "CONFIGURATION ERROR: Pool of size " 
                          + p_maxPoolSize 
                          + " can't accept initial size of " 
                          + p_initialPoolSize );
        }
        
        threadPool = ObjectPool.getInstance( "Thread Pool",
                                             p_maxPoolSize );

        for( int i = 0; i < p_initialPoolSize; ++i )
        {
            WorkerThread toAdd = new WorkerThread();
            if ( ! threadPool.checkIn( toAdd ) )
            {
                // pool couldn't take the thread... shutdown
                toAdd.shutdownThread();
                
                Logger.trace( "Thread pool initialized with " + i + " ready threads" );
                return threadPool;
            }
        }

        Logger.trace( "Thread pool initialized with " + p_initialPoolSize + " ready threads" );

        return threadPool;
    }


    // //////////////////////////////////////////////////////////////////////////
    // END Static
    // //////////////////////////////////////////////////////////////////////////


    private Thread thread;
    private ThreadTask currentTask;
    private Object threadAssignMonitor;
    private String rootName;


    private WorkerThread()
    {
        currentTask = null;
        threadAssignMonitor = new Object();

        thread = new Thread( this );
        rootName = thread.getName();
        thread.setDaemon( true );
        thread.start();
    }


    static WorkerThread getInstance( ThreadTask p_worker )
    {
        WorkerThread result = null;

        if( threadPool != null )
        {
            result = (WorkerThread)threadPool.checkOut();

            if( result == null )
            {
                result = new WorkerThread();
                Logger.info( "Pool empty: Allocating new thread : " + result.getName() );
            }
            result.assignTask( p_worker );
        }
        else
        {
            // not pooling
            result = new WorkerThread();
            result.assignTask( p_worker );
            Logger.info( "Created Thread : " + result.getName() );
        }

        return result;
    }


    private void assignTask( ThreadTask p_task )
    {
        synchronized( threadAssignMonitor )
        {
            currentTask = p_task;
            try
            {
                thread.setName( rootName + p_task.getName() );
            }
            catch( RuntimeException ex )
            {
                // couldn't set name... do nothing
            }
            threadAssignMonitor.notify();
        }
    }


    /**
     * called by the contained Thread object's internal thread
     * 
     * NOTE: should NOT be called by either extenders or users of this class the
     * only reason it is private is so that the Thread object can call it
     */
    public void run()
    {
        Logger.trace( "Thread started : " + getName() );

        try
        {
            while( thread != null )
            {
                try
                {
                    waitForTaskAssignment();

                    if( currentTask.execute() ) // if task completed
                    {
                        releaseThread();
                    }
                }
                catch( InterruptedException ex )
                {
                    Logger.trace( "Thread interrupted while waiting to be assigned a task" );
                }
                catch( RuntimeException ex )
                {
                    if( Thread.interrupted() ) // clears interrupted status
                    {
                        Logger.info( "RuntimeException (Interrupted) " + getName(),
                                     ex );
                    }
                    else
                    {
                        Logger.error( "SHUTTING DOWN THREAD : RuntimeException thrown in worker thread : " + getName(),
                                      ex );
                        currentTask.cleanup();
                        shutdownThread();
                    }
                }
            }
        }
        finally
        {
            Logger.trace( "Thread discarded : " + getName() );
        }
    }


    /**
     * 
     */
    private void releaseThread()
    {
        currentTask.cleanup();

        synchronized( threadAssignMonitor )
        {
            currentTask = null;

            if( threadPool != null )
            {
                if( thread != null ) // if thread is still usable
                {
                    // then the thread is recyclable
                    if ( ! threadPool.checkIn( this ) )
                    {
                        // pool couldn't take the thread... shutdown
                        shutdownThread();
                    }
                }
                else
                // thread had a serious problem
                {
                    shutdownThread();
                }
            }
            else
            // then not pooling --> need to end this thread
            {
                shutdownThread();
            }
        }
    }


    /**
     * 
     */
    private void shutdownThread()
    {
        synchronized( threadAssignMonitor )
        {
            currentTask = null;
            thread = null;
        }
    }


    private void waitForTaskAssignment()
        throws InterruptedException
    {
        synchronized( threadAssignMonitor )
        {
            while( currentTask == null ) // wait until assigned to task
            {
                threadAssignMonitor.wait();
            }
        }

    }


    private String getName()
    {
        String name = "Thread : " + rootName;

        if( currentTask != null )
        {
            name += " - Task : " + currentTask.getName();
        }
        else
        {
            name += " - No Assigned Task";
        }

        return name;
    }

}
