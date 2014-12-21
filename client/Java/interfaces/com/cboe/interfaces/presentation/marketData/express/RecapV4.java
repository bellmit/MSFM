//
// -----------------------------------------------------------------------------------
// Source file: RecapV4.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData.express;

import java.util.*;

import com.cboe.idl.cmiMarketData.RecapStructV4;

import com.cboe.interfaces.domain.Price;

public interface RecapV4 extends V4MarketData
{
    public RecapStructV4 getRecapStructV4();

    public void setRecapStructV4(RecapStructV4 struct);

    public short getProductType();
    public byte getPriceScale();
    public Price getLowPrice();
    public Price getHighPrice();
    public Price getOpenPrice();
    public Price getPreviousClosePrice();
    public Date getSentTime();
    public String getStatusCodes();

    // methods to get the previous values for the prices
    public Price getLowPricePrev();
    public Price getHighPricePrev();

    // methods to get the times that the most recent price updates were received
    public Date getLowPriceLastUpdated();
    public Date getHighPriceLastUpdated();
}
