package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.CurrentMarketV3ConsumerProxy;
import com.cboe.interfaces.domain.session.BaseSessionManager;
/**
 * CurentMarketSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 */

public class CurrentMarketV3Supplier extends UserSessionMarketDataBaseSupplier
{
    public CurrentMarketV3Supplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return CurrentMarketV3ConsumerProxy.class.getName();
    }
}
