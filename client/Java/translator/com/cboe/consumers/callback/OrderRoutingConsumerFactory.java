//
// -----------------------------------------------------------------------------------
// Source file: \client\Java\translator\com\cboe\consumers\callback\OrderRoutingConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.consumers.callback;

import com.cboe.idl.ohsConsumers.OrderRoutingConsumerHelper;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;

import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.ohsEvents.OrderRoutingConsumerDelegate;


/**
 * Factory used to create OrderManagementConsumer
 *
 * @author  Shawn Khosravani
 * @since   May 22, 2007
 */
public class OrderRoutingConsumerFactory
{
    private static OrderRoutingConsumer consumer = null;

    private OrderRoutingConsumerFactory()
    {
    }

    // lazy initialization of static consumer is not thread safe
    public static synchronized OrderRoutingConsumer create(EventChannelAdapter eventProcessor)
    {
        if (consumer == null)
        {
            try
            {
                com.cboe.interfaces.ohsEvents.OrderRoutingConsumer internalConsumer = new OrderRoutingConsumerImpl(eventProcessor);
                OrderRoutingConsumerDelegate delegate = new OrderRoutingConsumerDelegate(internalConsumer);
                org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegate);
                consumer = OrderRoutingConsumerHelper.narrow (corbaObject);
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(e, "OrderRoutingConsumerFactory.create");
            }
        }
        return consumer;
    }
}
