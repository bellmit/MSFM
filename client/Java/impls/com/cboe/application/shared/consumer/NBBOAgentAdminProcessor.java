/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 1:57:58 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.shared.consumer;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.interfaces.application.NBBOAgentAdminCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.domain.util.UserSessionClassContainer;
import com.cboe.domain.util.IntermarketAdminMessageContainer;
import com.cboe.domain.util.IntermarketBroadcastMessageContainer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Emily Huang
 */
public class NBBOAgentAdminProcessor extends InstrumentedProcessor {
	private NBBOAgentAdminCollector parent = null;

  public NBBOAgentAdminProcessor(NBBOAgentAdminCollector parent) {
    super(parent);
    this.parent = parent;
  }

  public void setParent(NBBOAgentAdminCollector parent) {
    this.parent = parent;
  }

  public NBBOAgentAdminCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event)
  {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    Object eventData = event.getEventData();
    UserSessionClassContainer sessionKey;

    if (parent != null)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.NBBO_AGENT_FORCED_OUT :
                String reason = (String)eventData;
                sessionKey = (UserSessionClassContainer)channelKey.key;
                parent.acceptForcedOut(reason, sessionKey.getClassKey(), sessionKey.getSessionName());
            break;

            case ChannelType.NBBO_AGENT_REMINDER :
                OrderReminderStruct reminder = (OrderReminderStruct)eventData;
                sessionKey = (UserSessionClassContainer)channelKey.key;
                parent.acceptReminder(reminder, sessionKey.getClassKey(),sessionKey.getSessionName() );
            break;

            case ChannelType.ALERT_SATISFACTION :
                parent.acceptSatisfactionAlert((SatisfactionAlertStruct)eventData);
            break;

            case ChannelType.INTERMARKET_ADMIN_MESSAGE :
                IntermarketAdminMessageContainer imAdminMessage = (IntermarketAdminMessageContainer)eventData;
                parent.acceptIntermarketAdminMessage(imAdminMessage.getSessionName(), imAdminMessage.getSourceExchange(), imAdminMessage.getProductKeysStruct(), imAdminMessage.getAdminStruct() );
            break;

            case ChannelType.INTERMARKET_ADMIN_MESSAGE_BROADCAST :
                IntermarketBroadcastMessageContainer imBroadcastMessage = (IntermarketBroadcastMessageContainer)eventData;
                parent.acceptBroadcastIntermarketAdminMessage(imBroadcastMessage.getSessionName(), imBroadcastMessage.getSourceExchange(), imBroadcastMessage.getAdminStruct() );
            break;
            default:
                if (Log.isDebugOn())
                {
                    Log.debug("NBBOAgentAdminProcessor -> Wrong Channel : " + channelKey.channelType);
                }
      }
    }
  }

  public String getMessageType()
  {
    return SupplierProxyMessageTypes.NBBO_AGENT_ADMIN;
  }
}
