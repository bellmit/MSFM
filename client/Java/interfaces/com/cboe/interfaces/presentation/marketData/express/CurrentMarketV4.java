//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV4.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData.express;

import java.util.*;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;

import com.cboe.interfaces.domain.Price;

/**
 * This interface provides an immutable wrapper for the CurrentMarketStructV4.
 */
public interface CurrentMarketV4
{
    public void setCurrentMarketStructV4(CurrentMarketStructV4 struct);
    public CurrentMarketStructV4 getCurrentMarketStructV4();

    public int getClassKey();
    public int getProductKey();
    public short getProductType();
    public String getExchange();
    public byte getCurrentMarketType();
    public Price getBidPrice();
    public MarketVolumeStructV4[] getBidSizeSequence();
    public Price getAskPrice();
    public MarketVolumeStructV4[] getAskSizeSequence();
    public Date getSentTime();
    public byte getMarketIndicator();
    public short getProductState();
    public byte getPriceScale();
    public char getBidTickDirection();

    // methods to get the previous values for the prices and quantities
    public Price getBidPricePrev();
    public MarketVolumeStructV4[] getBidSizeSequencePrev();
    public Price getAskPricePrev();
    public MarketVolumeStructV4[] getAskSizeSequencePrev();

    // methods to get the times that the most recent price and quantity updates were received
    public Date getBidPriceLastUpdated();
    public Date getBidSizeSequenceLastUpdated();
    public Date getAskPriceLastUpdated();
    public Date getAskSizeSequenceLastUpdated();
}
