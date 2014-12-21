package com.cboe.lwt.thread;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;

import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.pool.ObjectPool;
import com.cboe.lwt.queue.QueueInterruptedException;


/**
 * Represents the common functionality of a controlled subordinate worker thread
 *
 * Implementing classes need only override doTask() with the task the worker 
 * thread should perform
 *
 * This class exists to provide a common management and synchronization strategy
 * to treads dedicated to repeated tasks.  
 *
 * @author  dotyl
 */
public abstract class ThreadTask 
{
    ////////////////////////////////////////////////////////////////////////////
    // Inner Class
    
    private static class State
    {
        // negative numbers imply a non-startable task
        public static final int FAILED    = -1; 
        public static final int PAUSED    = 0;  
        public static final int RUNNING   = 1;  
        public static final int COMPLETE  = 2;  
        
        /**
         * Returns the string representation of the specified state
         *
         * @param p_state The state to find the string representation of
         * 
         * @returns String representation of p_state
         */
        static String toString( int p_state ) 
        {
            switch ( p_state )
            {
                case FAILED    : return "FAILED";
                case PAUSED    : return "PAUSED";
                case RUNNING   : return "RUNNING";
                case COMPLETE  : return "COMPLETE";
            }
            
            return "Illegal state with value (" + p_state + ")";
        }
        
    }
 
    // End Inner Class
    ////////////////////////////////////////////////////////////////////////////


    public static final long NO_WAIT_WARNING = 0;
    
    
    protected String name;     
    
    
    public static ObjectPool establishThreadPool( int p_maxPoolSize,
                                                  int p_initialPoolSize )
    {
        return WorkerThread.establishThreadPool( p_maxPoolSize,
                                                 p_initialPoolSize );
    }
    
    
    private Object stateMonitor;
    private int    state;
    private Thread executor;
    
    
    private ThreadTask()
    {
        stateMonitor = new Object();
        executor = null;
        state = State.PAUSED;
    }
    
    
    protected ThreadTask( String p_name )
    {
        this();
        name = p_name;   
        WorkerThread.getInstance( this );
    }
       
    
    /* This is the method that externding classes should override to do the work
     * that they desire 
     */
    protected abstract void doTask() 
        throws InterruptedException, 
               IOException,
               QueueInterruptedException;

    
    protected void cleanup()
    {
    };
    
    
    public String getName()
    {
        return name;
    }
    
    
    /**
     * causes an unstarted or paused thread to run, for threads in other states, 
     * throws and Error
     *
     * @throws Error if thread was not in UNSTARTED or PAUSED states when this method was called
     */
    public void go()
    {
        synchronized ( stateMonitor )
        {
            if ( state == State.PAUSED )
            {
                state = State.RUNNING;
                stateMonitor.notify();
                Logger.trace( "Starting task : " + getName() );
            }
            else
            {
                Logger.info( "go() called on task : " + getName()
                             + ", but task was in state : " 
                             + State.toString( state ) );
            }
        }
    }

    
    /* 
     * Causes the worker thread to shut down, but returns immediately
     *
     * This method will not return until the worker thread has actually terminated
     */
    public void signalKill()
    {
        Logger.trace( "killing task : " + getName() + ", while in state " + State.toString( state ) );
        
        synchronized ( stateMonitor )
        {
            if (  ( state == State.COMPLETE )
               || ( state == State.FAILED ) )
            {
                return;
            }
            
            state = State.FAILED;
            interruptIfNecessary();
            stateMonitor.notifyAll();
        }
    }
    
    
    protected void setComplete()
    {
        synchronized ( stateMonitor )
        {
            assert ( executor != null ) : "Programming error : Null executor, setComplete must be called from within doTask()";
            assert ( executor == Thread.currentThread() ) : "Programming error : setComplete must be called from within doTask()";
            
            state = State.COMPLETE;
            stateMonitor.notifyAll();
        }
    }
    
    
    /**
     * 
     */
    private void interruptIfNecessary()
    {
        if ( executor != null )
        {
            if ( executor != Thread.currentThread() )
            {
                executor.interrupt();
            }
        }
    }


    /* 
     * Waits for the worker thread to shut down  (similar to thread.join())
     *
     * This method will not return until the worker thread has actually terminated
     * NOTE: Since this method won't return until the thead terminates, it is a 
     * programming error to call this method from the worker thread (the doTask thread) 
     */
    public boolean waitForTermination()
    {
        return waitForTermination( 0 );  // wait forever
    }

    /* 
     * Waits for the worker thread to shut down  (similar to thread.join())
     *
     * This method will not return until the worker thread has actually terminated
     * NOTE: Since this method won't return until the thead terminates, it is a 
     * programming error to call this method from the worker thread (the doTask thread) 
     */
    public boolean waitForTermination( long p_warningRetryTimeout )
    {
        Logger.trace( "Waiting for task to terminate : " + getName() + ", while in state " + State.toString( state ) );

        synchronized ( stateMonitor )
        {
            if ( executor == Thread.currentThread() )
            {
                return ( state == State.COMPLETE );
            }
            
            while ( ! (    executor == null 
                        && isTaskFinished() ) )
            {
                try
                {
                    stateMonitor.wait( p_warningRetryTimeout );
                    
                    if ( ! (    executor == null 
                             && isTaskFinished() ) )
                    {
                        Logger.warning( "Thread " + getName() + " is still waiting for termination. "
                                      + "Executor is " 
                                      + ( ( executor == null ) 
                                          ? "null"
                                          : executor.getName() )
                                      + " State is " 
                                      + State.toString( state ) );
                    }
                }
                catch ( InterruptedException ex )
                {
                    Logger.error( "Interrupted while waiting for " + getName() + " to die", ex );
                    Thread.currentThread().interrupt();  // propagate exception
                    return false;
                }
            }
        
            switch ( state )
            {
                case State.COMPLETE:
                    return true;
                
                case State.FAILED:
                    return false;
                
                default:
                    Logger.error( "Task terminated in state " + State.toString( state ) );
                    return false;
            }
        }
    }

        
    /**
     * Causes the thread to pause if currently in the RUNNING state.  If in 
     * another state, takes no action
     *
     * After it is paused, the worker thread may be resumed by calling go()
     */
    public void pause()
    {
        Logger.trace( "Pausing task : " + getName() + ", while in state " + State.toString( state ) );
        
        synchronized ( stateMonitor )
        {
            if ( state == State.PAUSED || isTaskFinished() )
            {
                return;
            }
            
            state = State.PAUSED;
            interruptIfNecessary();
        }
    }

        
    /** 
     * Returns true if the thread is not dead or dying
     *
     * @returns true if thread is unstarted, paused, or running, 
     * returns false if thread is dead or dying
     */
    public boolean isTaskFinished() 
    { 
        synchronized ( stateMonitor )
        {
            return (  ( state == State.FAILED )
                   || ( state == State.COMPLETE ) );
        }
    }

    
    /** 
     * Returns true if the thread is paused
     *
     * @returns true if thread is paused state
     * returns false if thread otherwise
     */
    public boolean isPaused() 
    { 
        synchronized ( stateMonitor )
        {
            return ( state == State.PAUSED );
        }
    }
    

    /**
     * Returns true when the thread is currently running
     *
     * @returns true if the thread is running, false otherwise
     */
    public boolean isRunning()
    {
        synchronized ( stateMonitor )
        {
            return ( state == State.RUNNING );
        }
    }

    
    /**
     * @return true if the task completed, false otherwise (false returns imply
     * that execute will be called again on the same task after state is rechecked
     */
    boolean execute()
    {
        try
        {
            synchronized ( stateMonitor )
            {
                assert( executor == null ) : "Execute called on running thread";
                
                executor = Thread.currentThread();
       
                while ( state == State.PAUSED )
                {    
                    stateMonitor.wait();
                }
            }
    
            while ( true )
            {    
                synchronized ( stateMonitor )
                {
                    if ( state != State.RUNNING ) // go until stopped
                    { 
                        break;
                    }
                }
                
                doTask();
            }
        }
        catch( InterruptedException ex )  // then recheck status
        {
            Logger.trace( "Task interrupted : " + getName() );
        }
        catch( QueueInterruptedException ex )  // then recheck status
        {
            Logger.trace( "Task interrupted : " + getName() );
        }
        catch( ClosedByInterruptException ex ) // then recheck status
        {
            Logger.info( "ClosedByInterruptException " + getName() );
        }
        catch( IOException ex )
        {
            if ( Thread.interrupted() ) // clears interrupted status
            {
                Logger.info( "IOException (Interrupted) " + getName() );
            }
            else
            {    
                Logger.error( "IOException thrown in ThreadTask thread" + getName(), ex );
                synchronized( stateMonitor )
                {
                    state = State.FAILED;
                }
            }
        }
        finally
        {
            synchronized ( stateMonitor )
            {
                executor = null;
                stateMonitor.notifyAll();
            }
        }
        
        return isTaskFinished();            
    }
    
}
