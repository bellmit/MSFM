//
// -----------------------------------------------------------------------------------
// Source file: TickerV4GeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import com.cboe.idl.cmiMarketData.TickerStructV4;

import com.cboe.interfaces.presentation.marketData.express.TickerV4;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.util.ChannelType;

import com.cboe.presentation.marketData.express.TickerV4Impl;

public class TickerV4GeneratorTask extends TestExchangeMarketDataGeneratorTask<TickerV4>
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_TICKER_BY_CLASS_V4;

    public TickerV4GeneratorTask(Product[] products)
    {
        this(products, SUBSCRIPTION_KEY);
    }

    public TickerV4GeneratorTask(Product[] products, int subscriptionChannelType)
    {
        super(products, subscriptionChannelType);
    }

    protected TickerV4 buildTestMarketData(Product product)
    {
        return buildTestMarketData(product, getNextTestExchange());
    }

    protected TickerV4 buildTestMarketData(Product product, String exchange)
    {
        TickerStructV4 struct = new TickerStructV4();
        struct.classKey = product.getProductKeysStruct().classKey;
        struct.productKey = product.getProductKey();
        struct.productType = product.getProductType();
        struct.exchange = exchange;
        struct.sentTime = getTestTime();
        struct.priceScale = 2;
        struct.tradeTime = getTestTime();
        struct.tradePrice = getPrice();
        struct.tradeVolume = getQty();
        struct.salePrefix = "fakePrefix";
        struct.salePostfix = "fakePostfix";

        return new TickerV4Impl(struct, messageSeqNumber++);
    }
}
