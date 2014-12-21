package com.cboe.domain.marketData.mdhQueueEntry;

import com.cboe.idl.internalBusinessServices.MarketDataHistoryEntriesStruct;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryService;

public class MDHEntriesStruct implements MDHQueueEntry
{
	public String blockId;

	public MarketDataHistoryEntriesStruct[] entries;

	public MDHEntriesStruct(String blockId, MarketDataHistoryEntriesStruct[] entries)
	{
		this.blockId = blockId;
		this.entries = entries;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer(2000);

		buffer.append("BlockId=").append(blockId).append("\n");

		for (int i = 0; i < entries.length; i++)
		{
			buffer.append("MarketDataHistoryEntriesStruct#").append(i).append("\n");
			buffer.append(MDHStructFormatter.formatStruct(entries[i], "entry"));
		}

		return buffer.toString();
	}

	public void execute(MarketDataHistoryService service)
	{
		try
		{
			service.createBlockedEntries(blockId, entries);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public int getProductKey()
	{
		return Math.abs(blockId.hashCode());
	}

}
