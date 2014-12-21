package com.cboe.domain.marketData.mdhQueueEntry;

import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryService;

public class MDHLastSaleEntry implements MDHQueueEntry
{
	public Price underlyingPrice;
	public short productState;
	public InternalTickerDetailStruct lastSale;
	public long entryTime;
	
	public MDHLastSaleEntry(TimeStruct tradeTime, InternalTickerDetailStruct lastSale, Price underlyingPrice, short productState, long entryTime)
	{
        lastSale.lastSaleTicker.tradeTime = tradeTime;
        this.lastSale = lastSale;
        this.underlyingPrice = underlyingPrice;
        this.productState = productState;
        this.entryTime = entryTime;
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(2000);
		
		buffer.append("MDH Last sale entry\n");
		buffer.append("EntryTime=").append(entryTime).append("\n");
		buffer.append(MDHStructFormatter.formatStruct(lastSale, "LastSale"));
		buffer.append(MDHStructFormatter.formatStruct(underlyingPrice.toStruct(), "UnderlyingPrice"));
		buffer.append("ProductState=").append(productState).append("\n");
		
		return buffer.toString();
	}
	
	public void execute(MarketDataHistoryService service)
	{
		service.createTickerEntry (lastSale,underlyingPrice, productState,entryTime);
	}

	public int getProductKey()
	{
		return lastSale.lastSaleTicker.ticker.productKeys.productKey;
	}

}
