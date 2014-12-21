package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.MarketDataARCommandHelper;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.interfaces.events.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class TickerConsumerIECImpl extends BObject implements TickerConsumer {
    private ConcurrentEventChannelAdapter internalEventChannel;
    /**
     * constructor comment.
     */
    public TickerConsumerIECImpl() {
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
    public void acceptTickerForClass(RoutingParameterStruct routing, TimeStruct[] tradeTimes, TickerStruct[] tickers)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> Ticker classKey:" + routing.classKey);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;
        channelKey = new ChannelKey(ChannelKey.TICKER_BY_CLASS, new SessionKeyContainer(routing.sessionName, routing.classKey));
        event = internalEventChannel.getChannelEvent(this, channelKey, tickers);
        internalEventChannel.dispatch(event);
    }

    public void acceptTicker(int[] groups, InternalTickerStruct internalTicker)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> Ticker" + internalTicker.ticker.productKeys.productKey);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.TICKER_BY_CLASS,
                new SessionKeyContainer(internalTicker.ticker.sessionName, internalTicker.ticker.productKeys.classKey));
        TickerStruct[] tickers = {internalTicker.ticker};
        event = internalEventChannel.getChannelEvent(this, channelKey, tickers);
        internalEventChannel.dispatch(event);
    }
    
    public void acceptLargeTradeTickerDetailForClass(RoutingParameterStruct routing, InternalTickerDetailStruct[] tickerDetails)
    {
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
