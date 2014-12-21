package com.cboe.application.subscription.productType;

import com.cboe.application.subscription.SubscriptionImpl;
import com.cboe.application.subscription.SubscriptionAbstractCollection;

public abstract class ProductTypeSubscriptionImpl extends SubscriptionImpl
{
    protected short productType;
    public ProductTypeSubscriptionImpl(short productType, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(subscriptionCollection);
        this.productType = productType;
        defaultKey = Short.valueOf(productType);
    }
}
