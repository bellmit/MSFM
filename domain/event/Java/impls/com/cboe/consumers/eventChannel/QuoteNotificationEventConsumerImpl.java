package com.cboe.consumers.eventChannel;

/**
 * Quote Locked listener object listens on the CBOE event channel as a QuoteNotificationConsumer.
 * There will only be a single quote locking listener per CAS.
 *
 * @author William Wei
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class QuoteNotificationEventConsumerImpl extends com.cboe.idl.events.POA_QuoteNotificationEventConsumer
                                            implements QuoteNotificationConsumer
{
    private QuoteNotificationConsumer delegate;
    /**
     * MarketBestListener constructor comment.
     */
    public QuoteNotificationEventConsumerImpl(QuoteNotificationConsumer quoteLockConsumer) {
        super();
        delegate = quoteLockConsumer;
    }
    /**
     * This method is called by the CORBA event channel when a BBBO event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     */
    public void acceptQuoteLockedNotification(int[] userKeys, LockNotificationStruct quoteLock) {
        delegate.acceptQuoteLockedNotification(userKeys, quoteLock);
    }

    public void acceptQuoteLockedNotificationForClass(RoutingParameterStruct routing, LockNotificationStruct[] quoteLocks) {
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
