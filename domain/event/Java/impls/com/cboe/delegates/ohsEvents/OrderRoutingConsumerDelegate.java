//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingConsumerDelegate.java
//
// PACKAGE: com.cboe.delegates.ohsEvents
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.delegates.ohsEvents;

import com.cboe.idl.ohsConsumers.POA_OrderRoutingConsumer_tie;

import com.cboe.interfaces.ohsEvents.OrderRoutingConsumer;

public class OrderRoutingConsumerDelegate extends POA_OrderRoutingConsumer_tie
{
    public OrderRoutingConsumerDelegate(OrderRoutingConsumer delegate)
    {
        super(delegate);
    }
}
