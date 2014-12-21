//
// -----------------------------------------------------------------------------------
// Source file: TestMarketDataGeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import java.util.*;

import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.VolumeTypes;
import com.cboe.idl.cmiConstants.MultiplePartiesIndicators;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;

import com.cboe.util.ChannelKey;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * This publishes market data events on the IEC, as if the events were coming
 * from the callback consumers.  It can be used to test the MD caches.
 */
public abstract class TestMarketDataGeneratorTask<T> extends TimerTask
{
    private static final String TEST_MODE_EVENT_DELAY_PROP_NAME = "MARKET_DATA_TEST_EVENT_DELAY";
    protected static long SLEEP_TIME;
    protected int channelType;
    private final EventChannelAdapter eventChannel;

    static
    {
        SLEEP_TIME = 0;
        String testProperty = System.getProperty(TEST_MODE_EVENT_DELAY_PROP_NAME);
        if (testProperty != null && testProperty.length() > 0)
        {
            SLEEP_TIME = Long.parseLong(testProperty);
        }
    }

    protected TestMarketDataGeneratorTask(int subscriptionChannelType)
    {
        channelType = subscriptionChannelType;
        eventChannel = EventChannelAdapterFactory.find();
    }

    /**
     * The action to be performed by this timer task.
     */
    public abstract void run();

    /**
     * Returns the number of milliseconds to sleep between publishing test events.
     */
    protected static long getTestThreadSleepMillis()
    {
        return SLEEP_TIME;
    }

    /**
     * If the system property TEST_MODE_EVENT_DELAY_PROP_NAME is set to a value
     * greater than zero, the current thread will sleep the specified number of
     * milliseconds.
     */
    protected void threadSleep()
    {
        if(getTestThreadSleepMillis() > 0)
        {
            try
            {
                Thread.sleep(getTestThreadSleepMillis());
            }
            catch (Throwable t)
            {
                GUILoggerHome.find().exception(t);
            }
        }
    }

    protected PriceStruct buildPriceStruct()
    {
        return new PriceStruct(PriceTypes.VALUED, (int)(Math.random() * 1000), (int)(Math.random() * 100));
    }

    protected MarketVolumeStruct[] buildMarketVolumeStructArray()
    {
        MarketVolumeStruct[] retVal = new MarketVolumeStruct[1];
        retVal[0] = new MarketVolumeStruct(VolumeTypes.LIMIT, getQty(), false);
        return retVal;
    }

    protected ExchangeVolumeStruct[] buildExchangeVolumeStructArray()
    {
        ExchangeVolumeStruct[] retVal = new ExchangeVolumeStruct[1];
        retVal[0] = new ExchangeVolumeStruct("CBOE", getQty());
        return retVal;
    }

    protected MarketVolumeStructV4[] buildMarketVolumeStructV4Array()
    {
        MarketVolumeStructV4[] retVal = new MarketVolumeStructV4[1];
        retVal[0] = new MarketVolumeStructV4(VolumeTypes.LIMIT, getQty(), MultiplePartiesIndicators.NO);
        return retVal;
    }

    protected void dispatchEvent(int channelType, Object key, T data)
    {
        threadSleep();
        ChannelKey channelKey = new ChannelKey(channelType, key);
        ChannelEvent event = eventChannel.getChannelEvent(this, channelKey, data);
        eventChannel.dispatch(event);
    }

    protected int getQty()
    {
        return (int) (Math.random() * 1000);
    }
}
