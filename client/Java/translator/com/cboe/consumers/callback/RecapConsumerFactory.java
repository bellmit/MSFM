package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIRecapConsumers.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class RecapConsumerFactory
{
    /**
     * RecapConsumerFactory constructor.
     *
     * @author Keith A. Korecky
     */
    public RecapConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIRecapConsumer callback Corba object.
     *
     * @author Keith A. Korecky
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIRecapConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            RecapConsumer RecapConsumer = new RecapConsumerImpl(eventProcessor);
            RecapConsumerDelegate delegate = new RecapConsumerDelegate(RecapConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIRecapConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "RecapConsumerFactory.create");
            return null;
        }
    }
}
