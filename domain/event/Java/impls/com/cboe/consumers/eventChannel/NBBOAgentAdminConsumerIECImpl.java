/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 12:17:25 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.consumers.eventChannel;

import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.util.UserSessionClassContainer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.interfaces.events.NBBOAgentAdminConsumer;
import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;

public class NBBOAgentAdminConsumerIECImpl extends BObject implements NBBOAgentAdminConsumer {
    private InstrumentedEventChannelAdapter internalEventChannel = null;
    /**
     * constructor comment.
     */
    public NBBOAgentAdminConsumerIECImpl() {
        super();
        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
    }

    public void acceptReminder(String userId, String sessionName, int classKey, OrderReminderStruct reminder) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptReminder : userid = " + userId );
        }

        ChannelKey channelKey = new ChannelKey(ChannelKey.NBBO_AGENT_REMINDER, new UserSessionClassContainer(userId, sessionName, classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, reminder);
        internalEventChannel.dispatch(event);
    }

    public void acceptForcedTakeOver(String userId, String sessionName,  int classKey, String reason) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptForcedTakeOver");
        }

        ChannelKey channelKey = new ChannelKey(ChannelKey.NBBO_AGENT_FORCED_OUT, new UserSessionClassContainer(userId, sessionName, classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, reason);
        internalEventChannel.dispatch(event);
    }

}
