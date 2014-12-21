//
// -----------------------------------------------------------------------------------
// Source file: TickerV4.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData.express;

import java.util.*;

import com.cboe.idl.cmiMarketData.TickerStructV4;

import com.cboe.interfaces.domain.Price;

public interface TickerV4 extends V4MarketData
{
    public TickerStructV4 getTickerStructV4();

    public void setTickerStructV4(TickerStructV4 struct);

    public short getProductType();
    public byte getPriceScale();
    public Date getTradeTime();
    public Price getTradePrice();
    public int getTradeVolume();
    public String getSalePrefix();
    public String getSalePostfix();
    public Date getSentTime();

    // methods to get the previous values for the prices and quantities
    public Price getTradePricePrev();
    public int getTradeVolumePrev();

    // methods to get the times that the most recent price and quantity updates were received
    public Date getTradePriceLastUpdated();
    public Date getTradeVolumeLastUpdated();
}
