package com.cboe.application.subscription;

import com.cboe.interfaces.application.subscription.SubscriptionGroup;
import com.cboe.interfaces.application.subscription.SubscriptionCollection;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class SubscriptionGroupImpl implements SubscriptionGroup
{
    Map subscriptionCollections;
    public SubscriptionGroupImpl()
    {
        subscriptionCollections = Collections.synchronizedMap(new HashMap());
    }
    public void addSubscriptionCollection(SubscriptionCollection subscriptionCollection)
    {
        subscriptionCollections.put(subscriptionCollection, subscriptionCollection);
    }
    public void removeSubscriptionCollection(SubscriptionCollection subscriptionCollection)
    {
        subscriptionCollections.remove(subscriptionCollection);
    }
    public void cleanUp()
    {
        SubscriptionCollection subCollection = null;
        for(Iterator i=subscriptionCollections.values().iterator();i.hasNext();)
        {
            subCollection = (SubscriptionCollection)i.next();
            try
            {
                subCollection.removeSubscriptionsByGroup(this);
            }
            catch(Exception e)
            {
                Log.exception(e);
            }
        }
    }
}
