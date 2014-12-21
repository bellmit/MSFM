//
// -----------------------------------------------------------------------------------
// Source file: CurrentIntermarketBestImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketBestStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.CurrentIntermarketBest;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.TimeImpl;

class CurrentIntermarketBestImpl implements CurrentIntermarketBest
{
    private CurrentIntermarketBestStruct    currentIntermarketBestStruct;
    private String                          exchange;
    private short                           marketCondition;
    private Price                           bidPrice;
    private int                             bidVolume;
    private Price                           askPrice;
    private int                             askVolume;
    private Time                            sentTime;

    public CurrentIntermarketBestImpl(CurrentIntermarketBestStruct currentIntermarketBestStruct)
    {
        this.currentIntermarketBestStruct = currentIntermarketBestStruct;
        initialize();
    }

    private void initialize()
    {
        exchange        = new String(currentIntermarketBestStruct.exchange);
        marketCondition = currentIntermarketBestStruct.marketCondition;
        bidPrice        = DisplayPriceFactory.create(currentIntermarketBestStruct.bidPrice);
        bidVolume       = currentIntermarketBestStruct.bidVolume;
        askPrice        = DisplayPriceFactory.create(currentIntermarketBestStruct.askPrice);
        askVolume       = currentIntermarketBestStruct.askVolume;
        sentTime        = new TimeImpl(currentIntermarketBestStruct.sentTime);
    }

    public String getExchange()
    {
        return exchange;
    }

    public short getMarketCondition()
    {
        return marketCondition;
    }

    public Price getBidPrice()
    {
        return bidPrice;
    }

    public int getBidVolume()
    {
        return bidVolume;
    }

    public Price getAskPrice()
    {
        return askPrice;
    }

    public int getAskVolume()
    {
        return askVolume;
    }

    public Time getSentTime()
    {
        return sentTime;
    }

    /**
     * Gets the underlying struct
     * @return CurrentIntermarketBestStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public CurrentIntermarketBestStruct getStruct()
    {
        return currentIntermarketBestStruct;
    }
}
