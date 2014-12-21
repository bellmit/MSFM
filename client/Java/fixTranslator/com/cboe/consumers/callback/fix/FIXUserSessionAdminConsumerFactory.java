package com.cboe.consumers.callback.fix;

import com.cboe.util.event.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create a FIX CMIUserSessionAdmin.
 */
public class FIXUserSessionAdminConsumerFactory
{
    /**
     * FIXUserSessionAdminConsumerFactory constructor.
     */
    public FIXUserSessionAdminConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIUserSessionAdmin callback object for the FIX session.
     *
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIUserSessionAdmin create(EventChannelAdapter eventProcessor)
    {
        try
        {
            return new FIXUserSessionAdminConsumerImpl(eventProcessor);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "FIXUserSessionAdminConsumerFactory.create");
            return null;
        }
    }
}
