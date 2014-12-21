//
// -----------------------------------------------------------------------------------
// Source file: CMICallbackV4ConsumerCacheFactoryImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CallbackV4ConsumerCacheFactory;
import com.cboe.interfaces.consumers.callback.CMICurrentMarketV4ConsumerCache;
import com.cboe.interfaces.consumers.callback.CMITickerV4ConsumerCache;
import com.cboe.interfaces.consumers.callback.CMIRecapV4ConsumerCache;
import com.cboe.interfaces.consumers.callback.CMINBBOV4ConsumerCache;

import com.cboe.util.event.EventChannelAdapter;

/**
 * This returns a cache of V4 market data consumers for each of the V4 market data types: CurrentMarket, Recap/LastSale, and Ticker.
 */
public class CMICallbackV4ConsumerCacheFactoryImpl implements CallbackV4ConsumerCacheFactory
{
    EventChannelAdapter eventChannel;
    CMICurrentMarketV4ConsumerCache currentMarketCache;
    CMINBBOV4ConsumerCache nbboCache;
    CMIRecapV4ConsumerCache recapCache;
    CMITickerV4ConsumerCache tickerCache;

    public CMICallbackV4ConsumerCacheFactoryImpl(EventChannelAdapter eventChannel)
    {
        if(eventChannel == null)
        {
            throw new IllegalArgumentException("EventChannelAdapter cannot be null.");
        }
        this.eventChannel = eventChannel;

        currentMarketCache = new CMICurrentMarketV4ConsumerCacheImpl(eventChannel);
        nbboCache = new CMINBBOV4ConsumerCacheImpl(eventChannel);
        recapCache = new CMIRecapV4ConsumerCacheImpl(eventChannel);
        tickerCache = new CMITickerV4ConsumerCacheImpl(eventChannel);
    }

    public CMICurrentMarketV4ConsumerCache getCurrentMarketConsumerCache()
    {
        return currentMarketCache;
    }

    public CMINBBOV4ConsumerCache getNBBOConsumerCache()
    {
        return nbboCache;
    }

    public CMIRecapV4ConsumerCache getRecapConsumerCache()
    {
        return recapCache;
    }

    public CMITickerV4ConsumerCache getTickerConsumerCache()
    {
        return tickerCache;
    }
}
