//
// -----------------------------------------------------------------------------------
// Source file: NBBOV4ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;
import com.cboe.idl.cmiCallbackV4.CMINBBOConsumerHelper;

import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.callback.NBBOV4ConsumerDelegate;

import org.omg.CORBA.Object;

import com.cboe.interfaces.callback.NBBOV4Consumer;

public class NBBOV4ConsumerFactory extends AbstractV4ConsumerFactory
{
    public NBBOV4ConsumerFactory()
    {
        super();
    }

    public static CMINBBOConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            NBBOV4Consumer consumer = new NBBOV4ConsumerImpl(eventProcessor);
            NBBOV4ConsumerDelegate delegate = new NBBOV4ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate, getPOAName());
            return CMINBBOConsumerHelper.narrow(corbaObject);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e, "NBBOV4ConsumerFactory.create");
            return null;
        }
    }
}
