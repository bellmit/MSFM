package com.cboe.application.subscription;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.subscription.SubscriptionGroup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SubscriptionCounter
{
    Map listenersByGroup;
    SubscriptionImpl channelSubscription;

    public SubscriptionCounter(SubscriptionImpl channelSubscription)
    {
        listenersByGroup = new HashMap();
        this.channelSubscription = channelSubscription;
    }

    private Map getMap(Map keyTable, Object key)
    {
        Map lookupHash = (Map) keyTable.get(key);
        if (lookupHash == null)
        {
            lookupHash = new HashMap();
            keyTable.put(key, lookupHash);
        }
        return lookupHash;
    }

    public synchronized boolean addSubscription(SubscriptionGroup group, Object listener, Object key, boolean skipSubscriptionCheck)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        boolean firstSubByGroup = false;
        if(skipSubscriptionCheck)
        {
            channelSubscription.setUpSubscription();
        }
        else
        {
            if (listenersByGroup.isEmpty())
            {
                channelSubscription.setUpSubscription();
            }
        }
        Map listeners = getMap(listenersByGroup, group);
        if(listeners.isEmpty())
        {
            firstSubByGroup = true;
        }
        Map keys = getMap(listeners, listener);
        keys.put(key, key);
        return firstSubByGroup;
    }

    public synchronized boolean containsSubscriptionForListenerByKey(SubscriptionGroup group, Object listener, Object key)
    {
        boolean included = false;
        if(listenersByGroup.containsKey(group))
        {
            Map listeners = getMap(listenersByGroup, group);
            if(listeners.containsKey(listener))
            {
                Map keys = getMap(listeners, listener);
                if(keys.containsKey(key))
                {
                    included = true;
                }
            }
        }
        return included;
    }

    public synchronized boolean containsSubscriptionsForListener(SubscriptionGroup group, Object listener)
    {
        boolean included = false;
        if(listenersByGroup.containsKey(group))
        {
            Map listeners = getMap(listenersByGroup, group);
            if(listeners.containsKey(listener))
            {
                included = true;
            }
        }
        return included;
    }

    public synchronized boolean removeSubscription(SubscriptionGroup group, Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        boolean noListenersByGroup = false;
        Map listeners = getMap(listenersByGroup, group);
        listeners.remove(listener);
        if (listeners.isEmpty())
        {
            Object o = listenersByGroup.remove(group);
            noListenersByGroup = true;
            if (o!=null && listenersByGroup.isEmpty())
            {
                channelSubscription.cleanUpSubscription();
            }
        }
        return noListenersByGroup;
    }

    public synchronized boolean removeSubscription(SubscriptionGroup group, Object listener, Object key)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        boolean noListenersByGroup = false;
        Map listeners = getMap(listenersByGroup, group);
        Map keys = getMap(listeners, listener);
        keys.remove(key);
        if(keys.isEmpty())
        {
            listeners.remove(listener);
            if (listeners.isEmpty())
            {
                Object o = listenersByGroup.remove(group);
                noListenersByGroup = true;
                if (o!=null && listenersByGroup.isEmpty())
                {
                    channelSubscription.cleanUpSubscription();
                }
            }
        }
        return noListenersByGroup;
    }

    public synchronized void removeSubscriptions(SubscriptionGroup group)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Object o = listenersByGroup.remove(group);
        if(o!=null && listenersByGroup.isEmpty())
        {
            channelSubscription.cleanUpSubscription();
        }
    }

    public synchronized SubscriptionGroup[] cleanUp()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SubscriptionGroup[] groups = new SubscriptionGroup[listenersByGroup.keySet().size()];
        groups = (SubscriptionGroup[])listenersByGroup.keySet().toArray(groups);
        try
        {
            channelSubscription.cleanUpSubscription();
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
        channelSubscription = null;
        listenersByGroup.clear();
        return groups;
    }

    public String toString()
    {
        StringBuilder stringBuffer = new StringBuilder();
        SubscriptionGroup group = null;
        Map listeners = null;
        Object listener = null;
        stringBuffer.append("\n");
        synchronized(this)
        {
            for(Iterator i = listenersByGroup.keySet().iterator(); i.hasNext();)
            {
                group = (SubscriptionGroup)i.next();
                stringBuffer.append(group.toString());
                listeners = getMap(listenersByGroup, group);
                stringBuffer.append(" listener size:").append(listeners.size());
                stringBuffer.append("\n");
                for(Iterator j = listeners.values().iterator(); j.hasNext();)
                {
                    listener = j.next();
                    stringBuffer.append("listener:").append(listener.getClass()).append(listener.hashCode());
                    stringBuffer.append("\n");
                }
            }
        }
        return stringBuffer.toString();
    }
}
