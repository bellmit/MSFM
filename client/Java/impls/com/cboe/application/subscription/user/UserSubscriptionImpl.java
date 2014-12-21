package com.cboe.application.subscription.user;

import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.application.subscription.SubscriptionImpl;
import com.cboe.idl.user.SessionProfileUserStructV2;

public abstract class UserSubscriptionImpl extends SubscriptionImpl
{
    protected SessionProfileUserStructV2 userStruct;

    public UserSubscriptionImpl(SessionProfileUserStructV2 userStruct, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(subscriptionCollection);
        this.userStruct = userStruct;
        defaultKey = userStruct;
    }

    public String toString()
    {
        StringBuilder stringBuffer = new StringBuilder(100);
        stringBuffer.append(this.getClass()).append(userStruct.toString());
        stringBuffer.append(super.toString());
        return stringBuffer.toString();
    }
}
