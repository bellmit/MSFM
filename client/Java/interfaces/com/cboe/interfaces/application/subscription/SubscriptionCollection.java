package com.cboe.interfaces.application.subscription;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

public interface SubscriptionCollection
{
    public void addDefaultSubscriptions(SubscriptionGroup group, Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void removeDefaultSubscriptions(SubscriptionGroup group, Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void removeSubscriptionsByGroup(SubscriptionGroup group);
    public void removeAllSubscriptions();
}
