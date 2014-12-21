//
// -----------------------------------------------------------------------------------
// Source file: LinkageStateConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.internalPresentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.internalPresentation;

import com.cboe.idl.consumers.ProductStatusConsumer;
import com.cboe.idl.consumers.ProductStatusConsumerPOA;

import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.application.shared.ServicesHelper;

public class LinkageStateConsumerFactory
{
    public static ProductStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            ProductStatusConsumerPOA eventStateConsumer = new LinkageStateConsumerImpl(eventProcessor);
            ProductStatusConsumer callbackConsumer = com.cboe.idl.consumers
                    .ProductStatusConsumerHelper
                    .narrow(ServicesHelper.connectToOrb(eventStateConsumer));
            return callbackConsumer;
        }
        catch(Exception e)
        {
            GUILoggerHome.find()
                    .exception(e, "LinkageStateConsumerFactory.create");
            return null;
        }
    }

}
