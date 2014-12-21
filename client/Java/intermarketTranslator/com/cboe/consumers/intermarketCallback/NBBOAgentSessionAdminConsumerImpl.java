package com.cboe.consumers.intermarketCallback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.intermarketCallback.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.domain.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

/**
 * This is the implementation of the CMINBBOAgentSessionAdmin callback object which
 * receives  market ticker data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Jing Chen
 */

public class NBBOAgentSessionAdminConsumerImpl implements NBBOAgentSessionAdminConsumer
{
	private EventChannelAdapter eventChannel = null;

    /**
     * NBBOAgentSessionAdminConsumerImpl constructor.
     *
     * @author Jing Chen
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public NBBOAgentSessionAdminConsumerImpl(EventChannelAdapter eventChannel)
    {
    	super();

    	this.eventChannel = eventChannel;
    }

    public void acceptForcedOut(String reason, int classKey, String session)
    {
        SessionKeyContainer sessionClass = new SessionKeyContainer(session, classKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_FORCED_OUT, sessionClass);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reason);
        eventChannel.dispatch(event);
    }

    public void acceptReminder(OrderReminderStruct reminder, int classKey, String session)
    {
        SessionKeyContainer sessionClass = new SessionKeyContainer(session, classKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_REMINDER, sessionClass);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reminder);
        eventChannel.dispatch(event);
    }

    public void acceptIntermarketAdminMessage(String sessionName, String originatingExchange, ProductKeysStruct productKey, AdminStruct message)
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        SessionKeyContainer sessionKeyContainer = null;

        sessionKeyContainer = new SessionKeyContainer(sessionName, productKey.classKey);
        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, sessionKeyContainer);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, message);
        eventChannel.dispatch(event);

    }
    public void acceptSatisfactionAlert(SatisfactionAlertStruct satisfactionAlertStruct, int classKey, String sessionName)
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        SessionKeyContainer sessionKeyContainer = null;
        sessionKeyContainer = new SessionKeyContainer(sessionName, classKey);
        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT, sessionKeyContainer);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, satisfactionAlertStruct);
        eventChannel.dispatch(event);
    }

    public void acceptBroadcastIntermarketAdminMessage(String sessionName, String originatingExchange, AdminStruct adminMessage)
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        SessionKeyContainer sessionKeyContainer = null;

        sessionKeyContainer = new SessionKeyContainer(sessionName, 0);
        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN_BROADCAST, sessionKeyContainer);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, adminMessage);
        eventChannel.dispatch(event);
    }
}
