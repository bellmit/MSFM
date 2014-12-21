// -----------------------------------------------------------------------------------
// Source file: MarketDataDetail
//
// PACKAGE: com.cboe.interfaces.presentation.marketData
// 
// Created: Jul 9, 2004 10:40:01 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiMarketData.MarketDataDetailStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.common.properties.KeyValueProperties;

public interface MarketDataDetail extends BusinessModel
{
    // Property Keys for extension field
    public static final String PKEY_MDR_QUOTE_SOURCE = "MDR_QUOTE_SOURCE";

    public MarketDataDetailStruct getStruct();

    public char getOverrideIndicator();

    public Price getNbboAskPrice();
    public ExchangeVolume[] getNbboAskExchanges();

    public Price getNbboBidPrice();
    public ExchangeVolume[] getNbboBidExchanges();

    public boolean getTradeThroughIndicator();

    public ExchangeIndicator[] getExchangeIndicators();

    public Price getBestPublishedBidPrice();
    public int getBestPublishedBidVolume();

    public Price getBestPublishedAskPrice();
    public int getBestPublishedAskVolume();

    public ExchangeAcronym[] getBrokers();
    public ExchangeAcronym[] getContras();

    public KeyValueProperties getExtensions();

} // -- end of interface MarketDataDetail
