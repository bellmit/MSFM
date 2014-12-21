package com.cboe.application.supplier;

import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.supplier.proxy.OrderStatusConsumerProxy;
/**
 * OrderStatusSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Derek T. Chambers-Boucher
 * @version 04/20/1999
 */

public class OrderStatusSupplier extends UserSessionBaseSupplier
{
    public OrderStatusSupplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return OrderStatusConsumerProxy.class.getName();
    }
}
