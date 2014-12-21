package com.cboe.application.subscription.firm;

import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.application.subscription.SubscriptionImpl;
import com.cboe.interfaces.domain.ExchangeFirmStructWrapper;

public abstract class FirmSubscriptionImpl extends SubscriptionImpl
{
    ExchangeFirmStructWrapper exchangeFirm;
    public FirmSubscriptionImpl(ExchangeFirmStructWrapper exchangeFirmStructContainer, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(subscriptionCollection);
        exchangeFirm = exchangeFirmStructContainer;
        defaultKey = exchangeFirm;
    }
}
