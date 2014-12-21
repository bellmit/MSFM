//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingProcessorFactory.java
//
// PACKAGE: com.cboe.application.shared.consumer
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.OrderRoutingCollector;

@SuppressWarnings({"UtilityClassWithoutPrivateConstructor"})
public class OrderRoutingProcessorFactory
{
    public static OrderRoutingProcessor create(OrderRoutingCollector parent)
    {
        OrderRoutingProcessor processor = new OrderRoutingProcessor();
        processor.setParent(parent);
        return processor;
    }
}
