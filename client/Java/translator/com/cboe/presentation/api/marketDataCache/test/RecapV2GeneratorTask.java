//
// -----------------------------------------------------------------------------------
// Source file: RecapV2GeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import com.cboe.idl.cmiMarketData.RecapStruct;

import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.util.ChannelType;

import com.cboe.domain.util.DateWrapper;

public class RecapV2GeneratorTask extends TestSessionMarketDataGeneratorTask<RecapStruct>
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_RECAP_BY_CLASS;

    public RecapV2GeneratorTask(SessionProduct[] products)
    {
        this(products, SUBSCRIPTION_KEY);
    }

    public RecapV2GeneratorTask(SessionProduct[] products, int subscriptionChannelType)
    {
        super(products, subscriptionChannelType);
    }

    protected RecapStruct buildTestMarketData(SessionProduct product)
    {
        RecapStruct retVal = new RecapStruct();
        retVal.productKeys = product.getProductKeysStruct();
        retVal.sessionName = product.getTradingSessionName();
        retVal.productInformation = product.getProductNameStruct();
        retVal.lastSalePrice = buildPriceStruct();
        retVal.tradeTime = new DateWrapper().toTimeStruct();
        retVal.lastSaleVolume = getQty();
        retVal.totalVolume = getQty();
        retVal.tickDirection = '+';
        retVal.netChangeDirection = '+';
        retVal.bidDirection = '+';
        retVal.netChange = buildPriceStruct();
        retVal.bidPrice = buildPriceStruct();
        retVal.bidSize = getQty();
        retVal.bidTime = new DateWrapper().toTimeStruct();
        retVal.askPrice = buildPriceStruct();
        retVal.askSize = getQty();
        retVal.askTime = new DateWrapper().toTimeStruct();
        retVal.recapPrefix = "pfx";
        retVal.tick = buildPriceStruct();
        retVal.lowPrice = buildPriceStruct();
        retVal.highPrice = buildPriceStruct();
        retVal.openPrice = buildPriceStruct();
        retVal.closePrice = buildPriceStruct();
        retVal.openInterest = getQty();
        retVal.previousClosePrice = buildPriceStruct();
        retVal.isOTC = false;
        return retVal;
    }
}
