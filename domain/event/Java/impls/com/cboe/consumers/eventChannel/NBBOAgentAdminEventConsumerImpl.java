/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 12:18:08 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.NBBOAgentAdminConsumer;
import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.domain.util.SessionKeyContainer;

public class NBBOAgentAdminEventConsumerImpl extends com.cboe.idl.events.POA_NBBOAgentAdminEventConsumer implements NBBOAgentAdminConsumer{
    private NBBOAgentAdminConsumer delegate;
    /**
     * constructor comment.
     */
    public NBBOAgentAdminEventConsumerImpl(NBBOAgentAdminConsumer agentAdminConsumer) {
        super();
        delegate = agentAdminConsumer;
    }

    public void acceptReminder(String userId, String sessionName, int classKey, OrderReminderStruct reminder) {
        delegate.acceptReminder(userId, sessionName, classKey, reminder);
    }

    public void acceptSatisfactionAlert(com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct struct)
    {
		//
    }

    public void acceptForcedTakeOver(String userId, String sessionName,  int classKey, String reason) {
        delegate.acceptForcedTakeOver(userId,sessionName, classKey, reason);
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
