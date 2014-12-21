//
// -----------------------------------------------------------------------------------
// Source file: TickerV4CacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import java.util.*;

import com.cboe.interfaces.presentation.marketData.express.TickerV4;
import com.cboe.interfaces.presentation.api.marketDataCache.TickerV4Cache;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelType;

import com.cboe.presentation.api.marketDataCache.test.TickerV4GeneratorTask;

public class TickerV4CacheImpl extends AbstractExchangeMarketDataCache<TickerV4>
    implements TickerV4Cache
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_TICKER_BY_CLASS_V4;

    /**
     * Returns the String that will be used as a prefix for log messages.
     */
    protected String getLoggingPrefix()
    {
        return "TickerV4CacheImpl";
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by classKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByClassKey()
    {
        return ChannelType.CB_TICKER_BY_CLASS_V4;
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
        TickerV4 ticker = (TickerV4) event.getEventData();
        int classKey = ticker.getProductClassKey();
        int productKey = ticker.getProductKey();
        String exchange = ticker.getExchange();

        Map<Integer, TickerV4> exchangeMap = getExchangeMapForClass(exchange, classKey);
        synchronized(exchangeMap)
        {
            TickerV4 prevTicker = exchangeMap.get(productKey);
            if(prevTicker == null ||
               prevTicker.getMessageSequenceNumber() < ticker.getMessageSequenceNumber() ||
               ticker.getMessageSequenceNumber() == 0)
            {
                exchangeMap.put(productKey, ticker);
            }
        }

        publishMarketDataEvent(ticker, classKey, productKey);
    }

    protected TickerV4GeneratorTask createNewTestTimerTask(Product[] products)
    {
        return new TickerV4GeneratorTask(products);
    }
}
