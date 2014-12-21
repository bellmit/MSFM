package com.cboe.domain.marketData.mdhQueueEntry;

import com.cboe.interfaces.domain.marketData.MarketDataHistoryEntry;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryService;

public class MDHEntryStruct implements MDHQueueEntry
{
	public MarketDataHistoryEntry entry;

	public MDHEntryStruct(MarketDataHistoryEntry entry)
	{
		this.entry = entry;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer(2000);

		buffer.append(MDHStructFormatter.formatStruct(entry.toMarketDataHistoryEntryStructV1Struct(), "entry"));

		return buffer.toString();
	}

	public void execute(MarketDataHistoryService service)
	{
		service.createEntry(entry);
	}

	public int getProductKey()
	{
		return entry.getProductKey();
	}
}
