package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.*;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: kyriakop
 * Date: Mar 24, 2004
 */
public class CMICallbackV3ConsumerCacheFactoryImpl implements CallbackV3ConsumerCacheFactory
{
    EventChannelAdapter eventChannel;
    CMICurrentMarketV3ConsumerCache currentMarketCache;

    public CMICallbackV3ConsumerCacheFactoryImpl(EventChannelAdapter eventChannel)
    {
        if(eventChannel == null)
        {
            throw new IllegalArgumentException("EventChannelAdapter cannot be null.");
        }
        this.eventChannel = eventChannel;
        // creating the caches in the constructor to avoid possible synchronization issues when creating them lazily
        this.currentMarketCache = new CMICurrentMarketV3ConsumerCacheImpl(eventChannel);
    }

    public CMICurrentMarketV3ConsumerCache getCurrentMarketConsumerCache()
    {
        return currentMarketCache;
    }
}
