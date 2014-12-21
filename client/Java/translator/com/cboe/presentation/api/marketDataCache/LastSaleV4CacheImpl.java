//
// -----------------------------------------------------------------------------------
// Source file: LastSaleV4CacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import java.util.*;

import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;
import com.cboe.interfaces.presentation.api.marketDataCache.LastSaleV4Cache;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelType;

import com.cboe.presentation.api.marketDataCache.test.LastSaleV4GeneratorTask;

public class LastSaleV4CacheImpl extends AbstractExchangeMarketDataCache<LastSaleV4>
    implements LastSaleV4Cache
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_LAST_SALE_BY_CLASS_V4;

    /**
     * Returns the String that will be used as a prefix for log messages.
     */
    protected String getLoggingPrefix()
    {
        return "LastSaleV4CacheImpl";
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by classKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByClassKey()
    {
        return ChannelType.CB_LAST_SALE_BY_CLASS_V4;
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
        LastSaleV4 lastSale = (LastSaleV4) event.getEventData();
        int classKey = lastSale.getProductClassKey();
        int productKey = lastSale.getProductKey();
        String exchange = lastSale.getExchange();

        Map<Integer, LastSaleV4> exchangeMap = getExchangeMapForClass(exchange, classKey);
        synchronized(exchangeMap)
        {
            LastSaleV4 prevLastSale = exchangeMap.get(productKey);
            if(prevLastSale == null ||
               prevLastSale.getMessageSequenceNumber() < lastSale.getMessageSequenceNumber() ||
               lastSale.getMessageSequenceNumber() == 0)
            {
                exchangeMap.put(productKey, lastSale);
            }
        }

        publishMarketDataEvent(lastSale, classKey, productKey);
    }

    protected LastSaleV4GeneratorTask createNewTestTimerTask(Product[] products)
    {
        return new LastSaleV4GeneratorTask(products);
    }
}
