/*
 * Created on Apr 20, 2005
 *
 * This is a lightwt. objectpool for MDH.
 */
package com.cboe.domain.marketData;

import com.cboe.interfaces.domain.HistoryServiceIdGenerator;

/**
 * @author singh
 * 
 */
public class MarketDataHistoryObjectPool
{
	private MarketDataHistoryEntryImpl[] pool;

	private int size;

	private int count;

	private static HistoryServiceIdGenerator idGenerator;

	static public void setIdGeneratorStrategy(HistoryServiceIdGenerator idGeneratorStrategy)
	{
		idGenerator = idGeneratorStrategy;
	}

	public static HistoryServiceIdGenerator getIdGenerator()
	{
		return idGenerator;
	}

	public MarketDataHistoryObjectPool()
	{

	}

	public void init(int size)
	{
		this.size = size;
		count = size;

		pool = new MarketDataHistoryEntryImpl[size];

		for (int i = 0; i < size; i++)
		{
			pool[i] = create();
		}
	}

	public static MarketDataHistoryEntryImpl create()
	{
		MarketDataHistoryEntryImpl impl = new MarketDataHistoryEntryImpl(true, getIdGenerator());

		impl.init();

		return impl;
	}

	public MarketDataHistoryEntryImpl acquire()
	{
		MarketDataHistoryEntryImpl impl = count > 0 ? pool[--count] : create();

		impl.init();

		return impl;
	}

	public void releaseAll()
	{
		count = size;
	}
}
