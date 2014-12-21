package com.cboe.interfaces.application.subscription;

import com.cboe.interfaces.domain.session.BaseSessionManager;

public interface SubscriptionServiceHome
{
    public final static String HOME_NAME = "SubscriptionServiceHome";
    public SubscriptionService create(BaseSessionManager sessionManager)
            throws Exception;
    public SubscriptionService find(BaseSessionManager sessionManager)
            throws Exception;
    public void remove(BaseSessionManager sessionManager);
}
