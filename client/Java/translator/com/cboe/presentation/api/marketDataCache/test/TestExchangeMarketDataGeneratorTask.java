//
// -----------------------------------------------------------------------------------
// Source file: TestExchangeMarketDataGeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import java.util.*;

import com.cboe.idl.cmiConstants.ExchangeStrings;

import com.cboe.interfaces.presentation.product.Product;

public abstract class TestExchangeMarketDataGeneratorTask<T> extends TestMarketDataGeneratorTask<T>
{
    private Product[] products;
    protected int messageSeqNumber = 0;

    protected TestExchangeMarketDataGeneratorTask(Product[] products, int subscriptionChannelType)
    {
        super(subscriptionChannelType);
        this.products = products;
    }

    protected abstract T buildTestMarketData(Product product);

    protected abstract T buildTestMarketData(Product product, String exchange);

    /**
     * The action to be performed by this timer task.
     */
    public void run()
    {
        for(Product product : products)
        {
            for(String xchg : testExchanges)
            {
                dispatchEvent(channelType, product.getProductKeysStruct().classKey, buildTestMarketData(product, xchg));
            }
        }
    }

    protected int getPrice()
    {
        return (int) (Math.random() * 10000);
    }

    protected int getTestTime()
    {
        Date currentDate = new Date();
        return (int) (currentDate.getTime() -
               new Date(currentDate.getYear(), currentDate.getMonth(), currentDate.getDate())
                       .getTime());
    }

    private String[] testExchanges = new String[]{ExchangeStrings.AMEX, ExchangeStrings.BSE,
                                                  ExchangeStrings.CBOE, ExchangeStrings.CBOT,
                                                  ExchangeStrings.CME, ExchangeStrings.ISE,
                                                  ExchangeStrings.LIFFE, ExchangeStrings.NASD,
                                                  ExchangeStrings.NYME, ExchangeStrings.NYSE,
                                                  ExchangeStrings.ONE, ExchangeStrings.PSE,
                                                  ExchangeStrings.CBOE2};
    private int testExchangeIndex = 0;

    protected String getNextTestExchange()
    {
        if(testExchangeIndex == testExchanges.length)
        {
            testExchangeIndex = 0;
        }
        return testExchanges[testExchangeIndex++];
    }
}
