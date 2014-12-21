// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryFactory
//
// PACKAGE: com.cboe.presentation.marketData
// 
// Created: Jul 9, 2004 3:17:17 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.marketData.*;
import com.cboe.idl.cmiMarketData.*;

public abstract class MarketDataHistoryFactory
{
    public static MarketDataHistory createMarketDataHistory(MarketDataHistoryStruct struct)
    {
        return new MarketDataHistoryImpl(struct);
    }

    public static MarketDataHistoryEntry createMarketDataHistoryEntry(MarketDataHistoryEntryStruct struct)
    {
        return new MarketDataHistoryEntryImpl(struct);
    }

    public static MarketDataDetail createMarketDataDetail(MarketDataDetailStruct struct)
    {
        return new MarketDataDetailImpl(struct);
    }

    public static MarketDataHistoryDetailEntry createMarketDataHistoryDetailEntry(MarketDataHistoryDetailEntryStruct struct)
    {
        return new MarketDataHistoryDetailEntryImpl(struct);
    }

    public static MarketDataHistoryDetail createMarketDataHistoryDetail(MarketDataHistoryDetailStruct struct)
    {
        return new MarketDataHistoryDetailImpl(struct);
    }

} // -- end of class MarketDataHistoryFactory
