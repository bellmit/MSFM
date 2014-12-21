package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.supplier.proxy.SessionAdminConsumerProxy;
/**
 * UserSessionAdminSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Keith A. Korecky
 * @version 07/07/1999
 */

public class UserSessionAdminSupplier extends UserSessionBaseSupplier
{
    public UserSessionAdminSupplier(BaseSessionManager session)
    {
        super(session);
    }

    public String getListenerClassName()
    {
        return SessionAdminConsumerProxy.class.getName();
    }
}
