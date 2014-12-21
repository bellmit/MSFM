//
// -----------------------------------------------------------------------------------
// Source file: CallbackV5ConsumerCacheFactoryImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CMINBBOV4ConsumerCache;
import com.cboe.interfaces.consumers.callback.CMIRecapV4ConsumerCache;
import com.cboe.interfaces.consumers.callback.CMITickerV4ConsumerCache;
import com.cboe.interfaces.consumers.callback.CallbackV5ConsumerCacheFactory;
import com.cboe.interfaces.consumers.callback.CurrentMarketManualQuoteConsumerCache;
import com.cboe.util.event.EventChannelAdapter;

/**
 * 
 * This returns a cache of V5 market data consumers for each of 
 * the V5 market data types: CurrentMarket, Recap/LastSale, and Ticker.
 * 
 * @author Eric Maheo
 */
public class CallbackV5ConsumerCacheFactoryImpl implements CallbackV5ConsumerCacheFactory
{

    private final EventChannelAdapter eventChannel;
    private final CMINBBOV4ConsumerCache nbboCache;
    private final CMIRecapV4ConsumerCache recapCache;
    private final CMITickerV4ConsumerCache tickerCache;
    private final CurrentMarketManualQuoteConsumerCache currentMarketCache;

    public CallbackV5ConsumerCacheFactoryImpl(EventChannelAdapter eventChannel)
    {
        if(eventChannel == null)
        {
            throw new IllegalArgumentException("EventChannelAdapter cannot be null.");
        }
        this.eventChannel = eventChannel;
        nbboCache = new CMINBBOV5ConsumerCacheImpl(eventChannel);
        recapCache = new CMIRecapV5ConsumerCacheImpl(eventChannel);
        tickerCache = new CMITickerV5ConsumerCacheImpl(eventChannel);
        currentMarketCache = new CurrentMarketManualQuoteConsumerCacheImpl(eventChannel);
    }
    
    
    /* (non-Javadoc)
     * @see com.cboe.interfaces.consumers.callback.CallbackV5ConsumerCacheFactory#getCurrentMarketManualQuoteConsumerCache()
     */
    @Override
    public CurrentMarketManualQuoteConsumerCache getCurrentMarketManualQuoteConsumerCache()
    {
        return currentMarketCache;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.consumers.callback.CallbackV5ConsumerCacheFactory#getNBBOConsumerCache()
     */
    @Override
    public CMINBBOV4ConsumerCache getNBBOConsumerCache()
    {
        return nbboCache;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.consumers.callback.CallbackV5ConsumerCacheFactory#getRecapConsumerCache()
     */
    @Override
    public CMIRecapV4ConsumerCache getRecapConsumerCache()
    {
        return recapCache;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.consumers.callback.CallbackV5ConsumerCacheFactory#getTickerConsumerCache()
     */
    @Override
    public CMITickerV4ConsumerCache getTickerConsumerCache()
    {
        return tickerCache;
    }
}