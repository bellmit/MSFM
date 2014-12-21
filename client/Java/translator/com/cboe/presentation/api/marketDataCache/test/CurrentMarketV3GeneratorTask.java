//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV3GeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

import com.cboe.interfaces.domain.CurrentMarketProductContainer;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.util.ChannelType;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.CurrentMarketProductContainerImpl;

public class CurrentMarketV3GeneratorTask extends TestSessionMarketDataGeneratorTask<CurrentMarketProductContainer>
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3;

    public CurrentMarketV3GeneratorTask(SessionProduct[] products)
    {
        this(products, SUBSCRIPTION_KEY);
    }

    public CurrentMarketV3GeneratorTask(SessionProduct[] products, int subscriptionChannelType)
    {
        super(products, subscriptionChannelType);
    }

    protected CurrentMarketProductContainer buildTestMarketData(SessionProduct product)
    {
        CurrentMarketStruct bestMarket = new CurrentMarketStruct(product.getProductKeysStruct(),
                                                                 product.getTradingSessionName(), "CBOE",
                                                                 buildPriceStruct(),
                                                                 buildMarketVolumeStructArray(),
                                                                 true, buildPriceStruct(),
                                                                 buildMarketVolumeStructArray(),
                                                                 true,
                                                                 new DateWrapper().toTimeStruct(),
                                                                 true);
        CurrentMarketStruct bestMarketAtTop = new CurrentMarketStruct(
                product.getProductKeysStruct(), product.getTradingSessionName(), "CBOE", buildPriceStruct(),
                buildMarketVolumeStructArray(), true, buildPriceStruct(),
                buildMarketVolumeStructArray(), true, new DateWrapper().toTimeStruct(), true);
        return new CurrentMarketProductContainerImpl(bestMarket, bestMarketAtTop);
    }

}
