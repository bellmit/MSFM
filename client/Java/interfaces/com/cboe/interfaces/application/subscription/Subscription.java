package com.cboe.interfaces.application.subscription;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

import java.util.Map;

public interface Subscription
{
    public void subscribe(SubscriptionGroup group, Object listener, Object key)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribe(SubscriptionGroup group, Object listener, Object key)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void subscribe(SubscriptionGroup group, Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribe(SubscriptionGroup group, Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public boolean isDefaultSubscription();
    public void setDefaultSubscriptionFlag(boolean defaultSubFlag);
    public void setSkipSubscriptionCheckingFlag(boolean skipSubscriptionCheck);
    public boolean channelSubscribed();
    public boolean containsDefaultKeySubscriptionForListener(SubscriptionGroup group, Object listener);
    public boolean containsSubscriptionsForListener(SubscriptionGroup group, Object listener);
}
