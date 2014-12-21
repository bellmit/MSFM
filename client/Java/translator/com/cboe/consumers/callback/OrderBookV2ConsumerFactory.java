//
// ------------------------------------------------------------------------
// FILE: OrderBookV2ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer;
import com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.OrderBookV2Consumer;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.callback.OrderBookV2ConsumerDelegate;
import org.omg.CORBA.Object;

public class OrderBookV2ConsumerFactory
{
    public OrderBookV2ConsumerFactory()
    {
        super();
    }

    public static CMIOrderBookConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            OrderBookV2Consumer consumer = new OrderBookV2ConsumerImpl(eventProcessor);
            OrderBookV2ConsumerDelegate delegate = new OrderBookV2ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate);
            return CMIOrderBookConsumerHelper.narrow(corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "OrderBookV2ConsumerFactory.create");
            return null;
        }
    }
}
