//
// -----------------------------------------------------------------------------------
// Source file: RecapV4GeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import com.cboe.idl.cmiMarketData.RecapStructV4;

import com.cboe.interfaces.presentation.marketData.express.RecapV4;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.util.ChannelType;

import com.cboe.presentation.marketData.express.RecapV4Impl;

public class RecapV4GeneratorTask extends TestExchangeMarketDataGeneratorTask<RecapV4>
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_RECAP_BY_CLASS_V4;

    public RecapV4GeneratorTask(Product[] products)
    {
        this(products, SUBSCRIPTION_KEY);
    }

    public RecapV4GeneratorTask(Product[] products, int subscriptionChannelType)
    {
        super(products, subscriptionChannelType);
    }

    protected RecapV4 buildTestMarketData(Product product)
    {
        return buildTestMarketData(product, getNextTestExchange());
    }

    protected RecapV4 buildTestMarketData(Product product, String exchange)
    {
        RecapStructV4 struct = new RecapStructV4();
        struct.classKey = product.getProductKeysStruct().classKey;
        struct.productKey = product.getProductKey();
        struct.productType = product.getProductType();
        struct.exchange = exchange;
        struct.sentTime = getTestTime();
        struct.priceScale = 2;
        struct.lowPrice = getPrice();
        struct.highPrice = getPrice();
        struct.openPrice = getPrice();
        struct.previousClosePrice = getPrice();
        struct.statusCodes = "fakeStat";

        return new RecapV4Impl(struct, messageSeqNumber++);
    }
}
