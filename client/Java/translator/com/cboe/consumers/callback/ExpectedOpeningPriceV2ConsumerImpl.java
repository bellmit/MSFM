//
// ------------------------------------------------------------------------
// Source file: ExpectedOpeningPriceV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;

import com.cboe.interfaces.callback.ExpectedOpeningPriceV2Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;

import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.domain.util.SessionKeyContainer;

public class ExpectedOpeningPriceV2ConsumerImpl extends AbstractCallbackConsumer
        implements ExpectedOpeningPriceV2Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public ExpectedOpeningPriceV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    protected String getDelayPropertyName()
    {
        return EOP_CONSUMER_DELAY_PROPERTY_NAME;
    }

    protected String getLogQueueDepthPropertyName()
    {
        return EOP_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }

    public void acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct[] expectedOpeningPrice, int queueDepth, short queueAction)
    {
        for( int i = 0; i < expectedOpeningPrice.length; i++ )
        {
            dispatchEvent(this, ChannelType.CB_EXPECTED_OPENING_PRICE,
                          new SessionKeyContainer(expectedOpeningPrice[i].sessionName,
                                                  expectedOpeningPrice[i].productKeys.classKey),
                          expectedOpeningPrice[i]);

            dispatchEvent(this, ChannelType.CB_EXPECTED_OPENING_PRICE_BY_PRODUCT,
                          new SessionKeyContainer(expectedOpeningPrice[i].sessionName, expectedOpeningPrice[i].productKeys.productKey),
                          expectedOpeningPrice[i]);

            increaseMethodCallCounter();

            if( isMethodCallLoggingOn() )
            {
                logMethodCall(expectedOpeningPrice[i].sessionName + "." + expectedOpeningPrice[i].productKeys.productKey,
                              "acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct[], int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptExpectedOpeningPrice", expectedOpeningPrice.length);

        waitDelay();
    }
}
