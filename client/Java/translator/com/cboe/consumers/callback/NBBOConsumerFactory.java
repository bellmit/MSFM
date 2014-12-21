package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMINBBOConsumers.
 *
 * @author Jimmy Wang
 * @version 06/07/2000
 */

public class NBBOConsumerFactory
{
    /**
     * NBBOConsumerFactory constructor.
     *
     * @author Jimmy Wang
     */
    public NBBOConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMINBBOConsumer callback Corba object.
     *
     * @author Jimmy Wang
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMINBBOConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            NBBOConsumer consumer = new NBBOConsumerImpl(eventProcessor);
            NBBOConsumerDelegate delegate = new NBBOConsumerDelegate(consumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMINBBOConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "NBBOConsumerFactory.create");
            return null;
        }
    }
}
