//
// ------------------------------------------------------------------------
// Source file: TickerV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.TickerStruct;

import com.cboe.interfaces.callback.TickerV2Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;

import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.domain.util.SessionKeyContainer;

public class TickerV2ConsumerImpl extends AbstractCallbackConsumer
        implements TickerV2Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public TickerV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    protected String getDelayPropertyName()
    {
        return TICKER_CONSUMER_DELAY_PROPERTY_NAME;
    }

    protected String getLogQueueDepthPropertyName()
    {
        return TICKER_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }

    public void acceptTicker(TickerStruct[] ticker, int queueDepth, short queueAction)
    {
        for (int i = 0; i < ticker.length; i++)
        {
            dispatchEvent(this, ChannelType.CB_TICKER,
                          new SessionKeyContainer(ticker[i].sessionName,
                                                  ticker[i].productKeys.productKey),
                          ticker[i]);

            dispatchEvent(this, ChannelType.CB_TICKER_BY_CLASS_V2,
                          new SessionKeyContainer(ticker[i].sessionName,
                                                  ticker[i].productKeys.classKey),
                          ticker[i]);

            increaseMethodCallCounter();

            if( isMethodCallLoggingOn() )
            {
                logMethodCall(ticker[i].sessionName + "." + ticker[i].productKeys.productKey,
                              "acceptTicker(TickerStruct[], int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptTicker", ticker.length);

        waitDelay();
    }
}