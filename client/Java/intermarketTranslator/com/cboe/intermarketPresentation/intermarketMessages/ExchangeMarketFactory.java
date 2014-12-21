//
// -----------------------------------------------------------------------------------
// Source file: ExchangeMarketFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;

public class ExchangeMarketFactory
{
    public static ExchangeMarket createExchangeMarket(ExchangeMarketStruct exchangeMarketStruct)
    {
        return new ExchangeMarketImpl(exchangeMarketStruct);
    }
}
