//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV4CacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import java.util.*;

import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;
import com.cboe.interfaces.presentation.api.marketDataCache.CurrentMarketV4Cache;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelType;

import com.cboe.presentation.api.marketDataCache.test.CurrentMarketV4GeneratorTask;

public class CurrentMarketV4CacheImpl extends AbstractExchangeMarketDataCache<CurrentMarketV4ProductContainer>
    implements CurrentMarketV4Cache
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4;

    protected CurrentMarketV4CacheImpl()
    {
    }

    protected String getLoggingPrefix()
    {
        return "CurrentMarketV4CacheImpl";
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by classKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByClassKey()
    {
        return ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4;
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by productKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByProductKey()
    {
        return -1;
    }

    /**
     * Returns the ChannelType constant that this cache will use to subscribe for events on the IEC
     * Subscriptions are class-based, but events may be re-published by both product and class.
     * @return ChannelType constant
     */
    protected int getChannelTypeForSubscribeByClassKey()
    {
        return SUBSCRIPTION_KEY;
    }

    /**
     * channelUpdate is called by the event channel adapter when it dispatches an event to the
     * registered listeners.
     */
    public void channelUpdate(ChannelEvent event)
    {
        CurrentMarketV4ProductContainer currentMarket = (CurrentMarketV4ProductContainer)event.getEventData();
        int classKey = currentMarket.getProductClassKey();
        int productKey = currentMarket.getProductKey();
        String exchange = currentMarket.getExchange();

        Map<Integer, CurrentMarketV4ProductContainer> exchangeMap = getExchangeMapForClass(exchange, classKey);
        synchronized(exchangeMap)
        {
            CurrentMarketV4ProductContainer prevCurrentMarket = exchangeMap.get(productKey);
            if(prevCurrentMarket == null ||
               prevCurrentMarket.getMessageSequenceNumber() < currentMarket.getMessageSequenceNumber() ||
               currentMarket.getMessageSequenceNumber() == 0)
            {
                exchangeMap.put(productKey, currentMarket);
            }
        }

        publishMarketDataEvent(currentMarket, classKey, productKey);
    }

    protected CurrentMarketV4GeneratorTask createNewTestTimerTask(Product[] products)
    {
        return new CurrentMarketV4GeneratorTask(products);
    }
}
