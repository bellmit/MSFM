package com.cboe.util;

/**
   This class is a thread in a thread pool.  It accepts
   a ThreadCommand to execute.
 */
public class WorkThread extends Thread
{
	private ThreadCommandQueue queue;
    /**
     * This constructor creates a new WorkThread associated
     * with the threadGroup in the pool.
     */
    public WorkThread(ThreadGroup threadGroup, ThreadCommandQueue queue, int number ) 
    {
        super(threadGroup, "WorkerThread" + number );
        this.queue = queue;
    }
    public void run() 
    {
        ThreadCommand command;
        while ( null != ( command = queue.getNextCommand() ) )
        {
            command.work();
            command.flagFinished();
        }
    }
}
