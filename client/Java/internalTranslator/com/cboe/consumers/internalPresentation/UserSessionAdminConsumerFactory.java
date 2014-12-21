package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create UserSessionAdminConsumers.
 *
 * @author Keith A. Korecky
 * @version 7/9/99
 */

public class UserSessionAdminConsumerFactory
{
    /**
     * UserSessionAdminConsumerFactory constructor.
     *
     * @author Keith A. Korecky
     */
    public UserSessionAdminConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new UserSessionAdminConsumer callback Corba object.
     *
     * @author Keith A. Korecky
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIUserSessionAdmin create(EventChannelAdapter eventProcessor)
    {
        try
        {
            UserSessionAdminConsumer userSessionAdminConsumer = new UserSessionAdminConsumerImpl(eventProcessor);
            POA_CMIUserSessionAdmin_tie poaObj = new POA_CMIUserSessionAdmin_tie(userSessionAdminConsumer);
            return CMIUserSessionAdminHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "UserSessionAdminConsumerFactory.create");
            return null;
        }
    }
}
