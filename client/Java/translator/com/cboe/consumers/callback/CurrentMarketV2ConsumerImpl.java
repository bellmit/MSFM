//
// ------------------------------------------------------------------------
// Source file: CurrentMarketV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

import com.cboe.interfaces.callback.CurrentMarketV2Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;
import com.cboe.interfaces.domain.SessionKeyWrapper;

import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.domain.util.SessionKeyContainer;

public class CurrentMarketV2ConsumerImpl extends AbstractCallbackConsumer
        implements CurrentMarketV2Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public CurrentMarketV2ConsumerImpl(EventChannelAdapter eventChannel)
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

    public void acceptCurrentMarket(CurrentMarketStruct[] currentMarket, int queueDepth, short queueAction)
    {
        for( int i = 0; i < currentMarket.length; i++ )
        {
            dispatchEvent(this, ChannelType.CB_CURRENT_MARKET_BY_PRODUCT,
                          new SessionKeyContainer(currentMarket[i].sessionName, currentMarket[i].productKeys.productKey),
                          currentMarket[i]);

            dispatchEvent(this, ChannelType.CB_CURRENT_MARKET_BY_CLASS,
                          new SessionKeyContainer(currentMarket[i].sessionName, currentMarket[i].productKeys.classKey),
                          currentMarket[i]);

            increaseMethodCallCounter();

            if(isMethodCallLoggingOn())
            {
                logMethodCall(currentMarket[i].sessionName + "." + currentMarket[i].productKeys.productKey,
                              "acceptCurrentMarket(CurrentMarketStruct[], int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptCurrentMarket", currentMarket.length);
        waitDelay();
    }
}
