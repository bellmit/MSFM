package com.cboe.application.supplier;

import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.application.supplier.proxy.ExpectedOpeningPriceConsumerProxy;
import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * ExpectedOpeningPriceSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class ExpectedOpeningPriceSupplier extends UserSessionMarketDataBaseSupplier
{
    public ExpectedOpeningPriceSupplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return ExpectedOpeningPriceConsumerProxy.class.getName();
    }
}
