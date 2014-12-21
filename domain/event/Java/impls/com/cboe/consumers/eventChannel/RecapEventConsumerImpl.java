package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
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

public class RecapEventConsumerImpl extends com.cboe.idl.events.POA_RecapEventConsumer implements RecapConsumer {
    private RecapConsumer delegate;
    /**
     * constructor comment.
     */
    public RecapEventConsumerImpl(RecapConsumer recapConsumer) {
        super();
        delegate = recapConsumer;
    }

    public void acceptRecapForClass(RoutingParameterStruct routing, RecapStruct[] recaps) {
        delegate.acceptRecapForClass(routing,recaps);
    }

    public void acceptRecap(int[] groups, RecapStruct recap) {
        delegate.acceptRecap(groups, recap);
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
