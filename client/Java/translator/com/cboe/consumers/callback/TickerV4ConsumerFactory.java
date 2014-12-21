//
// ------------------------------------------------------------------------
// FILE: TickerV4ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.TickerV4Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.TickerV4ConsumerDelegate;
import org.omg.CORBA.Object;

public class TickerV4ConsumerFactory extends AbstractV4ConsumerFactory
{
    public TickerV4ConsumerFactory()
    {
        super();
    }

    public static CMITickerConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            TickerV4Consumer consumer = new TickerV4ConsumerImpl(eventProcessor);
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
