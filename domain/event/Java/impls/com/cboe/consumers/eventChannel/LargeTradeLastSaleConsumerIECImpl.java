package com.cboe.consumers.eventChannel;

import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.MarketDataARCommandHelper;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.TickerConsumer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
//
// -----------------------------------------------------------------------------------
// Source file: LargeTradeLastSaleConsumerIECImpl.java
//
// PACKAGE: PACKAGE_NAME
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.

// -----------------------------------------------------------------------------------
public class LargeTradeLastSaleConsumerIECImpl extends BObject implements TickerConsumer {
    private ConcurrentEventChannelAdapter internalEventChannel;

    /**
     * constructor comment.
     */
    public LargeTradeLastSaleConsumerIECImpl() {
        super();
        try
        {
            internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.MARKETDATA_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception("Exception getting MARKETDATA_INSTRUMENTED_IEC!", e);
        }
    }

    public void acceptTickerForClass(RoutingParameterStruct routing, TimeStruct[] tradeTimes, TickerStruct[] tickers) {
    }

    public void acceptTicker(int[] groups, InternalTickerStruct internalTicker) {
    }

    public void acceptLargeTradeTickerDetailForClass(RoutingParameterStruct routing, InternalTickerDetailStruct[] tickerDetails) {
        ChannelKey channelKey = null;
        ChannelEvent event = null;

        if (MarketDataARCommandHelper.PublishLTLSData.isPublishLTLS()) {
            channelKey = new ChannelKey(ChannelKey.LARGE_TRADE_LAST_SALE_BY_CLASS,
                    new SessionKeyContainer(routing.sessionName, routing.classKey));
            if (Log.isDebugOn())
            {
                Log.debug(this, "event received -> LargeTradeTickerDetails[" + tickerDetails.length + "] with channelKey: " + channelKey);
            }
            event = internalEventChannel.getChannelEvent(this, channelKey, tickerDetails);
            internalEventChannel.dispatch(event);
        }
    }
}
