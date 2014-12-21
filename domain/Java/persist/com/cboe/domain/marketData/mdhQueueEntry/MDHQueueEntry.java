package com.cboe.domain.marketData.mdhQueueEntry;

import com.cboe.interfaces.domain.marketData.MarketDataHistoryService;

public interface MDHQueueEntry
{
	/**
	 * Executes entry read from queue.
	 */
	void execute(MarketDataHistoryService service);

	int getProductKey();
}
