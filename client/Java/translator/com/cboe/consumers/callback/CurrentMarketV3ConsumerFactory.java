//
// ------------------------------------------------------------------------
// FILE: CurrentMarketV3ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.CurrentMarketV3Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.CurrentMarketV3ConsumerDelegate;
import org.omg.CORBA.Object;

public class CurrentMarketV3ConsumerFactory
{
    public CurrentMarketV3ConsumerFactory()
    {
        super();
    }

    public static CMICurrentMarketConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            CurrentMarketV3Consumer consumer = new CurrentMarketV3ConsumerImpl(eventProcessor);
            CurrentMarketV3ConsumerDelegate delegate = new CurrentMarketV3ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMICurrentMarketConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "CurrentMarketV3ConsumerFactory.create");
            return null;
        }
    }
}

