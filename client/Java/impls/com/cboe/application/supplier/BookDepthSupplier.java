package com.cboe.application.supplier;

import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.application.supplier.proxy.BookDepthConsumerProxy;
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

public class BookDepthSupplier extends UserSessionMarketDataBaseSupplier
{
    public BookDepthSupplier(BaseSessionManager sessionManager)
    {
        super(sessionManager);
    }

    public String getListenerClassName()
    {
        return BookDepthConsumerProxy.class.getName();
    }
}
