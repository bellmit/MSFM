package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIRFQConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class RFQConsumerFactory
{
    /**
     * UnderlyingRecapConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public RFQConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIRFQConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIRFQConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            RFQConsumer rfqConsumer = new RFQConsumerImpl(eventProcessor);
            RFQConsumerDelegate delegate = new RFQConsumerDelegate(rfqConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIRFQConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "RFQConsumerFactory.create");
            return null;
        }
    }
}
