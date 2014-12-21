//
// -----------------------------------------------------------------------------------
// Source file: CurrentIntermarketBest.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketBestStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Time;

public interface CurrentIntermarketBest
{
    /**
     * Gets the underlying struct
     * @return CurrentIntermarketBestStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public CurrentIntermarketBestStruct getStruct();

    public String getExchange();

    public short getMarketCondition();

    public Price getBidPrice();

    public int getBidVolume();

    public Price getAskPrice();

    public int getAskVolume();

    public Time getSentTime();
}