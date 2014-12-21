package com.cboe.application.supplier;

import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.application.supplier.proxy.NBBOConsumerProxy;
import com.cboe.interfaces.domain.session.BaseSessionManager;
/**
 * CurentMarketSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Derek T. Chambers-Boucher
 * @author Jimmy Wang
 * @version 06/07/2000
 */

public class NBBOSupplier extends UserSessionMarketDataBaseSupplier
{
    public NBBOSupplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return NBBOConsumerProxy.class.getName();
    }
}
