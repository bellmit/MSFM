//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.internalPresentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.internalPresentation;

import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;
import com.cboe.idl.ohsConsumers.POA_OrderRoutingConsumer_tie;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumerHelper;

import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.delegates.ohsEvents.OrderRoutingConsumerDelegate;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.consumers.callback.OrderRoutingConsumerImpl;

public class OrderRoutingConsumerFactory
{
    public OrderRoutingConsumerFactory()
    {
        super();
    }

    public static OrderRoutingConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            com.cboe.interfaces.ohsEvents.OrderRoutingConsumer eventStateConsumer = new OrderRoutingConsumerImpl(eventProcessor);
            POA_OrderRoutingConsumer_tie evenStatePOAObj = new OrderRoutingConsumerDelegate(eventStateConsumer);
            OrderRoutingConsumer callbackConsumer = OrderRoutingConsumerHelper.narrow(ServicesHelper.connectToOrb(evenStatePOAObj));
            return callbackConsumer;
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e, "OrderRoutingConsumerFactory.create");
            return null;
        }
    }
}
