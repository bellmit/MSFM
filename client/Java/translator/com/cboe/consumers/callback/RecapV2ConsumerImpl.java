//
// ------------------------------------------------------------------------
// Source file: RecapV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.RecapStruct;

import com.cboe.interfaces.callback.RecapV2Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;

import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.domain.util.SessionKeyContainer;

public class RecapV2ConsumerImpl extends AbstractCallbackConsumer
        implements RecapV2Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public RecapV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    protected String getDelayPropertyName()
    {
        return RECAP_CONSUMER_DELAY_PROPERTY_NAME;
    }

    protected String getLogQueueDepthPropertyName()
    {
        return RECAP_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }

    public void acceptRecap(RecapStruct[] recap, int queueDepth, short queueAction)
    {
        for (int i = 0; i < recap.length; i++)
        {
            dispatchEvent(this, -1 * ChannelType.CB_RECAP_BY_CLASS,
                          new SessionKeyContainer(recap[i].sessionName,
                                                  recap[i].productKeys.classKey),
                          recap[i]);

            dispatchEvent(this, -1 * ChannelType.CB_RECAP_BY_PRODUCT,
                          new SessionKeyContainer(recap[i].sessionName,
                                                  recap[i].productKeys.productKey),
                          recap[i]);

            increaseMethodCallCounter();

            if( isMethodCallLoggingOn() )
            {
                logMethodCall(recap[i].sessionName + "." + recap[i].productKeys.productKey,
                              "acceptRecap(RecapStruct[], int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptRecap", recap.length);

        waitDelay();
    }
}
