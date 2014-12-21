package com.cboe.application.subscription;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.subscription.SubscriptionCollectionServiceHome;
import com.cboe.interfaces.application.subscription.SubscriptionCollectionService;

public class SubscriptionCollectionServiceHomeImpl extends ClientBOHome implements SubscriptionCollectionServiceHome
{
    SubscriptionCollectionService subscriptionCollectionService;
    public SubscriptionCollectionService create()
    {
        if(subscriptionCollectionService == null)
        {
            subscriptionCollectionService = new SubscriptionCollectionServiceImpl();
        }
        return subscriptionCollectionService;
    }
    public SubscriptionCollectionService find()
    {
        return create();
    }
}
