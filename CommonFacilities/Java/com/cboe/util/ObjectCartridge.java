package com.cboe.util;

import java.util.ArrayList;
import java.util.Date;

public final class ObjectCartridge<T extends Copyable>
{
	private ArrayList<T> list;
	private T copyable;
	private int capacityIncrement;
	private int capacity;
	private boolean overflow = false;

	public ObjectCartridge(int capacity, int capacityIncrement, T clasz, boolean full)
	{
		list = new ArrayList<T>(capacity);

		this.capacity = 0;
		this.capacityIncrement = capacityIncrement;

		copyable = clasz;

		if (full)
		{
			ensureAvailabilty (capacity);
		}
		else
		{
			this.capacity = capacity;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void ensureAvailabilty (final int incrementSize)
	{
        for (int i = 0; i < incrementSize; i++)
        {
            try
            {
                T newInstance = (T) copyable.copy();
                list.add(newInstance);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        this.capacity += incrementSize;
	}
	
	public void ensure ()
	{
		if (isEmpty() && (getCapacityIncrement() > 0))
		{
			ensureAvailabilty (getCapacityIncrement());
		}
	}

	public int getCapacityIncrement()
	{
		return this.capacityIncrement;
	}

	public T remove()
	{
		return list.get(list.size()-1);
	}
	
	public int size ()
	{
		return list.size();
	}
	
	public int capacity ()
	{
		return capacity;
	}

	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public boolean isFull()
	{
		return list.size() == capacity;
	}

	public boolean add(T elem)
	{
		if (isFull())
		{
		    if (!overflow)
		    {
		        overflow = true;

		        System.err.println ((new Date()).toString () + " WARNING: Object pool for <" + elem.getClass().getName() + "> not configured properly. " + 
		                "Thread " + Thread.currentThread().getId() + "/" + Thread.currentThread().getName() + 
		        " is releasing more objects to its thread local object pool. This can happen when some other thread is acquiring objects and this thread is releasing it. Check Object pool configuration for this object.");
		    }
		}
		else
		{
		    list.add(elem);
		}
		return isFull();
	}
}

