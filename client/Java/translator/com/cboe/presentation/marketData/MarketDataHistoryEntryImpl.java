// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryEntryImpl
//
// PACKAGE: com.cboe.presentation.marketData
// 
// Created: Jul 9, 2004 2:08:54 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.idl.cmiMarketData.MarketDataHistoryEntryStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.presentation.marketData.MarketDataHistoryEntry;
import com.cboe.interfaces.domain.Price;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

public class MarketDataHistoryEntryImpl extends AbstractBusinessModel implements MarketDataHistoryEntry
{
    private MarketDataHistoryEntryStruct struct;

    private Price price;
    private Price bidPrice;
    private Price askPrice;
    private Price lastSalePrice;

    public MarketDataHistoryEntryImpl(MarketDataHistoryEntryStruct struct)
    {
        setStruct(struct);
    }

    private void setStruct(MarketDataHistoryEntryStruct struct)
    {
        checkState(struct);

        price         = DisplayPriceFactory.create(struct.price);
        askPrice      = DisplayPriceFactory.create(struct.askPrice);
        bidPrice      = DisplayPriceFactory.create(struct.bidPrice);
        lastSalePrice = DisplayPriceFactory.create(struct.underlyingLastSalePrice);

        this.struct = struct;
    }

    public MarketDataHistoryEntryStruct getStruct()
    {
        return struct;
    }

    public short getEntryType()
    {
        return struct.entryType;
    }

    public char getSource()
    {
        return struct.source;
    }

    public DateTimeStruct getReportTime()
    {
        return struct.reportTime;
    }

    public Price getPrice()
    {
        return price;
    }

    public int getQuantity()
    {
        return struct.quantity;
    }

    public String getSellerAcronym()
    {
        return struct.sellerAcronym;
    }

    public String getBuyerAcronym()
    {
        return struct.buyerAcronym;
    }

    public int getBidSize()
    {
        return struct.bidSize;
    }

    public Price getBidPrice()
    {
        return bidPrice;
    }

    public int getAskSize()
    {
        return struct.askSize;
    }

    public Price getAskPrice()
    {
        return askPrice;
    }

    public Price getUnderlyingLastSalePrice()
    {
        return lastSalePrice;
    }

    public short getEopType()
    {
        return struct.eopType;
    }

    public short getMarketCondition()
    {
        return struct.marketCondition;
    }

    public String getOptionalData()
    {
        return struct.optionalData;
    }

    public String getExceptionCode()
    {
        return struct.exceptionCode;
    }

    public String getPhysLocation()
    {
        return struct.physLocation;
    }

    public String getPrefix()
    {
        return struct.prefix;
    }

} // -- end of class MarketDataHistoryEntryImpl
