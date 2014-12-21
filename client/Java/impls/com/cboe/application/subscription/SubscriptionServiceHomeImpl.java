package com.cboe.application.subscription;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.subscription.SubscriptionServiceHome;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.domain.session.BaseSessionManager;

import java.util.Map;
import java.util.HashMap;

public class SubscriptionServiceHomeImpl extends ClientBOHome implements SubscriptionServiceHome
{
    Map subscriptionServices;
    public SubscriptionServiceHomeImpl()
    {
        subscriptionServices = new HashMap();
    }

    public synchronized SubscriptionService create(BaseSessionManager sessionManager)
        throws Exception
    {
        SubscriptionServiceImpl subscriptionService = (SubscriptionServiceImpl)subscriptionServices.get(sessionManager);
        if(subscriptionService == null)
        {
            subscriptionService = new SubscriptionServiceImpl(sessionManager);
            subscriptionServices.put(sessionManager, subscriptionService);
        }
        return subscriptionService;
    }
    public SubscriptionService find(BaseSessionManager sessionManager)
        throws Exception
    {
        return create(sessionManager);
    }
    public synchronized void remove(BaseSessionManager sessionManager)
    {
        SubscriptionServiceImpl subscriptionService = (SubscriptionServiceImpl)subscriptionServices.get(sessionManager);
        if(subscriptionService != null)
        {
            subscriptionService.cleanUp();
            subscriptionServices.remove(sessionManager);
        }
    }
}
