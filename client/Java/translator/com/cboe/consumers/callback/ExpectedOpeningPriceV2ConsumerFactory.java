//
// ------------------------------------------------------------------------
// FILE: ExpectedOpeningPriceV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer;
import com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.ExpectedOpeningPriceV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.ExpectedOpeningPriceV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class ExpectedOpeningPriceV2ConsumerFactory
{
    public ExpectedOpeningPriceV2ConsumerFactory()
    {
        super();
    }

    public static CMIExpectedOpeningPriceConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            ExpectedOpeningPriceV2Consumer consumer = new ExpectedOpeningPriceV2ConsumerImpl(eventProcessor);
            ExpectedOpeningPriceV2ConsumerDelegate delegate = new ExpectedOpeningPriceV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMIExpectedOpeningPriceConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "ExpectedOpeningPriceV2ConsumerFactory.create");
            return null;
        }
    }
}
