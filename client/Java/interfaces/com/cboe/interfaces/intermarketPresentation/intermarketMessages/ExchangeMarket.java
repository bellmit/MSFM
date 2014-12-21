//
// -----------------------------------------------------------------------------------
// Source file: ExchangeMarket.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.marketData.ExchangeVolume;

public interface ExchangeMarket
{
    /**
     * Gets the underlying struct
     * @return ExchangeMarketStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ExchangeMarketStruct getStruct();

    public short getMarketInfoType();

    public Price getBestBidPrice();

    public ExchangeVolume[] getBidExchangeVolumes();

    public Price getBestAskPrice();

    public ExchangeVolume[] getAskExchangeVolumes();
}