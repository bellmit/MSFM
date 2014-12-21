//
// -----------------------------------------------------------------------------------
// Source file: AbstractMarketDataCache.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import java.util.*;

import com.cboe.interfaces.presentation.api.marketDataCache.MarketDataCache;
import com.cboe.interfaces.presentation.api.ProductQueryAPI;
import com.cboe.interfaces.presentation.api.MarketQueryV3API;

import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

public abstract class AbstractMarketDataCache<T> implements MarketDataCache, EventChannelListener
{
    protected final Object subscriptionLock = new Object();

    protected ProductQueryAPI productQueryDelegate;
    protected MarketQueryV3API marketQueryDelegate;

    protected final Set<Integer> subscribedClasses;

    protected final EventChannelAdapter eventChannel;

    protected AbstractMarketDataCache()
    {
        subscribedClasses = Collections.synchronizedSet(new HashSet<Integer>(50));
        marketQueryDelegate = APIHome.findMarketQueryAPI();
        productQueryDelegate = APIHome.findProductQueryAPI();
        eventChannel = EventChannelAdapterFactory.find();
    }

    /**
     * Returns the String that will be used as a prefix for log messages.
     * @return
     */
    protected abstract String getLoggingPrefix();

    /**
     * Removes the market data from the cache for the specified classKey.
     * @param classKey
     */
    protected abstract void removeMarketDataByClass(int classKey);
    /**
     * Adds the cache to the IEC as an EventChannelListener for the correct ChannelType.
     */
    protected abstract void internalSubscribeIEC(int classKey);

    /**
     * Removes the cache from the IEC as an EventChannelListener.
     * @param classKey
     */
    protected abstract void internalUnsubscribeIEC(int classKey);

    /**
     * Publishes a market data event on the IEC.
     * @param eventData
     */
    protected abstract void publishMarketDataEvent(T eventData, int classKey, int productKey);

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by classKey, else returns a ChannelType constant
     */
    protected abstract int getChannelTypeForPublishByClassKey();

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by productKey, else returns a ChannelType constant
     */
    protected abstract int getChannelTypeForPublishByProductKey();

    /**
     * Returns the ChannelType constant that this cache will use to subscribe for events on the IEC.
     * Subscriptions are class-based, but events may be re-published by both product and class.
     * @return ChannelType constant
     */
    protected abstract int getChannelTypeForSubscribeByClassKey();

    protected abstract String getTestModePropName();

    protected abstract String getTestModeIntervalPropName();

    /**
     * Returns true if the getTestModePropName() system property is set to to true.
     */
    protected boolean isTestMode()
    {
        boolean retVal = false;
        String testProperty = System.getProperty(getTestModePropName());
        if (testProperty != null &&
                (testProperty.equalsIgnoreCase("enabled") || testProperty.equalsIgnoreCase("true")))
        {
            retVal = true;
        }
        return retVal;
    }

    protected long getTestModeDataPublishInterval()
    {
        long retVal = 1000;
        String testProperty = System.getProperty(getTestModeIntervalPropName());
        if (testProperty != null && testProperty.length() > 0)
        {
            retVal = Long.parseLong(testProperty);
        }
        return retVal;
    }

    /**
     * @param classKey
     * @return true if this cache is subscribed for the classKey
     */
    public boolean isSubscribedForClass(int classKey)
    {
        return subscribedClasses.contains(classKey);
    }

    public void subscribeMarketData(int classKey)
    {
        synchronized(subscriptionLock)
        {
            if(!isSubscribedForClass(classKey))
            {
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
                {
                    GUILoggerHome.find().debug(getLoggingPrefix()+".subscribeMarketData()",
                                               GUILoggerBusinessProperty.MARKET_QUERY,
                                               "subscribing cache to the IEC for classKey="+classKey);
                }
                internalSubscribeIEC(classKey);
                subscribedClasses.add(classKey);
            }
        }
    }

    public void unsubscribeMarketData(int classKey)
    {
        synchronized(subscriptionLock)
        {
            if(isSubscribedForClass(classKey))
            {
                // unsubscribe from the IEC for MD for the class
                internalUnsubscribeIEC(classKey);
                subscribedClasses.remove(classKey);

                removeMarketDataByClass(classKey);
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
                {
                    GUILoggerHome.find().debug(getLoggingPrefix() + ".unsubscribeMarketData()",
                                               GUILoggerBusinessProperty.MARKET_QUERY,
                                               "unsubscribing cache to the IEC for classKey=" + classKey);
                }
            }
        }
    }

    /**
     * Convenience method to publish an event on the IEC.
     * @param source
     * @param channelType
     * @param key
     * @param data
     */
    protected void dispatchEvent(int channelType, Object key, T data)
    {
        ChannelKey channelKey = new ChannelKey(channelType, key);
        ChannelEvent event = eventChannel.getChannelEvent(this, channelKey, data);
        eventChannel.dispatch(event);
    }
}
