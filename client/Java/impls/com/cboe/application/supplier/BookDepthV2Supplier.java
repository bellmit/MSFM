package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.BookDepthV2ConsumerProxy;
import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.interfaces.domain.session.BaseSessionManager;
/**
 * CurentMarketSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author William Wei
 * @version 06/28/1999
 */

public class BookDepthV2Supplier extends UserSessionMarketDataBaseSupplier
{
    public BookDepthV2Supplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return BookDepthV2ConsumerProxy.class.getName();
    }
}
