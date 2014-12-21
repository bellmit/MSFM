package com.cboe.util;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ObjectPoolImpl<T extends Copyable> implements ObjectPool<T>
{
	private int capacity;
	private int increment;
	private AtomicBoolean autoGrow;
	private T template;
	private ThreadLocal<ObjectCartridge<T>> cartridge = new ThreadLocal<ObjectCartridge<T>>();
	private ConcurrentLinkedQueue<ObjectCartridge<T>> emptyCartridgePool = new ConcurrentLinkedQueue<ObjectCartridge<T>>();
	private ConcurrentLinkedQueue<ObjectCartridge<T>> fullCartridgePool = new ConcurrentLinkedQueue<ObjectCartridge<T>>();
	private boolean verbose = false;
	static boolean logOverflow = System.getProperty("ObjectPool.logPoolOverflow", "false").equals("true");
	static boolean traceMode = System.getProperty("ObjectPool.traceMode", "false").equals("true");
	private int maxNumSegments;
	public ObjectPoolImpl(int capacity, int increment, T clasz)
	{
		this.capacity = capacity;
		this.increment = increment;
		this.template = clasz;
		autoGrow = new AtomicBoolean (increment > 0);
		verbose = getVerboseFlag ();
	}

	private boolean getVerboseFlag()
	{
		return System.getProperty("ObjectPool.verbose." + template.getClass().getName(), "off").compareToIgnoreCase("on") == 0;
	}
	
	
	private boolean isVerbose()
	{
		return verbose;
	}

	public T checkOut()
	{
		ObjectCartridge<T> cartridge = getFullCartridge();

		T rval = cartridge.remove();
		
		rval.setAcquiringThreadId(Thread.currentThread().getId());
		
		logCartridge (cartridge, "post checkout", rval.hashCode());

		return rval;
	}
	
	private void logMsg (String msg)
	{
		System.err.println ("<INFO> < " + (new Date()).toString() + " > < " + 
				Thread.currentThread().getId() + "/" + Thread.currentThread().getName() + 
				"> " + msg);
	}
	
	private void logCartridge(ObjectCartridge<T> cartridge, String msg, int hash)
	{
		if (isVerbose())
		{
			logMsg ("ObjectPoolImpl for " + this.template.getClass().getCanonicalName() + 
					" " + msg + " hash = " + hash + ", size = " + cartridge.size() + ", capacity = " + cartridge.capacity() + 
					", capacity increment = " + cartridge.getCapacityIncrement());
		}
	}

	public void checkIn(T elem)
	{
		if (elem != null)
		{
		    elem.clear();
			boolean autoGrowFlag = autoGrow();
			
			if (autoGrowFlag && (elem.getAcquiringThreadId() != Thread.currentThread().getId()))
			{
				this.autoGrow.set(false);
				
				logMsg ("ObjectPoolImpl for " + this.template.getClass().getCanonicalName() +
                        ". Switching strategy to object allocation/release across threads. Acquiring thread id = " + elem.getAcquiringThreadId() +
                        ", releasing thread id = " + Thread.currentThread().getId());

			}
			
			ObjectCartridge cartridge = getEmptyCartridge();
			
			cartridge.add(elem);

			logCartridge (cartridge, "post checkin", elem.hashCode());
		}
	}

	private ObjectCartridge<T> createEmptyCartridge()
	{
		ObjectCartridge<T> rval = new ObjectCartridge<T>(getCapacity(), getCapacityIncrement(),
				getTemplateObj(), false);

		return rval;
	}

	private ObjectCartridge<T> createFullCartridge()
	{
		ObjectCartridge rval = new ObjectCartridge<T>(getCapacity(), getCapacityIncrement(),
				getTemplateObj(), true);

		return rval;
	}

	private int getCapacity()
	{
		return capacity;
	}
	
	private int getCapacityIncrement()
	{
		return increment;
	}
	
	private boolean autoGrow ()
	{
		return autoGrow.get();
	}

	private ObjectCartridge<T> getEmptyCartridge()
	{
		ObjectCartridge<T> empty = cartridge.get();

		if ((empty == null) || (empty.isFull() && (autoGrow() == false)))
		{
			returnToFullPool(empty);

			empty = getFromEmptyPool();

			cartridge.set(empty);
		}

		return empty;
	}

	private ObjectCartridge<T> getFromEmptyPool()
	{
		ObjectCartridge<T> rval = emptyCartridgePool.poll();

		if (rval == null)
		{
			rval = createEmptyCartridge();
		}

		return rval;
	}

	private ObjectCartridge<T> getFromFullPool()
	{
		ObjectCartridge<T> rval = fullCartridgePool.poll();

		if (rval == null)
		{
			rval = createFullCartridge();
		}

		return rval;
	}

	private ObjectCartridge<T> getFullCartridge()
	{
		ObjectCartridge<T> full = cartridge.get();
		
		if ((full != null) && this.autoGrow())
		{
			full.ensure();
		}

		if ((full == null) || full.isEmpty())
		{
			returnToEmptyPool(full);

			full = getFromFullPool();

			cartridge.set(full);
		}

		return full;
	}

	private T getTemplateObj()
	{
		return template;
	}

	private void returnToEmptyPool(ObjectCartridge<T> full)
	{
		if (full != null)
		{
			emptyCartridgePool.offer(full);
		}
	}

	private void returnToFullPool(ObjectCartridge<T> empty)
	{
		if (empty != null)
		{
			fullCartridgePool.offer(empty);
		}
	}
	
	public boolean isLogOverflow()
    {
        return logOverflow;
    }

    public void setLogOverflow(boolean p_logOverflow)
    {
        logOverflow = p_logOverflow;
    }

    public boolean isTraceMode()
    {
        return traceMode;
    }

    public void setTraceMode(boolean p_traceMode)
    {
        traceMode = p_traceMode;
    }
    
    public int getMaxNumSegments()
    {
        return maxNumSegments;
    }

    public void setMaxNumSegments(int p_maxNumSegments)
    {
        maxNumSegments = p_maxNumSegments;
    }
    
    public void undoObjectPooling(StringBuffer returnValue)
    {
    }
    
    public void addMoreSegments(StringBuffer returnValue, int numberOfSegmentsToBeAdded)
    {
    }
}

