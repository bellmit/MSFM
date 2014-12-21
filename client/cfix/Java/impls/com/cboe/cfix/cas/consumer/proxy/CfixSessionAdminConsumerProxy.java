/**
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Feb 20, 2003
 * Time: 5:27:34 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.cfix.cas.consumer.proxy;

import com.cboe.cfix.cas.supplier.*;
import com.cboe.domain.instrumentedChannel.supplier.proxy.InstrumentedGMDSupplierProxy;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.*;
import com.cboe.interfaces.domain.session.*;
import com.cboe.util.*;
import com.cboe.util.channel.*;

public class CfixSessionAdminConsumerProxy extends InstrumentedGMDSupplierProxy
{
    private CfixUserSessionAdminConsumer cfixUserSessionAdmin;

    public CfixSessionAdminConsumerProxy(CfixUserSessionAdminConsumer userSessionAdmin, BaseSessionManager sessionManager, boolean gmdTextMessaging, GMDProxyHome home)
    {
        super(sessionManager, CfixUserSessionAdminSupplierFactory.find(sessionManager), gmdTextMessaging, home, userSessionAdmin.toString());
        this.cfixUserSessionAdmin = userSessionAdmin;
    }
    public  void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        if (event != null)
        {
            try
            {
                ChannelKey key = (ChannelKey)event.getChannel();

                switch (key.channelType)
                {
                    case ChannelType.CB_LOGOUT:
                        if (Log.isDebugOn())
                        {
                            Log.debug(this, "calling acceptLogout for " + getSessionManager());
                        }
                        // Call the proxied method passing the extracted String from the EventChannelEvent..
                        cfixUserSessionAdmin.acceptLogout((String)event.getEventData());
                        break;
                    case ChannelType.CB_TEXT_MESSAGE:
                        if (Log.isDebugOn())
                        {
                            Log.debug(this, "calling acceptTextMessage for " + getSessionManager());
                        }
                        MessageStruct   message = (MessageStruct)event.getEventData();
                        // Call the proxied method passing the extracted String from the EventChannelEvent..
                        cfixUserSessionAdmin.acceptTextMessage( message );
                        //After the msg is delivered to client, inform server
                        if (getGMDStatus())
                        {
                            ((SessionManager)getSessionManager()).acceptMessageDelivery(getSessionManager().getUserId(),
                                                                            message.messageKey);
                        }
                        break;
                    default:
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
            case ChannelType.CB_LOGOUT:
                // Call the proxied method passing the extracted String from the EventChannelEvent..
                method = "acceptLogout";
                break;
            case ChannelType.CB_TEXT_MESSAGE:
                method = "acceptTextMessage";
                break;
            default:
                break;
        }
        return method;

    }
    public void startMethodInstrumentation(boolean privateOnly){}
    public void stopMethodInstrumentation(){}
    public void queueInstrumentationInitiated(){}
    public String getMessageType()
    {
        return SupplierProxyMessageTypes.SESSION_ADMIN;
    }
}
