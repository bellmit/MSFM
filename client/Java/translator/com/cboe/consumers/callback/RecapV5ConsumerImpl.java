//
// -----------------------------------------------------------------------------------
// Source file: RecapV5ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.LastSaleStructV4;
import com.cboe.idl.cmiMarketData.RecapStructV4;
import com.cboe.interfaces.callback.RecapV4Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;
import com.cboe.interfaces.presentation.marketData.express.RecapV4;
import com.cboe.presentation.marketData.express.LastSaleV4Impl;
import com.cboe.presentation.marketData.express.RecapV4Impl;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Consumer for the MarketQueryV5 Recap for a product.
 * 
 * @author Eric Maheo
 *
 */
public class RecapV5ConsumerImpl extends AbstractV4CallbackConsumer implements RecapV4Consumer, MarketDataQueueDepthLogging, MarketDataTimeDelay
{

    public RecapV5ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDelayPropertyName()
    {
        return RECAP_CONSUMER_DELAY_PROPERTY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLogQueueDepthPropertyName()
    {
        return RECAP_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }
    /**
     * {@inheritDoc}
     */
    public void acceptLastSale(LastSaleStructV4[] lastSales, int messageSequence, int queueDepth, short queueAction)
    {
        for(LastSaleStructV4 lastSale : lastSales)
        {
            LastSaleV4 lastSaleWrapper = new LastSaleV4Impl(lastSale, messageSequence);
            dispatchEvent(this, ChannelType.CB_LAST_SALE_BY_PRODUCT_V4, lastSale.productKey, lastSaleWrapper);

            increaseMethodCallCounter();

            if(isMethodCallLoggingOn())
            {
                logMethodCall(lastSale.exchange + '.' + lastSale.productKey,
                              "acceptLastSale(LastSaleStructV4[], int, int, short)");
            }
        }
        logQueueDepth(queueDepth, queueAction, "acceptLastSale", lastSales.length);
        waitDelay();
    }

    /**
     * {@inheritDoc}
     */
    public void acceptRecap(RecapStructV4[] recaps, int messageSequence, int queueDepth, short queueAction)
    {
        for(RecapStructV4 recap : recaps)
        {
            RecapV4 recapWrapper = new RecapV4Impl(recap, messageSequence);
            dispatchEvent(this, ChannelType.CB_RECAP_BY_PRODUCT_V4, recap.productKey, recapWrapper);

            increaseMethodCallCounter();

            if(isMethodCallLoggingOn())
            {
                logMethodCall(recap.exchange + '.' + recap.productKey,
                              "acceptRecap(RecapStructV4[], int, int, short)");
            }
        }
        logQueueDepth(queueDepth, queueAction, "acceptRecap", recaps.length);
        waitDelay();
    }

}
