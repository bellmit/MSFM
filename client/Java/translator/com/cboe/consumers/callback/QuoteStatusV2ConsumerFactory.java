//
// ------------------------------------------------------------------------
// FILE: QuoteStatusConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callbackV2
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer;
import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.QuoteStatusV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.QuoteStatusV2ConsumerDelegate;

/**
 * @author torresl@cboe.com
 */
public class QuoteStatusV2ConsumerFactory
{
    public QuoteStatusV2ConsumerFactory()
    {
        super();
    }

    public static CMIQuoteStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            QuoteStatusV2Consumer quoteStatusConsumer = new QuoteStatusV2ConsumerImpl(eventProcessor);
            QuoteStatusV2ConsumerDelegate delegate = new QuoteStatusV2ConsumerDelegate(quoteStatusConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMIQuoteStatusConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "V2QuoteStatusConsumerFactory.create");
            return null;
        }
    }

}
