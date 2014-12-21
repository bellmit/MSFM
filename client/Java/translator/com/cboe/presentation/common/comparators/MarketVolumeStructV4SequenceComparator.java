//
// -----------------------------------------------------------------------------------
// Source file: MarketVolumeStructV4SequenceComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2010 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;
import com.cboe.presentation.marketData.MarketVolumeStructHelper;

import java.util.Comparator;

public class MarketVolumeStructV4SequenceComparator implements Comparator<MarketVolumeStructV4[]>
{
    public int compare(MarketVolumeStructV4[] mvs1, MarketVolumeStructV4[] mvs2)
    {
        Integer qty1 = (mvs1 == null) ? 0 : MarketVolumeStructHelper.getQuantityValue(mvs1);
        Integer qty2 = (mvs2 == null) ? 0 : MarketVolumeStructHelper.getQuantityValue(mvs2);
        return qty1.compareTo(qty2);
    }
}
