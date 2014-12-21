package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.domain.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

/**
 * This is the implementation of the UserSessionAdminConsumer callback object which
 * receives product status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Connie Feng
 */

public class UserSessionAdminConsumerImpl implements UserSessionAdminConsumer
{
    private EventChannelAdapter eventChannel = null;
    private int channelType = 0;

    /**
     * UserSessionAdminConsumerImpl constructor.
     *
     * @author Connie Feng
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public UserSessionAdminConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }

    /**
     * The callback method used by the CAS to publish  market ticker data.
     *
     * @author Connie Feng
     *
     * @param heartbeat the  heartbeat data to publish to all subscribed listeners
     */
    public HeartBeatStruct acceptHeartBeat(HeartBeatStruct heartbeat)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_HEARTBEAT, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, heartbeat);
        eventChannel.dispatch(event);

        return heartbeat;
    }

    public void acceptLogout( String reason )
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reason);
        eventChannel.dispatch(event);
    }

    public void acceptTextMessage(MessageStruct message)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_TEXT_MESSAGE, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, message);
        eventChannel.dispatch(event);
    }


    public void acceptAuthenticationNotice()
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_AUTHENTICATION_NOTICE, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, "");
        eventChannel.dispatch(event);
    }

    public void acceptCallbackRemoval(CallbackInformationStruct callback, String reason, int errorCode)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_UNREGISTER_LISTENER, new Integer(0));
        CallbackDeregistrationInfoStruct deregistrationInfo = new CallbackDeregistrationInfoStruct(callback, reason, errorCode);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, deregistrationInfo);
        eventChannel.dispatch(event);
    }
}
