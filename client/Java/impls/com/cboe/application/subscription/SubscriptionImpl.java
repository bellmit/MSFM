package com.cboe.application.subscription;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.application.subscription.Subscription;
import com.cboe.interfaces.application.subscription.SubscriptionGroup;


public abstract class SubscriptionImpl implements Subscription
{
    protected SubscriptionAbstractCollection subscriptionCollection;
    protected SubscriptionCounter subscriptionCounter;
    protected boolean subscribed;
    protected boolean defaultSub;
    protected boolean skipSubscriptionCheck;
    protected Object defaultKey;

    public SubscriptionImpl(SubscriptionAbstractCollection subscriptionCollection)
    {
        this.subscriptionCollection = subscriptionCollection;
        subscriptionCollection.addSubscription(this);
        subscriptionCounter = new SubscriptionCounter(this);
        subscribed = false;
        defaultSub = false;
    }

    public boolean isDefaultSubscription()
    {
        return defaultSub;
    }

    public void setUpSubscription()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscribeChannel();
        subscribed = true;
    }

    public void cleanUpSubscription()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        unsubscribeChannel();
        subscribed = false;
    }

    public void subscribe(SubscriptionGroup group, Object listener, Object key)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(subscriptionCounter.addSubscription(group, listener, key, skipSubscriptionCheck))
        {
            if(!defaultSub)
            {
                subscriptionCollection.addDefaultInterest(group, this);
            }
        }
    }

    public void subscribe(SubscriptionGroup group, Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscribe(group, listener, defaultKey);
    }

    public void unsubscribe(SubscriptionGroup group, Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(subscriptionCounter.removeSubscription(group, listener))
        {
            if(!defaultSub)
            {
                subscriptionCollection.removeDefaultInterest(group, this);
            }
        }
    }

    public boolean containsDefaultKeySubscriptionForListener(SubscriptionGroup group,Object listener)
    {
        return subscriptionCounter.containsSubscriptionForListenerByKey(group,listener,defaultKey);
    }

    public boolean containsSubscriptionsForListener(SubscriptionGroup group, Object listener)
    {
        return subscriptionCounter.containsSubscriptionsForListener(group, listener);
    }

    public void setDefaultSubscriptionFlag(boolean defaultSub)
    {
        this.defaultSub = defaultSub;
        subscriptionCollection.addDefaultSubscription(this);
    }

    protected void setSubscriptionFlag(boolean sub)
    {
        subscribed = sub;
    }

    public void setSkipSubscriptionCheckingFlag(boolean skipSubscriptionCheck)
    {
        this.skipSubscriptionCheck = skipSubscriptionCheck;
    }

    public void unsubscribe(SubscriptionGroup group, Object listener, Object key)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(subscriptionCounter.removeSubscription(group, listener, key))
        {
            if(!defaultSub)
            {
                subscriptionCollection.removeDefaultInterest(group, this);
            }
        }
    }

    public void cleanUp(SubscriptionGroup group)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCounter.removeSubscriptions(group);
    }

    public void cleanUp()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCounter.cleanUp();
    }

    public boolean channelSubscribed()
    {
        return subscribed;
    }

    public String toString()
    {
        return subscriptionCounter.toString();
    }

    public abstract void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public abstract void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
