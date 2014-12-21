package com.cboe.domain.util;

import com.cboe.interfaces.internalBusinessServices.HistoryServiceStatisticsManager;
import com.cboe.domain.util.HistoryServiceStatisticsManagerImpl;


/**
 * Stats manager for market data history service.
 * @author singh
 *
 */

public class MarketDataHistoryServiceStatisticsManager
{
	private static HistoryServiceStatisticsManager sInstance;
	
	static
	{
		sInstance = new HistoryServiceStatisticsManagerImpl ();
	}
	
	public static HistoryServiceStatisticsManager getInstance ()
	{
		return sInstance;
	}
}
