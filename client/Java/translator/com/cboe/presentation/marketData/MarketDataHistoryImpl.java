// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryImpl
//
// PACKAGE: com.cboe.presentation.marketData
// 
// Created: Jul 9, 2004 2:51:01 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.idl.cmiMarketData.MarketDataHistoryStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.interfaces.presentation.marketData.MarketDataHistory;
import com.cboe.interfaces.presentation.marketData.MarketDataHistoryEntry;
import com.cboe.interfaces.presentation.product.ProductKeys;

import com.cboe.presentation.product.ProductKeysImpl;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

public class MarketDataHistoryImpl extends AbstractBusinessModel implements MarketDataHistory
{
    private MarketDataHistoryStruct struct;
    private MarketDataHistoryEntry[] entries;
    private ProductKeys productKeys;

    private MarketDataHistoryImpl() { super(); }

    public MarketDataHistoryImpl(MarketDataHistoryStruct struct)
    {
        this();
        if (struct == null)
        {
            throw new IllegalArgumentException("MarketDataHistory struct can not be null");
        }

        this.struct = struct;
        initialize();
    }

    private void initialize()
    {
        productKeys = new ProductKeysImpl(struct.productKeys);

        entries = new MarketDataHistoryEntry[struct.entries.length];
        for (int i=0; i<entries.length; i++)
        {
            entries[i] = MarketDataHistoryFactory.createMarketDataHistoryEntry(struct.entries[i]);
        }
    }

    public MarketDataHistoryStruct getStruct()
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

    public MarketDataHistoryEntry[] getEntries()
    {
        return entries;
    }

} // -- end of class MarketDataHistoryImpl
