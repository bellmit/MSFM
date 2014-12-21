package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.supplier.proxy.OrderStatusV2ConsumerProxy;

/**
 * OrderStatusSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Tom Trop
 */
public class OrderStatusV2Supplier extends UserSessionBaseSupplier
{
    public OrderStatusV2Supplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return OrderStatusV2ConsumerProxy.class.getName();
    }
}
