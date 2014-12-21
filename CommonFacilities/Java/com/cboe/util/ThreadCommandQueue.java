package com.cboe.util;

/**
 * This class implements a priority queue for ThreadCommands
 * Note that it has package access.
 *
 * @author Craig Murphy
 */

import java.util.Enumeration;

public class ThreadCommandQueue
{
    private ThreadCommandRingQueue primaryQueue; // Used for normal stuff coming in
    private ThreadCommandArrayQueue unavailableQueue; // Used for stuff that was marked unavailable when removed from primary Queue
    private boolean shuttingDown;
    private ThreadPool threadPool;


    /**
     * Constructor
     */
    public ThreadCommandQueue(ThreadPool pool)
    {
        primaryQueue = new ThreadCommandRingQueue();
        StringBuilder label = new StringBuilder(pool.poolName.length()+65);
        label.append(this).append("threadPool name").append(pool.poolName);
        System.out.println(label.toString());
        unavailableQueue = new ThreadCommandArrayQueue();
        shuttingDown = false;
        threadPool = pool;
    }

    /**
     * Tells whether there is anything on the queue or not
     * @return true == nothing on queue
     */
    public boolean isEmpty()
    {
        return primaryQueue.isEmpty() && unavailableQueue.isEmpty();
    }
    /**
     * Adds a new command to the queue, placing it in the proper place
     * for its priority.
     *
     * @param command  -- the command to place in the queue
     */
    public synchronized void insertCommand( ThreadCommand command )
        throws InvalidThreadPoolStateException
    {
        if  ( !shuttingDown )
        {
            primaryQueue.insertCommand( command );
            notify();
        }
        else
        {
            throw new InvalidThreadPoolStateException("ThreadPool Shutting Down");
        }
    }
    /**
     * This method will get the next available command.  If a command is not
     * available, the method will wait for one.  If there are no commands
     * waiting, the method returns null.
	  * NOTE: the queue does not get explicitly notified when a command goes
	  * from unavailable to available. However, the only reason
	  * that a command is unavailable is that it is being processed
	  * by a thread. So, we should never get into the situation where
	  * all threads are waiting and there are unavailable commands.
     * @author David Wegener
     * @author Craig Murphy
     * @return ThreadCommand the command to be executed.
     */
    public synchronized ThreadCommand getNextCommand()
    {
        ThreadCommand retVal = null;
        while ( ( !isEmpty() || !shuttingDown ) && null == retVal )
        {
            retVal = unavailableQueue.getNextCommand();
            if ( null == retVal )
            {
                try
                {
                    while ( null != ( retVal = primaryQueue.getNextCommand() ) && !retVal.isAvailable() )
                    {
                        unavailableQueue.insertCommand( retVal );
                        retVal = null;
                    }
                }
                catch ( Throwable t )
                {
                    System.err.println( "Received throwable getting next command: " );
                    t.printStackTrace( System.err );
                    retVal = null;
                }
            }
            if ( null == retVal )
            {
                threadPool.addIdleThread(Thread.currentThread());
                try
                {
                    wait();
                }
                catch (InterruptedException e)
                {
                }
                threadPool.removeIdleThread(Thread.currentThread());
            }
        }
        if (retVal == null)
        {
            notify();
        }
        return retVal;
    }
    /**
     * Method just wakes us up so we can shut down in a proper manner
     */
    public synchronized Enumeration shutdown( boolean quickShutdown )
    {
        Enumeration retVal = null;
        if ( quickShutdown )
        {
            retVal = clearQueue();
        }
        // Note that no new commands will be getting sent in
        shuttingDown = true;
        while ( !isEmpty() )
        {
            try
            {
                wait();
            }
            catch ( InterruptedException ie )
            {
            }
        }
        notifyAll(); // Wake up and shut down any waiting threads
        return retVal;
    }

    public synchronized Enumeration clearQueue()
    {
        Enumeration clearedCommands =
            new QueueEnumerator( unavailableQueue.clearQueue(), primaryQueue.clearQueue() );
        notify();
        return clearedCommands;
    }

    /**
     * Create a class for generating an enumeration of the data
     */
    private class QueueEnumerator implements Enumeration
    {

        private Enumeration unavailableData;
        private Enumeration primaryData;
        QueueEnumerator( Enumeration unavailableData, Enumeration primaryData )
        {
            this.unavailableData = unavailableData;
            this.primaryData = primaryData;
        }
        public boolean hasMoreElements()
        {
            return   null != unavailableData && unavailableData.hasMoreElements() ||
                     null != primaryData && primaryData.hasMoreElements();
        }
        public Object nextElement()
        {
            Object retVal = null;
            if ( null != unavailableData )
            {
                if ( unavailableData.hasMoreElements() )
                {
                    retVal = unavailableData.nextElement();
                }
                else
                {
                    unavailableData = null;
                }
            }
            if ( null == retVal && null != primaryData )
            {
                if ( primaryData.hasMoreElements() )
                {
                    retVal = primaryData.nextElement();
                }
                else
                {
                    unavailableData = null;
                }
            }
            return retVal;
        }
    }

}
