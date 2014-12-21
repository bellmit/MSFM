package com.cboe.consumers.internalPresentation;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.internalCallback.SystemControlConsumerDelegate;
import com.cboe.interfaces.callback.SystemControlCallbackConsumer;
import com.cboe.idl.consumers.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create SystemControlConsumer.
 * 
 */

public class SystemControlCallbackConsumerFactory
{
    /**
     * SystemControlConsumerFactory constructor.
     */
    public SystemControlCallbackConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new SystemControlConsumer callback Corba object.
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static SystemControlConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
			SystemControlCallbackConsumer eventStateConsumer = new SystemControlCallbackConsumerImpl(eventProcessor);
			POA_SystemControlConsumer_tie evenStatePOAObj = new SystemControlConsumerDelegate(eventStateConsumer);
			SystemControlConsumer callbackConsumer =
			com.cboe.idl.consumers.SystemControlConsumerHelper.narrow(ServicesHelper.connectToOrb(evenStatePOAObj));
			return callbackConsumer;
        }
		catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "SystemControlCallbackConsumerFactory.create");
			return null;
        }
		
    }
}
