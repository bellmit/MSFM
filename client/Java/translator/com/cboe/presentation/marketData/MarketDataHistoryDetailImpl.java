// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryDetailImpl
//
// PACKAGE: com.cboe.presentation.marketData
// 
// Created: Jul 12, 2004 10:30:40 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.marketData.MarketDataHistoryDetail;
import com.cboe.interfaces.presentation.marketData.MarketDataHistoryDetailEntry;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailStruct;
import com.cboe.presentation.product.ProductKeysImpl;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

public class MarketDataHistoryDetailImpl extends AbstractBusinessModel implements MarketDataHistoryDetail
{
    private MarketDataHistoryDetailStruct struct;
    private ProductKeys productKeys;
    private MarketDataHistoryDetailEntry[] entries;

    public MarketDataHistoryDetailImpl(MarketDataHistoryDetailStruct struct)
    {
        setStruct(struct);
    }

    private void setStruct(MarketDataHistoryDetailStruct struct)
    {
        checkState(struct);

        productKeys = new ProductKeysImpl(struct.productKeys);

        entries = new MarketDataHistoryDetailEntry[struct.entries.length];
        for (int i=0; i<entries.length; i++)
        {
            entries[i] = MarketDataHistoryFactory.createMarketDataHistoryDetailEntry(struct.entries[i]);
        }

        this.struct = struct;
    }

    public MarketDataHistoryDetailStruct getStruct()
    {
        return struct;
    }

    public ProductKeys getProductKeys()
    {
        return productKeys;
    }

    public String getSessionName()
    {
        return struct.sessionName;
    }

    public DateTimeStruct getStartTime()
    {
        return struct.startTime;
    }

    public DateTimeStruct getEndTime()
    {
        return struct.endTime;
    }

    public boolean getIsOutOfSequence()
    {
        return struct.isOutOfSequence;
    }

    public MarketDataHistoryDetailEntry[] getEntries()
    {
        return entries;
    }

} // -- end of class MarketDataHistoryDetailImpl
