package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIProductStatusConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class ProductStatusConsumerFactory
{
    /**
     * ProductStatusConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public ProductStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIProductStatusConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIProductStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            ProductStatusConsumer productStatusConsumer = new ProductStatusConsumerImpl(eventProcessor);
            ProductStatusConsumerDelegate delegate = new ProductStatusConsumerDelegate(productStatusConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIProductStatusConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "ProductStatusConsumerFactory.create");
            return null;
        }
    }
}
