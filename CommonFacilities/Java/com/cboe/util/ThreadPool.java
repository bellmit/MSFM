package com.cboe.util;

import java.util.*;
/**
 * This class manages a pool of threads.  It accepts commands
 * to perform and assigns them to a thread.
 */
public class ThreadPool implements Runnable
{
	protected transient ThreadCommandQueue commands;
	protected ThreadGroup poolGroup;
	protected String poolName;
    protected boolean quickShutdown; // Do we want to execute scheduled commands or just exit?
    protected boolean shutdown;
    protected int threadNumber;
    protected HashSet idleSet;

    /**
     * This constructor builds a new ThreadPool with a the
     * specified number of threads and a ThreadGroup name of name.
     * @param numThreads the number of threads to start with
     * @param name the name of the ThreadGroup to create the
     * threads in.
     */
    public ThreadPool(int numThreads, String name)
    {
        poolName = name;
        commands = new ThreadCommandQueue(this);
        poolGroup = new ThreadGroup(poolName);
        shutdown = false;
        quickShutdown = false;
        threadNumber = 0;
        idleSet=new HashSet();
        for ( int i = 0; i < numThreads; i++ )
        {
          createWorkThread();
        }
    }
    /**
     * Set whether or not we want a quick shutdown
     * @param boolean quickShutdown
     */
    public void setQuickShutdown( boolean quickShutdown )
    {
        this.quickShutdown = quickShutdown;
    }
    /**
     * This method will clear the queue of commands and
     * return an enumeration of the cleared commands to
     * the caller.
     * @return Enumeration of commands cleared.
     */
    public Enumeration clearQueue()
    {
        return commands.clearQueue();
    }
    /**
     * This method creates a new WorkThread and starts it up.
     * @return WorkThread the new thread
     */
    public synchronized WorkThread createWorkThread()
    {
        WorkThread newWorker = new WorkThread(poolGroup, commands, threadNumber++);
        newWorker.start();
        return newWorker;
    }
    public ThreadGroup getPoolGroup()
    {
        return poolGroup;
    }
    public String getName()
    {
        return poolName;
    }

    public synchronized void addIdleThread(Thread thread)
    {
        idleSet.add(thread);
    }

    public synchronized void removeIdleThread(Thread thread)
    {
        idleSet.remove(thread);
    }

    public int getIdleThreadCount()
    {
        return idleSet.size();
    }

    /**
     * This method allows the caller to delay until the specified
     * thread has completed execution.  The command is scheduled
     * behind all existing commands so the caller may be blocked
     * for quite some time waiting for the command to complete.  If
     * the thread pool is shutting down, the method will return an
     * InvalidThreadPoolStateException.
     * @param command the command to be performed
     * @exception InvalidThreadPoolStateException when thread pool is shutting down
     */
    public void perform(ThreadCommand command) throws InvalidThreadPoolStateException
    {
        command.perform( this );
    }

    /**
     * This method allows the caller to schedule a command for execution
     * when a thread becomes available.  The method will return when
     * the command has been scheduled but not necessarily completed.  The
     * method will throw an InvalidThreadPoolStateException if a command
     * is attempted to be scheduled while the thread pool is shutting down.
     * @param command - the command to be scheduled.
     * @exception InvalidThreadPoolStateException when thread pool is shutting down.
     */
    public void schedule(ThreadCommand command) throws InvalidThreadPoolStateException
    {
        commands.insertCommand( command );
    }
    /**
     * This method will cause the thread pool to shutdown.  It
     * will first prevent any new commands from being scheduled.
     * It will then stop all of the threads, the monitor thread,
     * and destroy the ThreadGroup.
     */
    public Enumeration shutdown()
    {
        shutdown = true;
        Enumeration retVal = commands.shutdown( quickShutdown );
        Thread shutdownThread = new Thread(this);
        shutdownThread.start();
        return retVal;
    }

    public void run()
    {
        while (!shutdown){}
        while ( 0 < poolGroup.activeCount() )
        {
            try
            {
                Thread.sleep( 500 ); // Try every 0.5 seconds
            }
            catch (InterruptedException e) { }
	    }
        if(!poolGroup.isDestroyed())
        {
            poolGroup.destroy();
        }
    }
}
