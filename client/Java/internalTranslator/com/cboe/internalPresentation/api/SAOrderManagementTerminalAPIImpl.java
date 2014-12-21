//
// -----------------------------------------------------------------------------------
// Source file: SAOrderManagementTerminalAPIImpl.java
//
// PACKAGE: com.cboe.internalPresentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.api;

import com.cboe.idl.omt.OrderManagementService;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;

import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.SystemException;

import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.api.OrderManagementTerminalAPIImpl;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

import com.cboe.consumers.internalPresentation.OrderRoutingConsumerFactory;
import com.cboe.infrastructureServices.foundationFramework.POAHelper;

public class SAOrderManagementTerminalAPIImpl extends OrderManagementTerminalAPIImpl
{
    private boolean alreadySubscribed = false;
    private OrderRoutingConsumer orderManagementConsumer;
    private EventChannelAdapter eventChannel;

    SAOrderManagementTerminalAPIImpl()
    {
        super();
        eventChannel = EventChannelAdapterFactory.find();
    }

    SAOrderManagementTerminalAPIImpl(OrderManagementService omtService)
    {
        this();
        initializeService(omtService);
    }

    public void cleanup()
    {
        disconnectConsumer(orderManagementConsumer);
    }

    public synchronized void subscribeOrdersForManualHandling() throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException
    {
        if(!alreadySubscribed)
        {
            GUILoggerHome.find().debug(getClass().getName() + ".subscribeOrdersForManualHandling() - about to call OrderManagementService.subscribeOrdersForManualHandling()...",
                    GUILoggerSABusinessProperty.ORDER_HANDLING);
            orderManagementConsumer = OrderRoutingConsumerFactory.create(eventChannel);
            omtService.subscribeOrdersForManualHandling(orderManagementConsumer, true);

            alreadySubscribed = true;
            GUILoggerHome.find().debug(getClass().getName() + ".subscribeOrdersForManualHandling() - done calling OrderManagementService.subscribeOrdersForManualHandling()",
                    GUILoggerSABusinessProperty.ORDER_HANDLING);
        }
        else
        {
            GUILoggerHome.find().debug(getClass().getName() + ".subscribeOrdersForManualHandling() - already subscribed to OrderManagementService.subscribeOrdersForManualHandling()",
                    GUILoggerSABusinessProperty.ORDER_HANDLING);
        }
    }

    private void disconnectConsumer(org.omg.CORBA.Object consumer)
    {
        try
        {
            POAHelper.disconnect(POAHelper.reference_to_servant(consumer, null), null);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(getClass().getName() + ".disconnectConsumer",
                                           "Error disconnecting user consumer::" + consumer, e);
        }
    }
}
