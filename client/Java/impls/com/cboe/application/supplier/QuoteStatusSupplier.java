package com.cboe.application.supplier;

import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.supplier.proxy.QuoteStatusConsumerProxy;
/**
 * QuoteStatusSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Derek T. Chambers-Boucher
 * @version 04/20/1999
 */

public class QuoteStatusSupplier extends UserSessionBaseSupplier
{
    public QuoteStatusSupplier(BaseSessionManager session)
    {
        super(session);
    }

    public String getListenerClassName()
    {
        return QuoteStatusConsumerProxy.class.getName();
    }
}
