//
// ------------------------------------------------------------------------
// FILE: LockedQuoteStatusV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.LockedQuoteStatusV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.LockedQuoteStatusV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class LockedQuoteStatusV2ConsumerFactory
{
    public LockedQuoteStatusV2ConsumerFactory()
    {
        super();
    }

    public static CMILockedQuoteStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            LockedQuoteStatusV2Consumer consumer = new LockedQuoteStatusV2ConsumerImpl(eventProcessor);
            LockedQuoteStatusV2ConsumerDelegate delegate = new LockedQuoteStatusV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMILockedQuoteStatusConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "LockedQuoteStatusV2ConsumerFactory.create");
            return null;
        }
    }
}
