package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.cmiProduct.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class TickerEventConsumerImpl extends com.cboe.idl.events.POA_TickerEventConsumer implements TickerConsumer{
    private TickerConsumer delegate;
    /**
     * constructor comment.
     */
    public TickerEventConsumerImpl(TickerConsumer tickerConsumer) {
        super();
        delegate = tickerConsumer;
    }

    public void acceptTickerForClass(RoutingParameterStruct routing, TimeStruct[] tradeTimes, TickerStruct[] tickers) {
        delegate.acceptTickerForClass(routing,tradeTimes,tickers );
    }

    public void acceptTicker(int[] groups, InternalTickerStruct ticker) {
        delegate.acceptTicker(groups, ticker);
    }

    public void acceptLargeTradeTickerDetailForClass(RoutingParameterStruct routing, InternalTickerDetailStruct[] tickerDetails) {
        delegate.acceptLargeTradeTickerDetailForClass(routing, tickerDetails);
    }

    /**
     * @author Jeff Illian
     */

    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }
}
