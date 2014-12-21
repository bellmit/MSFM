// -----------------------------------------------------------------------------------
// Source file: MarketDataHistory
//
// PACKAGE: com.cboe.interfaces.presentation.marketData
// 
// Created: Jul 9, 2004 10:38:39 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiMarketData.MarketDataHistoryStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface MarketDataHistory extends BusinessModel
{
    MarketDataHistoryStruct getStruct();

    public ProductKeys getProductKeys();
    public String getSessionName();

    public DateTimeStruct getStartTime();
    public DateTimeStruct getEndTime();

    public MarketDataHistoryEntry[] getEntries();

} // -- end of interface MarketDataHistory
