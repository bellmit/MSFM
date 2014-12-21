package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.interfaces.events.*;
import com.cboe.util.event.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.util.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class RFQEventConsumerImpl extends com.cboe.idl.events.POA_RFQEventConsumer implements RFQConsumer {
    private RFQConsumer delegate;
    /**
     * constructor comment.
     */
    public RFQEventConsumerImpl(RFQConsumer rfqConsumer) {
        super();
        delegate = rfqConsumer;
    }

    public void acceptRFQ(RFQStruct rfq) {
        delegate.acceptRFQ(rfq);
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
