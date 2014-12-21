//
// ------------------------------------------------------------------------
// FILE: TickerV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.TickerV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.TickerV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class TickerV2ConsumerFactory
{
    public TickerV2ConsumerFactory()
    {
        super();
    }

    public static CMITickerConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            TickerV2Consumer consumer = new TickerV2ConsumerImpl(eventProcessor);
            TickerV2ConsumerDelegate delegate = new TickerV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMITickerConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "TickerV2ConsumerFactory.create");
            return null;
        }
    }
}
