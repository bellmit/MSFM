package com.cboe.application.supplier;

import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.application.supplier.proxy.RecapV2ConsumerProxy;
import com.cboe.interfaces.domain.session.BaseSessionManager;
/**
 * RecapSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class RecapV2Supplier extends UserSessionMarketDataBaseSupplier
{
    public RecapV2Supplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return RecapV2ConsumerProxy.class.getName();
    }
}
