//
// -----------------------------------------------------------------------------------
// Source file: RecapV4ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.LastSaleStructV4;
import com.cboe.idl.cmiMarketData.RecapStructV4;

import com.cboe.interfaces.callback.RecapV4Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;
import com.cboe.interfaces.presentation.marketData.express.RecapV4;
import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.ChannelType;

import com.cboe.presentation.marketData.express.RecapV4Impl;
import com.cboe.presentation.marketData.express.LastSaleV4Impl;

public class RecapV4ConsumerImpl extends AbstractV4CallbackConsumer
        implements RecapV4Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public RecapV4ConsumerImpl(EventChannelAdapter eventChannel)
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

    public void acceptLastSale(LastSaleStructV4[] lastSales, int messageSequence, int queueDepth, short queueAction)
    {
        for(int i = 0; i < lastSales.length; i++)
        {
            LastSaleV4 lastSaleWrapper = new LastSaleV4Impl(lastSales[i], messageSequence);
            dispatchEvent(this, -1 * ChannelType.CB_LAST_SALE_BY_CLASS_V4, lastSales[i].classKey, lastSaleWrapper);

            increaseMethodCallCounter();

            if(isMethodCallLoggingOn())
            {
                logMethodCall(lastSales[i].exchange + "." + lastSales[i].productKey,
                              "acceptLastSale(LastSaleStructV4[], int, int, short)");
            }
        }
        logQueueDepth(queueDepth, queueAction, "acceptLastSale", lastSales.length);
        waitDelay();
    }

    public void acceptRecap(RecapStructV4[] recaps, int messageSequence, int queueDepth, short queueAction)
    {
        for(int i = 0; i < recaps.length; i++)
        {
            RecapV4 recapWrapper = new RecapV4Impl(recaps[i], messageSequence);
            dispatchEvent(this, -1 * ChannelType.CB_RECAP_BY_CLASS_V4, recaps[i].classKey, recapWrapper);

            increaseMethodCallCounter();

            if(isMethodCallLoggingOn())
            {
                logMethodCall(recaps[i].exchange + "." + recaps[i].productKey,
                              "acceptRecap(RecapStructV4[], int, int, short)");
            }
        }
        logQueueDepth(queueDepth, queueAction, "acceptRecap", recaps.length);
        waitDelay();
    }
}
