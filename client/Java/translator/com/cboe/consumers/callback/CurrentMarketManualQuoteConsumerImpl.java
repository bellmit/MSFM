//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketManualQuoteConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;


import com.cboe.application.util.MarketDataHelper;
import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.idl.marketData.ManualQuoteDetailInternalStruct;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.internalCallback.CurrentMarketManualQuoteConsumer;
import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;
import com.cboe.presentation.marketData.express.CurrentMarketV4ProductContainerImpl;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Consumer used by marketQueryV5 while subscribing to a product for current market.
 *
 * @author Eric Maheo
 *
 */
public class CurrentMarketManualQuoteConsumerImpl extends AbstractV4CallbackConsumer
        implements CurrentMarketManualQuoteConsumer, MarketDataTimeDelay,
        MarketDataQueueDepthLogging
{

    protected CurrentMarketManualQuoteConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    @Override
    protected String getDelayPropertyName()
    {
        return CURRENT_MARKET_CONSUMER_DELAY_PROPERTY_NAME;
    }

    @Override
    protected String getLogQueueDepthPropertyName()
    {
        return CURRENT_MARKET_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }


    @Override
    public void acceptCurrentMarketForManualQuote(CurrentMarketStructV4[] bestMarkets,
                                                  CurrentMarketStructV4[] bestPublicMarkets, CurrentMarketStructV4[] manualQuoteMarkets,
                                                  ManualQuoteDetailInternalStruct[] manualQuoteDetails, int messageSequence,
                                                  int queueDepth, short queueAction)
    {
        Object[] params = new Object[]{manualQuoteMarkets, manualQuoteDetails};
        GUILoggerHome.find().audit("CurrentMarketManualQuoteConsumerImpl.acceptCurrentMarketForManualQuote() isn't implemented, but a callback was received. manualQuoteMarkets and manualQuoteDetails:", params);
    }

    @Override
    public void acceptCurrentMarket(CurrentMarketStructV4[] bestLimitMarkets,
                                    CurrentMarketStructV4[] bestPublicMarkets,
                                    int messageSequence, int queueDepth, short queueAction)
    {
        for(int i = 0; i < bestLimitMarkets.length; i++)
        {
            CurrentMarketV4ProductContainer currentMarket = new CurrentMarketV4ProductContainerImpl(messageSequence);

            currentMarket.setBestMarket(bestLimitMarkets[i]);
            int publicIndexAtBest = MarketDataHelper.findPublicIndexAtBest(bestLimitMarkets[i].exchange, bestLimitMarkets[i].productKey, bestPublicMarkets);
            if(publicIndexAtBest >= 0)
            {
                currentMarket.setBestPublicMarketAtTop(bestPublicMarkets[publicIndexAtBest]);
            }
            else
            {
                // We were not given a corresponding best public market at top, so create one so its zero volumes can be propagated to the cache
                currentMarket.setBestPublicMarketAtTop(MarketDataStructBuilder.buildCurrentMarketStructV4(bestLimitMarkets[i].classKey,
                        bestLimitMarkets[i].productType,
                        bestLimitMarkets[i].exchange,
                        bestLimitMarkets[i].productKey));
            }
            dispatchEvent(this, ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V4 , bestLimitMarkets[i].productKey, currentMarket);
            if(isMethodCallLoggingOn())
            {
                logMethodCall(bestLimitMarkets[i].exchange + "." + bestLimitMarkets[i].productKey,
                        "acceptCurrentMarket(CurrentMarketStructV4[], CurrentMarketStructV4[], int, int, short)");
            }
        }
        increaseMethodCallCounter();
        logQueueDepth(queueDepth, queueAction, "acceptCurrentMarket", bestLimitMarkets.length);
        waitDelay();
    }
}
