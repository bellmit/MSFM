//
// -----------------------------------------------------------------------------------
// Source file: TickerV5ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.TickerStructV4;
import com.cboe.interfaces.callback.TickerV4Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.presentation.marketData.express.TickerV4Impl;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Consumer V5 for the ticker consumer per product.
 * 
 * @author Eric Maheo
 *
 */
public class TickerV5ConsumerImpl extends AbstractV4CallbackConsumer implements TickerV4Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{

    public TickerV5ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDelayPropertyName()
    {
        return TICKER_CONSUMER_DELAY_PROPERTY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLogQueueDepthPropertyName()
    {
        return TICKER_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptTicker(TickerStructV4[] ticker, int messageSequence, int queueDepth, short queueAction)
    {
        for(TickerStructV4 aTicker : ticker)
        {
            //todo replaced class key with product key
            dispatchEvent(this, ChannelType.CB_TICKER_BY_PRODUCT_V4, aTicker.productKey,
                          new TickerV4Impl(aTicker, messageSequence));

            increaseMethodCallCounter();

            if(isMethodCallLoggingOn())
            {
                logMethodCall(aTicker.exchange + '.' + aTicker.productKey,
                              "acceptTicker(TickerStructV4[], int, int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptTicker", ticker.length);
        waitDelay();
    }

}
