package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.*;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Caches the CallbackConsumerCaches for each interface (e.g., CMICurrentMarketConsumerCache, etc)
 *
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Feb 25, 2003
 * Time: 11:51:09 AM
 * To change this template use Options | File Templates.
 */
public class CMICallbackConsumerCacheFactoryImpl implements CallbackConsumerCacheFactory
{
    EventChannelAdapter eventChannel;
    CMICurrentMarketConsumerCache currentMarketCache;
    CMIRecapConsumerCache recapCache;
    CMINBBOConsumerCache nbboCache;
    CMIOrderBookConsumerCache bookDepthCache;

    public CMICallbackConsumerCacheFactoryImpl(EventChannelAdapter eventChannel)
    {
        if(eventChannel == null)
        {
            throw new IllegalArgumentException("EventChannelAdapter cannot be null.");
        }
        this.eventChannel = eventChannel;
        // creating the caches in the constructor to avoid possible synchronization issues when creating them lazily
        this.currentMarketCache = new CMICurrentMarketConsumerCacheImpl(eventChannel);
        this.recapCache = new CMIRecapConsumerCacheImpl(eventChannel);
        this.nbboCache = new CMINBBOConsumerCacheImpl(eventChannel);
        this.bookDepthCache = new CMIOrderBookConsumerCacheImpl(eventChannel);
    }

    public CMICurrentMarketConsumerCache getCurrentMarketConsumerCache()
    {
        return currentMarketCache;
    }

    public CMIRecapConsumerCache getRecapConsumerCache()
    {
        return recapCache;
    }

    public CMINBBOConsumerCache getNBBOConsumerCache()
    {
        return nbboCache;
    }

    public CMIOrderBookConsumerCache getBookDepthConsumerCache()
    {
        return bookDepthCache;
    }
}
