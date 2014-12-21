package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.idl.cmiMarketData.MarketVolumeStruct;

import com.cboe.presentation.marketData.MarketVolumeStructHelper;

/**
  Compares PriceStruct instances
 */
public class MarketVolumeStructSequenceComparator implements Comparator<MarketVolumeStruct[]>
{
    /**
      Implements Comparator.
    */
    public int compare(MarketVolumeStruct[] mvs1, MarketVolumeStruct[] mvs2)
    {
        int result = -1;
        int qty1 = (mvs1 == null) ? 0 : MarketVolumeStructHelper.getQuantityValue(mvs1);
        int qty2 = (mvs2 == null) ? 0 : MarketVolumeStructHelper.getQuantityValue(mvs2);
        if (qty1 == qty2)
        {
            result = 0;
        }
        else if (qty1 > qty2)
        {
            result = 1;
        }
        return result;
    }
}

