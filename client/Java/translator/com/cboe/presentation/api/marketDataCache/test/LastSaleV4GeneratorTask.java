//
// -----------------------------------------------------------------------------------
// Source file: LastSaleV4GeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import com.cboe.idl.cmiMarketData.LastSaleStructV4;
import com.cboe.idl.cmiConstants.TickDirectionTypes;

import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.util.ChannelType;

import com.cboe.presentation.marketData.express.LastSaleV4Impl;

public class LastSaleV4GeneratorTask extends TestExchangeMarketDataGeneratorTask<LastSaleV4>
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_LAST_SALE_BY_CLASS_V4;

    public LastSaleV4GeneratorTask(Product[] products)
    {
        this(products, SUBSCRIPTION_KEY);
    }

    public LastSaleV4GeneratorTask(Product[] products, int subscriptionChannelType)
    {
        super(products, subscriptionChannelType);
    }

    protected LastSaleV4 buildTestMarketData(Product product)
    {
        return buildTestMarketData(product, getNextTestExchange());
    }

    protected LastSaleV4 buildTestMarketData(Product product, String exchange)
    {
        LastSaleStructV4 struct = new LastSaleStructV4();
        struct.classKey = product.getProductKeysStruct().classKey;
        struct.productKey = product.getProductKey();
        struct.productType = product.getProductType();
        struct.exchange = exchange;
        struct.sentTime = getTestTime();
        struct.priceScale = 2;
        struct.lastSaleTime = getTestTime();
        struct.lastSalePrice = getPrice();
        struct.lastSaleVolume = getQty();
        struct.totalVolume = getQty();
        struct.tickDirection = TickDirectionTypes.PLUS_TICK;
        struct.netPriceChange = getPrice();

        return new LastSaleV4Impl(struct, messageSeqNumber++);
    }
}
