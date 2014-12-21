// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryEntry
//
// PACKAGE: com.cboe.interfaces.presentation.marketData
// 
// Created: Jul 9, 2004 10:39:18 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryEntryStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface MarketDataHistoryEntry extends BusinessModel
{
    public MarketDataHistoryEntryStruct getStruct();

    public short getEntryType();
    public char getSource();

    public DateTimeStruct getReportTime();
    public Price getPrice();
    public int getQuantity();

    public String getSellerAcronym();
    public String getBuyerAcronym();

    public int getBidSize();
    public Price getBidPrice();

    public int getAskSize();
    public Price getAskPrice();

    public Price getUnderlyingLastSalePrice();

    public short getEopType();
    public short getMarketCondition();

    public String getOptionalData();
    public String getExceptionCode();
    public String getPhysLocation();
    public String getPrefix();

} // -- end of interface MarketDataHistoryEntry
