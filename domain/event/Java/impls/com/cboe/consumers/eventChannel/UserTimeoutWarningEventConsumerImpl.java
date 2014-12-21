package com.cboe.consumers.eventChannel;

/**
 * Best book listener object listens on the CBOE event channel as an BBBOConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Jeff Illian
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class UserTimeoutWarningEventConsumerImpl extends com.cboe.idl.infrastructureServices.infrastructureEvents.POA_UserTimeoutWarningConsumer
                                            implements UserTimeoutWarningConsumer
{
    private UserTimeoutWarningConsumer delegate;
    /**
     * UserTimeoutWarningEventConsumerImpl constructor comment.
     */
    public UserTimeoutWarningEventConsumerImpl(UserTimeoutWarningConsumer UserTimeoutWarningConsumer) {
        super();
        delegate = UserTimeoutWarningConsumer;
    }
    /**
     * This method is called by the CORBA event channel when a BBBO event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void acceptUserTimeoutWarning(String userName) {
        delegate.acceptUserTimeoutWarning(userName);
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
