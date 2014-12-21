//
// -----------------------------------------------------------------------------------
// Source file: LastSaleV4.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData.express;

import java.util.*;

import com.cboe.idl.cmiMarketData.LastSaleStructV4;

import com.cboe.interfaces.domain.Price;

public interface LastSaleV4 extends V4MarketData
{
    public LastSaleStructV4 getLastSaleStructV4();

    public void setLastSaleStructV4(LastSaleStructV4 struct);

    public short getProductType();
    public byte getPriceScale();
    public Date getLastSaleTime();
    public Price getLastSalePrice();
    public int getLastSaleVolume();
    public int getTotalVolume();
    public char getTickDirection();
    public Price getNetPriceChange();
    public Date getSentTime();

    // methods to get the previous values for the prices and quantities
    public Price getLastSalePricePrev();
    public int getLastSaleVolumePrev();
    public Price getNetPriceChangePrev();

    // methods to get the times that the most recent price and quantity updates were received
    public Date getLastSalePriceLastUpdated();
    public Date getLastSaleVolumeLastUpdated();
    public Date getNetPriceChangeLastUpdated();
}
