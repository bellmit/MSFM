package com.cboe.consumers.eventChannel;

/**
 * AcceptTextMessageConsumer listener object listens on the CBOE event channel as an AcceptTextMessageConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Connie Feng
 */

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.textMessage.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import org.omg.CORBA.Object;
import org.omg.CORBA.Any;

public class TextMessageConsumerIECImpl extends BObject implements com.cboe.interfaces.events.TextMessageConsumer
{
    private EventChannelAdapter internalEventChannel;

    /**
     * AcceptTextMessageConsumerIECImpl constructor comment.
     */
    public TextMessageConsumerIECImpl()
    {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
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
        if (Log.isDebugOn())
        {
            Log.debug( this, "event received -> acceptTextMessageForUser : " + userId );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.TEXT_MESSAGE_BY_USER, userId);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, message);
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a AcceptTextMessage event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void acceptTextMessageForProductClass( short productKey, int classKey, MessageTransportStruct message)
    {
        if (Log.isDebugOn())
        {
            Log.debug( this, "event received -> acceptTextMessageForProductClass : " + productKey + "::" + classKey );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, message);
        internalEventChannel.dispatch(event);
    }
 }
