//
// ------------------------------------------------------------------------
// Source file: TickerV4ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.TickerStructV4;

import com.cboe.interfaces.callback.TickerV4Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;

import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.marketData.express.TickerV4Impl;

public class TickerV4ConsumerImpl extends AbstractV4CallbackConsumer
        implements TickerV4Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public TickerV4ConsumerImpl(EventChannelAdapter eventChannel)
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

    public void acceptTicker(TickerStructV4[] ticker, int messageSequence, int queueDepth, short queueAction)
    {
        for (int i = 0; i < ticker.length; i++)
        {
            dispatchEvent(this, -1 * ChannelType.CB_TICKER_BY_CLASS_V4,
                          ticker[i].classKey,
                          new TickerV4Impl(ticker[i], messageSequence));

            increaseMethodCallCounter();

            if( isMethodCallLoggingOn() )
            {
                logMethodCall(ticker[i].exchange + "." + ticker[i].productKey,
                              "acceptTicker(TickerStructV4[], int, int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptTicker", ticker.length);
        waitDelay();
    }
}
