package com.cboe.application.subscription.sessionClass;

import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.application.subscription.SubscriptionImpl;
import com.cboe.interfaces.domain.SessionKeyWrapper;

public abstract class SessionClassSubscriptionImpl extends SubscriptionImpl
{
    protected SessionKeyWrapper sessionClass;

    public SessionClassSubscriptionImpl(SessionKeyWrapper sessionClass, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(subscriptionCollection);
        this.sessionClass = sessionClass;
        defaultKey = sessionClass;
    }
}
