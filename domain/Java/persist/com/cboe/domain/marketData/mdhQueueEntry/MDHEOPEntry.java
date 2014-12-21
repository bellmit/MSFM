package com.cboe.domain.marketData.mdhQueueEntry;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryService;

public class MDHEOPEntry implements MDHQueueEntry
{
	public ExpectedOpeningPriceStruct expectedOpenPrice;

	public Price underlyingPrice;

	public short productState;

	public long entryTime;

	public MDHEOPEntry(ExpectedOpeningPriceStruct expectedOpenPrice, Price underlyingPrice, short productState, long entryTime)
	{
		this.expectedOpenPrice = expectedOpenPrice;
		this.underlyingPrice = underlyingPrice;
		this.productState = productState;
		this.entryTime = entryTime;
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(2000);
		
		buffer.append("MDH Expected opening price entry\n");
		buffer.append("ProductState=").append(productState).append("\n");
		buffer.append("EntryTime=").append(entryTime).append("\n");
		buffer.append(MDHStructFormatter.formatStruct(expectedOpenPrice, "eop"));
		buffer.append(MDHStructFormatter.formatStruct(underlyingPrice.toStruct(), "UnderlyingPrice"));
		
		return buffer.toString();
	}	

	public void execute(MarketDataHistoryService service)
	{
		service.createEOPEntry(expectedOpenPrice, underlyingPrice, productState, entryTime);
	}

	public int getProductKey()
	{
		return expectedOpenPrice.productKeys.productKey;
	}
}
