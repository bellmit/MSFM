//
// ------------------------------------------------------------------------
// FILE: RFQV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer;
import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.AuctionConsumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.AuctionConsumerDelegate;
import org.omg.CORBA.Object;

public class AuctionConsumerFactory
{
    public AuctionConsumerFactory()
    {
        super();
    }

    public static CMIAuctionConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            AuctionConsumer consumer = new AuctionConsumerImpl(eventProcessor);
            AuctionConsumerDelegate delegate = new AuctionConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMIAuctionConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "AuctionConsumerFactory.create");
            return null;
        }
    }
}
