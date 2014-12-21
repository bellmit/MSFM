//
// ------------------------------------------------------------------------
// FILE: OrderBookUpdateV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer;
import com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.OrderBookUpdateV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.OrderBookUpdateV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class OrderBookUpdateV2ConsumerFactory
{
    public OrderBookUpdateV2ConsumerFactory()
    {
        super();
    }

    public static CMIOrderBookUpdateConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            OrderBookUpdateV2Consumer consumer = new OrderBookUpdateV2ConsumerImpl(eventProcessor);
            OrderBookUpdateV2ConsumerDelegate delegate = new OrderBookUpdateV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMIOrderBookUpdateConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "OrderBookUpdateV2ConsumerFactory.create");
            return null;
        }
    }
}
