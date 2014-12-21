//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV4GeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.idl.cmiConstants.ExchangeStrings;
import com.cboe.idl.cmiConstants.ProductStates;
import com.cboe.idl.cmiConstants.CurrentMarketTypes;
import com.cboe.idl.cmiConstants.TickDirectionTypes;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;

import com.cboe.util.ChannelType;

import com.cboe.presentation.marketData.express.CurrentMarketV4ProductContainerImpl;

public class CurrentMarketV4GeneratorTask extends TestExchangeMarketDataGeneratorTask<CurrentMarketV4ProductContainer>
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4;

    public CurrentMarketV4GeneratorTask(Product[] products)
    {
        this(products, SUBSCRIPTION_KEY);
    }

    public CurrentMarketV4GeneratorTask(Product[] products, int subscriptionChannelType)
    {
        super(products, subscriptionChannelType);
    }

    protected CurrentMarketV4ProductContainer buildTestMarketData(Product product)
    {
        return buildTestMarketData(product, getNextTestExchange());
    }

    protected CurrentMarketV4ProductContainer buildTestMarketData(Product product, String exchange)
    {
        CurrentMarketV4ProductContainer retVal = new CurrentMarketV4ProductContainerImpl();

        CurrentMarketStructV4 bestMarket = buildCMV4Struct(product, CurrentMarketTypes.BEST_MARKETS, exchange);
        CurrentMarketStructV4 bestPublicMarket = buildCMV4Struct(product, CurrentMarketTypes.BEST_PUBLIC_MARKETS, exchange);
        retVal.setBestMarket(bestMarket);
        retVal.setBestPublicMarketAtTop(bestPublicMarket);
        return retVal;
    }

    private CurrentMarketStructV4 buildCMV4Struct(Product product, byte marketType, String exchange)
    {
        CurrentMarketStructV4 struct = new CurrentMarketStructV4();
        struct.classKey = product.getProductKeysStruct().classKey;
        struct.productKey = product.getProductKey();
        struct.productType = product.getProductType();
        struct.exchange = exchange;
        struct.sentTime = getTestTime();
        struct.currentMarketType = marketType;
        struct.bidPrice = getPrice();
        struct.bidTickDirection = TickDirectionTypes.PLUS_TICK;
        struct.bidSizeSequence = buildMarketVolumeStructV4Array();
        struct.askPrice = getPrice();
        struct.askSizeSequence = buildMarketVolumeStructV4Array();
        struct.marketIndicator = ' ';
        if(struct.exchange.equals(ExchangeStrings.CBOE))
        {
            struct.productState = ProductStates.OPEN;
        }
        else
        {
            struct.productState = ProductStates.UNKNOWN;
        }
        struct.priceScale = 2;
        return struct;
    }
}
