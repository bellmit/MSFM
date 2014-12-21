//
// ------------------------------------------------------------------------
// FILE: CurrentMarketV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.CurrentMarketV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.CurrentMarketV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class CurrentMarketV2ConsumerFactory
{
    public CurrentMarketV2ConsumerFactory()
    {
        super();
    }

    public static CMICurrentMarketConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            CurrentMarketV2Consumer consumer = new CurrentMarketV2ConsumerImpl(eventProcessor);
            CurrentMarketV2ConsumerDelegate delegate = new CurrentMarketV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMICurrentMarketConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "CurrentMarketV2ConsumerFactory.create");
            return null;
        }
    }
}

