//
// -----------------------------------------------------------------------------------
// Source file: TestSessionMarketDataGeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.domain.util.SessionKeyContainer;

public abstract class TestSessionMarketDataGeneratorTask<T> extends TestMarketDataGeneratorTask<T>
{
    protected SessionProduct[] products;
    protected TestSessionMarketDataGeneratorTask(SessionProduct[] products, int subscriptionChannelType)
    {
        super(subscriptionChannelType);
        this.products = products;
    }

    protected abstract T buildTestMarketData(SessionProduct product);

    /**
     * The action to be performed by this timer task.
     */
    public void run()
    {
        for(SessionProduct product : products)
        {
            dispatchEvent(channelType, new SessionKeyContainer(product.getTradingSessionName(),
                                                               product.getProductKeysStruct().classKey),
                                       buildTestMarketData(product));
        }
    }
}
