package com.cboe.consumers.eventChannel;

/**
 * @author Mike Pyatetsky */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.idl.product.*;

public class QuoteRiskManagementProfileEventConsumerImpl extends com.cboe.idl.events.POA_QuoteRiskManagementProfileEventConsumer implements QuoteRiskManagementProfileConsumer{
    private QuoteRiskManagementProfileConsumer delegate;
    /**
     * constructor comment.
     */
    public QuoteRiskManagementProfileEventConsumerImpl(QuoteRiskManagementProfileConsumer quoteRiskManagementProfileConsumer) {
        super();
        delegate = quoteRiskManagementProfileConsumer;
    }

    public void acceptNewProfile(String userId, QuoteRiskManagementProfileStruct quoteRiskManagementProfile)
    {
        delegate.acceptNewProfile(userId, quoteRiskManagementProfile);
    }

    public void acceptRemoveProfile(String userId, int classKey)
    {
        delegate.acceptRemoveProfile(userId, classKey);
    }

    public void acceptNewGlobalStatusEnabled(String userId, boolean status)
    {
        delegate.acceptNewGlobalStatusEnabled(userId, status);
    }

    /**
     * @author Mike Pyatetsky     */

    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }

}
