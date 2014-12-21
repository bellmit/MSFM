package com.cboe.domain.marketData.mdhQueueEntry;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryService;

public class MDHProductStateChangeEntry implements MDHQueueEntry
{
	public String sessionName;

	public int productKey;

	public Price underlyingPrice;

	public short productState;

	public MDHProductStateChangeEntry(String sessionName, int productKey, Price underlyingPrice, short productState)
	{
		this.sessionName = sessionName;
		this.productKey = productKey;
		this.productState = productState;
		this.underlyingPrice = underlyingPrice;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer(2000);
		
		buffer.append("MDH product state change entry\n");
		buffer.append(MDHStructFormatter.formatStruct(underlyingPrice.toStruct(), "UnderlyingPrice"));
		buffer.append("ProductState=").append(productState).append("\n");
		buffer.append("ProductKey=").append(productKey).append("\n");
		buffer.append("SessionName=").append(sessionName).append("\n");
		
		return buffer.toString();
	}	
	public void execute(MarketDataHistoryService service)
	{
		service.createProductStateChangeEntry(sessionName, productKey, underlyingPrice, productState);
	}

	public int getProductKey()
	{
		return productKey;
	}

}
