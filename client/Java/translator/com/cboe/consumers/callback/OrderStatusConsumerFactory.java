package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIOrderStatusConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class OrderStatusConsumerFactory
{
    /**
     * OrderStatusConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public OrderStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIOrderStatusConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIOrderStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            OrderStatusConsumer orderStatusConsumer = new OrderStatusConsumerImpl(eventProcessor);
            OrderStatusConsumerDelegate delegate= new OrderStatusConsumerDelegate(orderStatusConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIOrderStatusConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "OrderStatusConsumerFactory.create");
            return null;
        }
    }
}
