package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.CurrentMarketViewTypes;
import com.cboe.idl.cmiConstants.VolumeTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.interfaces.domain.MarketUpdate;
import com.cboe.interfaces.domain.Price;

public class OpraBestLimitMarketsHelper
{
	// MAX_SIZE is the max. size that can be sent to OPRA.
	private static final int MAX_SIZE = 99999;

	public static int getAskSideQuantity (final CurrentMarketStruct cm)
	{
		return getCount (cm.askSizeSequence);
	}

	public static int getBidSideQuantity (final CurrentMarketStruct cm)
	{
		return getCount (cm.bidSizeSequence);
	}
	
	public static Price getAskPrice(final CurrentMarketStruct cm)
	{
		return PriceFactory.create(cm.askPrice);
	}

	public static Price getBidPrice(final CurrentMarketStruct cm)
	{
		return PriceFactory.create(cm.bidPrice);
	}
	
	public static void getCurrentMarketToOpra (final CurrentMarketStruct cm, OpraCurrentMarketStruct struct)
	{
		struct.askPrice = getAskPrice(cm);
		struct.bidPrice = getBidPrice(cm);
		struct.askQuantity = getAskSideQuantity(cm);
		struct.bidQuantity = getBidSideQuantity(cm);
	}
	
	public static void getCurrentMarketToOpra (final MarketUpdate mu, OpraCurrentMarketStruct struct)
	{
	    struct.askPrice = mu.getAsk().getBestLimitPrice();
	    struct.bidPrice = mu.getBid().getBestLimitPrice();
	    struct.askQuantity = mu.getAsk().getTotalVolume(CurrentMarketViewTypes.BEST_LIMIT_PRICE);
	    if (struct.askQuantity > MAX_SIZE)
	    {
	        struct.askQuantity = MAX_SIZE;
	    }
	    struct.bidQuantity = mu.getBid().getTotalVolume(CurrentMarketViewTypes.BEST_LIMIT_PRICE);
	    if (struct.bidQuantity > MAX_SIZE)
	    {
	        struct.bidQuantity = MAX_SIZE;
	    }
	}
	
	private static int getCount(final MarketVolumeStruct[] sequence)
	{
		int rval = 0;
		
	    // Sum up all the ask volumes
        for (int i = 0; i < sequence.length; i++)
        {
            if ((sequence[i].volumeType == VolumeTypes.LIMIT)
                || (sequence[i].volumeType == VolumeTypes.IOC))
            {
                rval += sequence[i].quantity;
            }
        }
        // check if the size exceeds OPRA max volume
        return (rval < MAX_SIZE) ? rval : MAX_SIZE;
	}
}
