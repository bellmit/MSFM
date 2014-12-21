package com.cboe.domain.marketData.mdhQueueEntry;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryService;

public class MDHCurrentMarketEntry implements MDHQueueEntry
{
	public CurrentMarketStruct bestMarket;

	public CurrentMarketStruct bestLimitMarket;

	public CurrentMarketStruct bestPublicMarket;

	public Price underlyingPrice;

	public short productState;

	public NBBOStruct nbboStruct;

	public NBBOStruct botrStruct;

	public ExchangeIndicatorStruct[] exchangeIndicatorStruct;

	public long entryTime;
	
	public String location;

	/**
	 * Constructs a new entry.
	 */
	public MDHCurrentMarketEntry(CurrentMarketStruct bestMarket, CurrentMarketStruct bestLimitMarket,
			CurrentMarketStruct bestPublicMarket, NBBOStruct nbboStruct, NBBOStruct botrStruct,
			ExchangeIndicatorStruct[] exchangeIndicatorStruct, Price underlyingPrice, short productState, long entryTime,
			String location)
	{
		this.bestMarket = bestMarket;
		this.bestLimitMarket = bestLimitMarket;
		this.bestPublicMarket = bestPublicMarket;
		this.nbboStruct = nbboStruct;
		this.botrStruct = botrStruct;
		this.underlyingPrice = underlyingPrice;
		this.productState = productState;
		this.exchangeIndicatorStruct = exchangeIndicatorStruct;
		this.entryTime = entryTime;
		this.location = location;
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(2000);
		
		buffer.append("MDH Current market entry\n");
		buffer.append("EntryTime=").append(entryTime).append("\n");
		buffer.append(MDHStructFormatter.formatStruct(bestMarket, "BestMarket"));
		buffer.append(MDHStructFormatter.formatStruct(bestLimitMarket, "BestLimitMarket"));
		buffer.append(MDHStructFormatter.formatStruct(bestPublicMarket, "BestPublicMarket"));
		buffer.append(MDHStructFormatter.formatStruct(nbboStruct, "NBBOStruct"));
		buffer.append(MDHStructFormatter.formatStruct(botrStruct, "BOTRStruct"));
		buffer.append(MDHStructFormatter.formatStruct(underlyingPrice.toStruct(), "UnderlyingPrice"));
		buffer.append("ProductState=").append(productState).append("\n");
		buffer.append("Location=").append(location).append("\n");
		
		for (int i = 0; i < exchangeIndicatorStruct.length; i++)
		{
			buffer.append("ExchangeIndicatorStruct#").append(i).append("\n");
			buffer.append(MDHStructFormatter.formatStruct(exchangeIndicatorStruct[i], "EI"));
		}
		
		return buffer.toString();
	}

	public void execute(MarketDataHistoryService service)
	{
		service.createCurrentMarketEntry(bestMarket, bestLimitMarket, bestPublicMarket, nbboStruct, botrStruct,
				exchangeIndicatorStruct, underlyingPrice, productState, entryTime, location);
	}

	public int getProductKey()
	{
		return bestMarket.productKeys.productKey;
	}
}
