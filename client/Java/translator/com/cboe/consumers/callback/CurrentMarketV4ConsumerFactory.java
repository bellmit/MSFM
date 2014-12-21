//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV4ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumerHelper;

import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.application.shared.RemoteConnectionFactory;

import org.omg.CORBA.Object;

public class CurrentMarketV4ConsumerFactory extends AbstractV4ConsumerFactory
{
    public CurrentMarketV4ConsumerFactory()
    {
        super();
    }

    public static CMICurrentMarketConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            com.cboe.interfaces.callback.CurrentMarketV4Consumer consumer = new com.cboe.consumers.callback.CurrentMarketV4ConsumerImpl(eventProcessor);
            com.cboe.delegates.callback.CurrentMarketV4ConsumerDelegate delegate = new com.cboe.delegates.callback.CurrentMarketV4ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate, getPOAName());
            return CMICurrentMarketConsumerHelper.narrow(corbaObject);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e, "CurrentMarketV4ConsumerFactory.create");
            return null;
        }
    }
}
