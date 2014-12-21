package com.cboe.consumers.internalPresentation;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.consumers.callback.*;

/**
 * Caches the CallbackConsumerCache instances for each interface (e.g., CMICurrentMarketConsumerCache, etc)
 *
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Feb 26, 2003
 * Time: 3:11:09 PM
 * To change this template use Options | File Templates.
 */
public class AdminCMICallbackConsumerCacheFactoryImpl implements CallbackConsumerCacheFactory
{
    EventChannelAdapter eventChannel;
    CMICurrentMarketConsumerCache currentMarketCache;
    CMIRecapConsumerCache recapCache;
    CMINBBOConsumerCache nbboCache;
    CMIOrderBookConsumerCache bookDepthCache;

    public AdminCMICallbackConsumerCacheFactoryImpl(EventChannelAdapter eventChannel)
    {
        if(eventChannel == null)
        {
            throw new IllegalArgumentException("EventChannelAdapter cannot be null.");
        }
        this.eventChannel = eventChannel;
        // creating the caches in the constructor to avoid possible synch issues when creating them lazily
        this.currentMarketCache = new AdminCMICurrentMarketConsumerCacheImpl(eventChannel);
        this.recapCache = new AdminCMIRecapConsumerCacheImpl(eventChannel);
        this.nbboCache = new AdminCMINBBOConsumerCacheImpl(eventChannel);
        this.bookDepthCache = new AdminCMIOrderBookConsumerCacheImpl(eventChannel);
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
