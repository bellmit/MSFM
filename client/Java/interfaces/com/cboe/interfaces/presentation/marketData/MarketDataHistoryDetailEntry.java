// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryDetailEntry
//
// PACKAGE: com.cboe.interfaces.presentation.marketData
// 
// Created: Jul 9, 2004 10:40:44 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailEntryStruct;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface MarketDataHistoryDetailEntry extends BusinessModel
{
    public MarketDataHistoryDetailEntryStruct getStruct();

    public MarketDataHistoryEntry getMarketDataHistoryEntry();

    public MarketDataDetail getMarketDataDetail();

} // -- end of interface MarketDataHistoryDetailEntry
