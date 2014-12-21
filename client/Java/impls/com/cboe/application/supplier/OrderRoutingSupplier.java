//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingSupplier.java
//
// PACKAGE: com.cboe.application.supplier
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

import com.cboe.application.supplier.proxy.OrderRoutingConsumerProxy;

/**
 * OrderRoutingSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 * It is important to note that the channel key can be any hashable object.
 */
public class OrderRoutingSupplier extends UserSessionBaseSupplier
{
    public OrderRoutingSupplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return OrderRoutingConsumerProxy.class.getName();
    }
}
