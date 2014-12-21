//
// -----------------------------------------------------------------------------------
// Source file: TickerV5ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import org.omg.CORBA.Object;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.callback.TickerV4ConsumerDelegate;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumerHelper;
import com.cboe.interfaces.callback.TickerV4Consumer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.event.EventChannelAdapter;

public class TickerV5ConsumerFactory  extends AbstractV4ConsumerFactory
{
    public TickerV5ConsumerFactory()
    {
        super();
    }

    public static CMITickerConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            TickerV4Consumer consumer = new TickerV5ConsumerImpl(eventProcessor);
            TickerV4ConsumerDelegate delegate = new TickerV4ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate, getPOAName());
            return CMITickerConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "TickerV4ConsumerFactory.create");
            return null;
        }
    }
}
