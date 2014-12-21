//
// -----------------------------------------------------------------------------------
// Source file: RecapV5ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import org.omg.CORBA.Object;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.callback.RecapV4ConsumerDelegate;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumerHelper;
import com.cboe.interfaces.callback.RecapV4Consumer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.event.EventChannelAdapter;

public class RecapV5ConsumerFactory extends AbstractV4ConsumerFactory
{
    public RecapV5ConsumerFactory()
    {
        super();
    }

    public static CMIRecapConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            RecapV4Consumer consumer = new RecapV5ConsumerImpl(eventProcessor);
            RecapV4ConsumerDelegate delegate = new RecapV4ConsumerDelegate(consumer);
            org.omg.CORBA.Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate, getPOAName());
            return CMIRecapConsumerHelper.narrow(corbaObject);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e, "RecapV5ConsumerFactory.create");
            return null;
        }
    }
}

