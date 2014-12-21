package com.cboe.consumers.eventChannel;

/**
 * Best book listener object listens on the CBOE event channel as an AcceptTextMessageConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Keith A. Korecky
 */

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.textMessage.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class TextMessageEventConsumerImpl extends com.cboe.idl.events.POA_TextMessageEventConsumer implements TextMessageConsumer
{

    private TextMessageConsumer delegate;

    /**
     * AcceptTextMessageEventConsumerImpl constructor comment.
     */
    public TextMessageEventConsumerImpl(TextMessageConsumer textMessageConsumer)
    {
        super();
        delegate = textMessageConsumer;
    }

    /**
     * This method is called by the CORBA event channel when a AcceptTextMessage event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void acceptTextMessageForUser(String userId, MessageTransportStruct message)
    {
        delegate.acceptTextMessageForUser(userId,message);
    }

    /**
     * This method is called by the CORBA event channel when a AcceptTextMessageForProduct event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void acceptTextMessageForProductClass(short productKey, int classKey, MessageTransportStruct message)
    {
        delegate.acceptTextMessageForProductClass(productKey, classKey, message);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
        throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
