package com.cboe.interfaces.domain;

public interface QHQueueEntry
{
	void execute(QuoteHistoryService service);

	int getProductKey();
}
