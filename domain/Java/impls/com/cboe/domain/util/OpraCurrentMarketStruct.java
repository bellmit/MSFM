package com.cboe.domain.util;

import com.cboe.interfaces.domain.Price;

/*
 * This struct is used by OpraBestLimitMarketsHelper. Its 
 * fields are the bid/ask price/size info sent to Opra
 * from CoppBridge. This info is used in HoldbackTimer
 * to not send duplicates to Opra
 */

public class OpraCurrentMarketStruct
{
	public Price askPrice;
	public Price bidPrice;
	public int   askQuantity;
	public int   bidQuantity;
	
	@Override
	public boolean equals(Object obj)
	{
		OpraCurrentMarketStruct other = (OpraCurrentMarketStruct) obj;
		
		if (other != null)
		{
			return other.askPrice.equals(askPrice) && other.bidPrice.equals(bidPrice) &&
			 	(other.askQuantity == askQuantity) && (other.bidQuantity == bidQuantity);
		}
		
		return super.equals(obj);
	}

	public void set(OpraCurrentMarketStruct from)
	{
		this.askPrice = from.askPrice;
		this.askQuantity = from.askQuantity;
		this.bidPrice = from.bidPrice;
		this.bidQuantity = from.bidQuantity;
	}

    @Override
    public String toString()
    {
        StringBuffer buf = new StringBuffer ();
        buf.append(bidQuantity).append('x').append(bidPrice).append('-').append(askPrice).append('x').append(askQuantity);
        
        return buf.toString();
    }
}