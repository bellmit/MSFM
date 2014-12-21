//
// ------------------------------------------------------------------------
// Source file: CurrentMarketV3ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

import com.cboe.interfaces.callback.CurrentMarketV3Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.domain.CurrentMarketProductContainer;

import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.CurrentMarketProductContainerImpl;
import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.application.util.MarketDataHelper;

public class CurrentMarketV3ConsumerImpl extends AbstractCallbackConsumer
        implements CurrentMarketV3Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public CurrentMarketV3ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    protected String getDelayPropertyName()
    {
        return CURRENT_MARKET_CONSUMER_DELAY_PROPERTY_NAME;
    }

    protected String getLogQueueDepthPropertyName()
    {
        return CURRENT_MARKET_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }

    public void acceptCurrentMarket(CurrentMarketStruct[] bestMarkets, CurrentMarketStruct[] bestPublicMarketsAtTop, int queueDepth, short queueAction)
    {
        for( int i = 0; i < bestMarkets.length; i++ )
        {
            CurrentMarketProductContainer currentMarket = new CurrentMarketProductContainerImpl();
            currentMarket.setBestMarket(bestMarkets[i]);
            int publicIndexAtBest = MarketDataHelper.findPublicIndexAtBest(bestMarkets[i].productKeys.productKey, bestPublicMarketsAtTop);
            if (publicIndexAtBest >= 0)
            {
                currentMarket.setBestPublicMarketAtTop(bestPublicMarketsAtTop[publicIndexAtBest]);
            }
            else
            {
                // We were not given a corresponding best public market at top, so create one so its zero volumes can be propagated to the cache
                currentMarket.setBestPublicMarketAtTop(MarketDataStructBuilder.buildCurrentMarketStruct(bestMarkets[i].sessionName, bestMarkets[i].productKeys));
            }
            dispatchEvent(this, -1 * ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3,
                          new SessionKeyContainer(bestMarkets[i].sessionName, bestMarkets[i].productKeys.productKey),
                          currentMarket);

            dispatchEvent(this, -1 * ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3,
                          new SessionKeyContainer(bestMarkets[i].sessionName, bestMarkets[i].productKeys.classKey),
                          currentMarket);

            increaseMethodCallCounter();

            if(isMethodCallLoggingOn())
            {
                logMethodCall(bestMarkets[i].sessionName + "." + bestMarkets[i].productKeys.productKey,
                              "acceptCurrentMarket(CurrentMarketStruct[], int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptCurrentMarket", bestMarkets.length);
        waitDelay();
    }
}
