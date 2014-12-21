//
// ------------------------------------------------------------------------
// FILE: NBBOV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMINBBOConsumer;
import com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.NBBOV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.NBBOV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class NBBOV2ConsumerFactory
{
    public NBBOV2ConsumerFactory()
    {
        super();
    }

    public static CMINBBOConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            NBBOV2Consumer consumer = new NBBOV2ConsumerImpl(eventProcessor);
            NBBOV2ConsumerDelegate delegate = new NBBOV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMINBBOConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "NBBOV2ConsumerFactory.create");
            return null;
        }
    }
}
