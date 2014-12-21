// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryDetail
//
// PACKAGE: com.cboe.interfaces.presentation.marketData
// 
// Created: Jul 9, 2004 10:40:20 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailStruct;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface MarketDataHistoryDetail extends BusinessModel
{
    public MarketDataHistoryDetailStruct getStruct();

    public ProductKeys  getProductKeys();
    public String getSessionName();

    public DateTimeStruct getStartTime();
    public DateTimeStruct getEndTime();

    public boolean getIsOutOfSequence();
    public MarketDataHistoryDetailEntry[] getEntries();

} // -- end of interface MarketDataHistoryDetail
