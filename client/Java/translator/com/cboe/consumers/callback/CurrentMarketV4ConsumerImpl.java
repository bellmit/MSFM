//
// ------------------------------------------------------------------------
// Source file: CurrentMarketV4ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;

import com.cboe.interfaces.callback.CurrentMarketV4Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;
import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.ChannelType;

import com.cboe.presentation.marketData.express.CurrentMarketV4ProductContainerImpl;

import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.application.util.MarketDataHelper;

public class CurrentMarketV4ConsumerImpl extends AbstractV4CallbackConsumer
        implements CurrentMarketV4Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public CurrentMarketV4ConsumerImpl(EventChannelAdapter eventChannel)
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

    public void acceptCurrentMarket(CurrentMarketStructV4[] bestMarkets,
                                    CurrentMarketStructV4[] bestPublicMarkets,
                                    int messageSequence, int queueDepth, short queueAction)
    {
        for(int i = 0; i < bestMarkets.length; i++)
        {
            CurrentMarketV4ProductContainer currentMarket = new CurrentMarketV4ProductContainerImpl(messageSequence);
            currentMarket.setBestMarket(bestMarkets[i]);
            int publicIndexAtBest = MarketDataHelper.findPublicIndexAtBest(bestMarkets[i].exchange, bestMarkets[i].productKey, bestPublicMarkets);
            if(publicIndexAtBest >= 0)
            {
                currentMarket.setBestPublicMarketAtTop(bestPublicMarkets[publicIndexAtBest]);
            }
            else
            {
                // We were not given a corresponding best public market at top, so create one so its zero volumes can be propagated to the cache
                currentMarket.setBestPublicMarketAtTop(MarketDataStructBuilder.buildCurrentMarketStructV4(bestMarkets[i].classKey,
                                                                                                          bestMarkets[i].productType,
                                                                                                          bestMarkets[i].exchange,
                                                                                                          bestMarkets[i].productKey));
            }
            dispatchEvent(this, -1 * ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4, bestMarkets[i].classKey, currentMarket);
            
            increaseMethodCallCounter();

            if(isMethodCallLoggingOn())
            {
                logMethodCall(bestMarkets[i].exchange + "." + bestMarkets[i].productKey,
                              "acceptCurrentMarket(CurrentMarketStructV4[], CurrentMarketStructV4[], int, int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptCurrentMarket", bestMarkets.length);
        waitDelay();
    }
}
