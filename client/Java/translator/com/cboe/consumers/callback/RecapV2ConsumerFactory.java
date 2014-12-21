//
// ------------------------------------------------------------------------
// FILE: RecapV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIRecapConsumer;
import com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.RecapV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.RecapV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class RecapV2ConsumerFactory
{
    public RecapV2ConsumerFactory()
    {
        super();
    }

    public static CMIRecapConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            RecapV2Consumer consumer = new RecapV2ConsumerImpl(eventProcessor);
            RecapV2ConsumerDelegate delegate = new RecapV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMIRecapConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "RecapV2ConsumerFactory.create");
            return null;
        }
    }
}
