package com.cboe.consumers.eventChannel;

/**
 * Book Depth listener object listens on the CBOE event channel as a BookDepthConsumer.
 * There will only be a single book depth listener per CAS.
 *
 * @author William Wei
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class BookDepthEventConsumerImpl extends com.cboe.idl.events.POA_BookDepthEventConsumer
                                            implements BookDepthConsumer
{
    private BookDepthConsumer delegate;
    /**
     * MarketBestListener constructor comment.
     */
    public BookDepthEventConsumerImpl(BookDepthConsumer currentMarketConsumer) {
        super();
        delegate = currentMarketConsumer;
    }
    /**
     * This method is called by the CORBA event channel when a BBBO event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     */
    public void acceptBookDepth(int[] groups, BookDepthStruct bookDepth) {
        delegate.acceptBookDepth(groups, bookDepth);
    }

    public void acceptBookDepthForClass(RoutingParameterStruct routing, BookDepthStruct[] bookDepths) {
        delegate.acceptBookDepthForClass(routing, bookDepths);
    }

    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }
}
