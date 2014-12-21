//
// ------------------------------------------------------------------------
// FILE: RFQV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIRFQConsumer;
import com.cboe.idl.cmiCallbackV2.CMIRFQConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.RFQV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.RFQV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class RFQV2ConsumerFactory
{
    public RFQV2ConsumerFactory()
    {
        super();
    }

    public static CMIRFQConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            RFQV2Consumer consumer = new RFQV2ConsumerImpl(eventProcessor);
            RFQV2ConsumerDelegate delegate = new RFQV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMIRFQConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "RFQV2ConsumerFactory.create");
            return null;
        }
    }
}
