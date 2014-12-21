package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIStrategyStatusConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 09/20/1999
 */

public class StrategyStatusConsumerFactory
{
    /**
     * StrategyStatusConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public StrategyStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIStrategyStatusConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIStrategyStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            StrategyStatusConsumer strategyStatusConsumer = new StrategyStatusConsumerImpl(eventProcessor);
            StrategyStatusConsumerDelegate delegate = new StrategyStatusConsumerDelegate(strategyStatusConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIStrategyStatusConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "StrategyStatusConsumerFactory.create");
            return null;
        }
    }
}
