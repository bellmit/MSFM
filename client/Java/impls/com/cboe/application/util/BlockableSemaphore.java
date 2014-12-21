package com.cboe.application.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Gijo Joseph
 */
public class BlockableSemaphore {
    private SemaphoreExt semaphore;
    private AtomicBoolean semaphoreBlocked = new AtomicBoolean(false); 
    private final Lock lock = new ReentrantLock();
    private final Condition notBlocked = lock.newCondition();
    private int blockCount;
    private long semaphoreWaitTimeout; 
    private long blockWaitTimeout;
    private int totalPermits;
    public static final String SEMAPHORE_TIMEOUT = "SEMAPHORE_TIMEOUT";
    public static final String BLOCK_TIMEOUT = "BLOCK_TIMEOUT";
    
    
	public BlockableSemaphore(int permits, long semaphoreWaitTimeout, long blockWaitTimeout) {
		semaphore = new SemaphoreExt(permits);
		totalPermits = permits;
		this.blockWaitTimeout = blockWaitTimeout;
		this.semaphoreWaitTimeout = semaphoreWaitTimeout;
	}

	public BlockableSemaphore(int permits, boolean fair, long semaphoreWaitTimeout, long blockWaitTimeout) {
		semaphore = new SemaphoreExt(permits, fair);
        totalPermits = permits;
		this.blockWaitTimeout = blockWaitTimeout;
		this.semaphoreWaitTimeout = semaphoreWaitTimeout;
	}

	public boolean isBlocked()
	{
		return semaphoreBlocked.get();
	}
	
	public boolean acquireNoWait()
	{
		return semaphore.tryAcquire();
	}
	
	
	public boolean acquireNoWait(int permits)
	{
		return semaphore.tryAcquire(permits);
	}
	
	public void release()
	{
		semaphore.release();
	}
	
	public void release(int permits)
	{
		semaphore.release(permits);
	}
	
	public synchronized void setSemaphoreBlock()
	{
		semaphoreBlocked.set(true);
		++blockCount;
	}
	
	public synchronized void resetSemaphoreBlock(boolean notifyBlocked)
	{
		if (--blockCount <= 0)
		{
			semaphoreBlocked.set(false);
			if (notifyBlocked)
			{
				lock.lock();
				try
				{
					notBlocked.signalAll();
				}
				finally
				{
					lock.unlock();
				}
			}
		}
	}
    
    public int getSemaphoresUsed()
    {
        return totalPermits - semaphore.availablePermits();
    }

    public void acquire(boolean interruptable, boolean blockTimeout, boolean semaphoreTimeout) throws InterruptedException, TimeoutException
	{
		if (semaphoreBlocked.get())
		{
			lock.lock();
			try
			{
				if (blockTimeout && blockWaitTimeout > 0L)
				{
					if (!(notBlocked.await(blockWaitTimeout, TimeUnit.MILLISECONDS)))
					{
						throw new TimeoutException(BLOCK_TIMEOUT);
					}					
				}
				else
				{
					notBlocked.await();
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		if (semaphoreTimeout && semaphoreWaitTimeout > 0L)
		{
			if (!(semaphore.tryAcquire(semaphoreWaitTimeout, TimeUnit.MILLISECONDS)))
			{
				throw new TimeoutException(SEMAPHORE_TIMEOUT);			
			}
		}
		else
		{
			if (interruptable)
			{
				semaphore.acquire();
			}
			else
			{
				semaphore.acquireUninterruptibly();
			}
		}
	}
	
	public void acquire(int permits, boolean interruptable, boolean blockTimeout, boolean semaphoreTimeout) throws InterruptedException, TimeoutException
	{
		if (semaphoreBlocked.get())
		{
			lock.lock();
			try
			{
				if (blockTimeout && blockWaitTimeout > 0L)
				{
					if (!(notBlocked.await(blockWaitTimeout, TimeUnit.MILLISECONDS)))
					{
						throw new TimeoutException(BLOCK_TIMEOUT);
					}					
				}
				else
				{
					notBlocked.await();
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		if (semaphoreTimeout && semaphoreWaitTimeout > 0L)
		{
			if (!(semaphore.tryAcquire(permits, semaphoreWaitTimeout, TimeUnit.MILLISECONDS)))
			{
				throw new TimeoutException(SEMAPHORE_TIMEOUT);			
			}
		}
		else
		{
			if (interruptable)
			{
				semaphore.acquire(permits);
			}
			else
			{
				semaphore.acquireUninterruptibly(permits);
			}
		}
	}
	
	public void cancelPendingThreads()
	{
    	semaphore.cancelPendingThreads();		
	}
	
	
	private class SemaphoreExt extends java.util.concurrent.Semaphore 
	{
	    private static final long serialVersionUID = -6222578661600680210L;

	    SemaphoreExt(int permits)
		{
			super(permits);
		}

		SemaphoreExt(int permits, boolean fair)
		{
			super(permits, fair);
		}
		
		public void cancelPendingThreads()
		{
	    	if (hasQueuedThreads())
	    	{
	    		for (Thread t: getQueuedThreads())
	    		{
	    			t.interrupt();
	    		}
	    	}		
		}
	}
	
}
