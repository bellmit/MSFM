package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMICurrentMarketConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class CurrentMarketConsumerFactory
{
    /**
     * CurrentMarketConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public CurrentMarketConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMICurrentMarketConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMICurrentMarketConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            CurrentMarketConsumer currentMarketConsumer = new CurrentMarketConsumerImpl(eventProcessor);
            CurrentMarketConsumerDelegate delegate = new CurrentMarketConsumerDelegate(currentMarketConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMICurrentMarketConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "CurrentMarketConsumerFactory.create");
            return null;
        }
    }
}
