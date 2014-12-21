// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryDetailEntryImpl
//
// PACKAGE: com.cboe.presentation.marketData
// 
// Created: Jul 12, 2004 10:00:22 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.marketData.MarketDataHistoryDetailEntry;
import com.cboe.interfaces.presentation.marketData.MarketDataDetail;
import com.cboe.interfaces.presentation.marketData.MarketDataHistoryEntry;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailEntryStruct;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

public class MarketDataHistoryDetailEntryImpl extends AbstractBusinessModel implements MarketDataHistoryDetailEntry
{
    private MarketDataHistoryDetailEntryStruct struct;

    private MarketDataHistoryEntry marketDataHistory;
    private MarketDataDetail marketDataDetail;

    public MarketDataHistoryDetailEntryImpl(MarketDataHistoryDetailEntryStruct struct)
    {
        setStruct(struct);
    }

    private void setStruct(MarketDataHistoryDetailEntryStruct struct)
    {
        checkState(struct);

        marketDataDetail = MarketDataHistoryFactory.createMarketDataDetail(struct.detailData);
        marketDataHistory = MarketDataHistoryFactory.createMarketDataHistoryEntry(struct.historyEntry);

        this.struct = struct;
    }

    public MarketDataHistoryDetailEntryStruct getStruct()
    {
        return struct;
    }

    public MarketDataHistoryEntry getMarketDataHistoryEntry()
    {
        return marketDataHistory;
    }

    public MarketDataDetail getMarketDataDetail()
    {
        return marketDataDetail;
    }

} // -- end of class MarketDataHistoryDetailEntryImpl
