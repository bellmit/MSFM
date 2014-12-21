//
// -----------------------------------------------------------------------------------
// Source file: MarketVolumeFactory.java
//
// PACKAGE: com.cboe.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.idl.cmiMarketData.MarketVolumeStruct;

import com.cboe.interfaces.presentation.marketData.MarketVolume;

public class MarketVolumeFactory
{
    public static MarketVolume create(MarketVolumeStruct marketVolumeStruct)
    {
        return new MarketVolumeImpl(marketVolumeStruct);
    }

    public static MarketVolume[] create(MarketVolumeStruct[] marketVolumeStructs)
    {
        MarketVolume[] marketVolumes = new MarketVolume[marketVolumeStructs.length];
        for (int i = 0; i < marketVolumes.length; i++)
        {
            marketVolumes[i] = create(marketVolumeStructs[i]);
        }

        return marketVolumes;
    }
}
