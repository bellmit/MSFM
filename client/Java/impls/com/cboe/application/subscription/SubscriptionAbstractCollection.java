package com.cboe.application.subscription;

import com.cboe.interfaces.application.subscription.Subscription;
import com.cboe.interfaces.application.subscription.SubscriptionCollection;
import com.cboe.interfaces.application.subscription.SubscriptionGroup;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.*;
/**
 * @author Jing Chen
 */
public abstract class SubscriptionAbstractCollection implements SubscriptionCollection
{
    protected List subscriptions;
    protected List defaultSubscriptions;
    protected boolean defaultSubscriptionOn;

    public SubscriptionAbstractCollection(boolean defaultSubscriptionOn)
    {
        subscriptions = new ArrayList();
        defaultSubscriptions = new ArrayList();
        this.defaultSubscriptionOn = defaultSubscriptionOn;
    }

    public synchronized void addDefaultSubscriptions(SubscriptionGroup group, Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(defaultSubscriptionOn)
        {
            addDefaultInterest(group, source);
        }
    }

    public synchronized void removeDefaultSubscriptions(SubscriptionGroup group, Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(defaultSubscriptionOn)
        {
            removeDefaultInterest(group, source);
        }
    }

    public synchronized void removeSubscriptionsByGroup(SubscriptionGroup group)
    {
        SubscriptionImpl subscription = null;
        for(Iterator iterator =  subscriptions.iterator();iterator.hasNext();)
        {
            subscription = (SubscriptionImpl)iterator.next();
            try
            {
                subscription.cleanUp(group);
            }
            catch(Exception e)
            {
                Log.exception(e);
            }
        }
    }

    public synchronized void addDefaultSubscription(Subscription subscription)
    {
        defaultSubscriptions.add(subscription);
    }

    public synchronized void addSubscription(Subscription subscription)
    {
        subscriptions.add(subscription);
    }

    public synchronized void removeAllSubscriptions()
    {
        SubscriptionImpl subscription = null;
        for(Iterator iteratorI = subscriptions.iterator(); iteratorI.hasNext();)
        {
            subscription = (SubscriptionImpl)iteratorI.next();
            if(subscription != null)
            {
                try
                {
                    subscription.cleanUp();
                }
                catch(Exception e)
                {
                    Log.exception(e);
                }
            }
        }
        subscriptions.clear();
        defaultSubscriptions.clear();
    }

    protected synchronized void addDefaultInterest(SubscriptionGroup group, Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Subscription subscription = null;
        Iterator iterator = defaultSubscriptions.iterator();
        while(iterator.hasNext())
        {
            subscription = (Subscription)iterator.next();
            subscription.subscribe(group, source);
        }
        group.addSubscriptionCollection(this);
    }

    protected synchronized void removeDefaultInterest(SubscriptionGroup group, Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Subscription subscription = null;
        Iterator iterator = defaultSubscriptions.iterator();
        while(iterator.hasNext())
        {
            subscription = (Subscription)iterator.next();
            subscription.unsubscribe(group, source);
        }
        group.removeSubscriptionCollection(this);
    }
}
