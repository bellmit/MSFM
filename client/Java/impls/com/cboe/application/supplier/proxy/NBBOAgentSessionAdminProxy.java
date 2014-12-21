/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 2:19:46 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.NBBOAgentAdminSupplierFactory;
import com.cboe.domain.supplier.proxy.CallbackSupplierProxy;
import com.cboe.domain.util.IntermarketAdminMessageContainer;
import com.cboe.domain.util.IntermarketBroadcastMessageContainer;
import com.cboe.domain.util.UserSessionClassContainer;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdmin;
import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;

public class NBBOAgentSessionAdminProxy extends InstrumentedConsumerProxy
{

    /**
     * NBBOAgentSessionAdminConsumerProxy constructor.
     *
     */
    public NBBOAgentSessionAdminProxy(CMINBBOAgentSessionAdmin nbboAgentSessionAdmin, BaseSessionManager sessionManager)
    {
        super(sessionManager, NBBOAgentAdminSupplierFactory.find(), nbboAgentSessionAdmin);
        interceptor = new NBBOAgentSessionAdminConsumerInterceptor(nbboAgentSessionAdmin);
    }

    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }

        BaseSessionManager      baseSessionManager;
        if (event != null)
        {
            try
            {
                ChannelKey channelKey = (ChannelKey) event.getChannel();
                baseSessionManager = getSessionManager();
                Object data = event.getEventData();

                UserSessionClassContainer userSessionKey = (UserSessionClassContainer)channelKey.key;
                String sessionName = userSessionKey.getSessionName();
                int classKey = userSessionKey.getClassKey();
                switch(channelKey.channelType)
                {
                    case ChannelType.CB_NBBO_AGENT_FORCED_OUT:
                        String reason = (String)data;
                        ((NBBOAgentSessionAdminConsumerInterceptor)interceptor).acceptForcedOut(reason, classKey, sessionName);
                        break;

                    case ChannelType.CB_NBBO_AGENT_REMINDER:
                        ((NBBOAgentSessionAdminConsumerInterceptor)interceptor).acceptReminder((OrderReminderStruct)data, classKey, sessionName);
                        break;
                    case ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT:
                        ((NBBOAgentSessionAdminConsumerInterceptor)interceptor).acceptSatisfactionAlert((SatisfactionAlertStruct)data, classKey, sessionName);
                        break;
                    case ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN:
                        IntermarketAdminMessageContainer imMessage = (IntermarketAdminMessageContainer)data;
                        ((NBBOAgentSessionAdminConsumerInterceptor)interceptor).acceptIntermarketAdminMessage(imMessage.getSessionName(),
                                                                            imMessage.getSourceExchange(),
                                                                            imMessage.getProductKeysStruct(),
                                                                            imMessage.getAdminStruct());
                        break;
                    case ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN_BROADCAST:
                        IntermarketBroadcastMessageContainer message = (IntermarketBroadcastMessageContainer)data;
                        ((NBBOAgentSessionAdminConsumerInterceptor)interceptor).acceptBroadcastIntermarketAdminMessage(message.getSessionName(),
                                                                            message.getSourceExchange(),
                                                                            message.getAdminStruct());
                        break;
                    default:
                        if (Log.isDebugOn())
                        {
                            Log.debug(this, "wrong channel");
                        }
                        break;
                }
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                lostConnection(event);
            }
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        String method = "";

        ChannelKey key = (ChannelKey) event.getChannel();

        switch(key.channelType)
        {
            case ChannelType.CB_NBBO_AGENT_FORCED_OUT:
                method = "acceptForcedTakeOver";
                break;

            case ChannelType.CB_NBBO_AGENT_REMINDER:
                method = "acceptReminder";
                break;
            case ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT:
                method = "acceptSatisfactionAlert";
                break;
            case ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN:
                method = "acceptIntermarketAdmin";
                break;
            case ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN_BROADCAST:
                method = "accepIntermarketAdminBroadcast";
                break;
            default:
                break;
        }
        return method;
    }
    
    /** Get string identifying the type of instrumentation data.
     * @return Value to identify data in instrumentation output file.
     */
    public String getMessageType()
    {
        return SupplierProxyMessageTypes.NBBO_AGENT_ADMIN;
    }
}
