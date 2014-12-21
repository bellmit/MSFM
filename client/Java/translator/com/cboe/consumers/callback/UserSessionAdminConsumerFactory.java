package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIUserSessionAdmin.
 *
 * @author Connie Feng
 */

public class UserSessionAdminConsumerFactory
{
    /**
     * UserSessionAdminConsumerFactory constructor.
     *
     * @author Connie Feng
     */
    public UserSessionAdminConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIUserSessionAdmin callback Corba object.
     *
     * @author Connie Feng
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIUserSessionAdmin create(EventChannelAdapter eventProcessor)
    {
        try
        {
            UserSessionAdminConsumer theConsumer = new UserSessionAdminConsumerImpl(eventProcessor);
            UserSessionAdminConsumerDelegate delegate = new UserSessionAdminConsumerDelegate(theConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate, RemoteConnectionCBOEOrb.HEARTBEAT_POA_NAME);
            return CMIUserSessionAdminHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "UserSessionAdminConsumerFactory.create");
            return null;
        }
    }
}
