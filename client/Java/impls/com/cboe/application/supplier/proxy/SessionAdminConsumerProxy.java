package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.UserSessionAdminSupplierFactory;
import com.cboe.domain.instrumentedChannel.supplier.proxy.InstrumentedGMDSupplierProxy;
import com.cboe.domain.util.CallbackDeregistrationInfoStruct;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.domain.GMDProxyHome;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
/**
 * UserSessionAdminProxy serves as a proxy to the UserSessionAdmin
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * UserSessionAdminSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.presentation.consumer.UserSessionAdminImpl
 * @see com.cboe.idl.cmiCallback.CMIUserSessionAdmin
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class SessionAdminConsumerProxy extends InstrumentedGMDSupplierProxy
{
    /**
     * TradingSessionStatusConsumerProxy constructor.
     *
     * @param userSessionAdmin a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public SessionAdminConsumerProxy(CMIUserSessionAdmin userSessionAdmin,
                                     BaseSessionManager sessionManager,
                                     boolean gmdTextMessaging,
                                     GMDProxyHome home)
    {
        super(sessionManager, UserSessionAdminSupplierFactory.find(sessionManager), gmdTextMessaging, home, userSessionAdmin);

        interceptor = new UserSessionAdminConsumerInterceptor(userSessionAdmin);
    }

    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public  void channelUpdate(ChannelEvent event)
    {
        long startTime = System.currentTimeMillis();
        long endTime;
//        Log.debug(this,"calling channelUpdate for " + getSessionManager());
        if (event != null)
        {
            try
            {
                ChannelKey key = (ChannelKey)event.getChannel();

                switch (key.channelType)
                {
                    case ChannelType.CB_HEARTBEAT:
                        // Call the proxied method passing the extracted HeartBeatStruct from the EventChannelEvent.
                        ((UserSessionAdminConsumerInterceptor)interceptor).acceptHeartBeat((HeartBeatStruct)event.getEventData());
                        endTime = System.currentTimeMillis();
                        long totalTime = endTime - startTime;
                        if ( (totalTime) > 5000 )
                        {
                            String smgr = getSessionManager().toString();
                            StringBuilder hb = new StringBuilder(smgr.length()+40);
                            hb.append("Heartbeat call for ").append(smgr).append(" took (").append(totalTime).append(" ms)");
                            Log.alarm(this, hb.toString());
                        }
                        break;

                    case ChannelType.CB_LOGOUT:
                        if (Log.isDebugOn())
                        {
                            Log.debug(this, "calling acceptLogout for " + getSessionManager());
                        }
                        // Call the proxied method passing the extracted String from the EventChannelEvent..
                        ((UserSessionAdminConsumerInterceptor)interceptor).acceptLogout((String)event.getEventData());
                        break;
                    case ChannelType.CB_AUTHENTICATION_NOTICE:
                        // Call the proxied method passing the extracted HeartBeatStruct from the EventChannelEvent.
                        ((UserSessionAdminConsumerInterceptor)interceptor).acceptAuthenticationNotice();
                        break;

                    case ChannelType.CB_TEXT_MESSAGE:
                        if (Log.isDebugOn())
                        {
                            Log.debug(this, "calling acceptTextMessage for " + getSessionManager());
                        }
                        MessageStruct   message = (MessageStruct)event.getEventData();
                        // Call the proxied method passing the extracted String from the EventChannelEvent..
                        ((UserSessionAdminConsumerInterceptor)interceptor).acceptTextMessage( message );
                        //After the msg is delivered to client, inform server
                        if (getGMDStatus())
                        {
                            ((SessionManager)getSessionManager()).acceptMessageDelivery(getSessionManager().getUserId(),
                                                                            message.messageKey);
                        }
                        break;
                    case ChannelType.CB_UNREGISTER_LISTENER:
                        CallbackDeregistrationInfoStruct deRegistrationInfo = (CallbackDeregistrationInfoStruct)event.getEventData();
                        CallbackInformationStruct callbackStruct = deRegistrationInfo.getCallbackInformationStruct();
                        String reason = deRegistrationInfo.getReason();
                        int    errorCode = deRegistrationInfo.getErrorCode();
                        String smgr = getSessionManager().toString();
                        StringBuilder calling = new StringBuilder(smgr.length()+callbackStruct.subscriptionInterface.length()+reason.length()+55);
                        calling.append("calling acceptCallbackRemoval for ").append(smgr)
                        		.append(" reason:").append(reason)
                               .append(" interface:").append(callbackStruct.subscriptionInterface);
                        Log.notification(this, calling.toString());
                        ((UserSessionAdminConsumerInterceptor)interceptor).acceptCallbackRemoval(callbackStruct, reason, errorCode);
                        break;
                    default:
                        break;
                }
            }
            catch(Exception e)
            {
                endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                String smgr = getSessionManager().toString();
                StringBuilder sb = new StringBuilder(smgr.length()+40);
                if ( (totalTime) > 5000 )
                {
                    sb.append("Heartbeat call for ").append(getSessionManager()).append(" took (").append(totalTime).append(" ms)");
                    Log.alarm(this, sb.toString());
                    sb.setLength(0);
                }
                sb.append("session:").append(smgr);
                Log.exception(this, sb.toString(), e);
                lostConnection(event);
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        String method = "";

        ChannelKey key = (ChannelKey) event.getChannel();

        switch(key.channelType)
        {
            case ChannelType.CB_HEARTBEAT:
                // Call the proxied method passing the extracted HeartBeatStruct from the EventChannelEvent.
                method = "acceptHeartBeat";
                break;

            case ChannelType.CB_LOGOUT:
                // Call the proxied method passing the extracted String from the EventChannelEvent..
                method = "acceptLogout";
                break;
            case ChannelType.CB_AUTHENTICATION_NOTICE:
                // Call the proxied method passing the extracted HeartBeatStruct from the EventChannelEvent.
                method = "acceptAuthenticationNotice";
                break;

            case ChannelType.CB_TEXT_MESSAGE:
                method = "acceptTextMessage";
                break;
            case ChannelType.CB_UNREGISTER_LISTENER:
                method = "acceptCallbackRemoval";
                break;
            default:
                break;
        }

        return method;
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.SESSION_ADMIN;
    }
}
