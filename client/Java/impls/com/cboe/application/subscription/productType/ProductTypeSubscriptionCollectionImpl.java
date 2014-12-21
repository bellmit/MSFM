package com.cboe.application.subscription.productType;

import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.interfaces.application.subscription.ProductTypeSubscriptionCollection;
import com.cboe.interfaces.application.subscription.Subscription;


public class ProductTypeSubscriptionCollectionImpl extends SubscriptionAbstractCollection implements ProductTypeSubscriptionCollection
{
    protected short productType;
    protected Subscription textMessagingSubscription;
    public ProductTypeSubscriptionCollectionImpl(short productType, boolean defaultSubscriptionOn)
    {
        super(defaultSubscriptionOn);
        this.productType = productType;
        textMessagingSubscription = new TextMessagingSubscriptionImpl(productType, this);
        if(defaultSubscriptionOn)
        {
            textMessagingSubscription.setDefaultSubscriptionFlag(true);
        }
    }

    public Subscription getTextMessagingSubscription()
    {
        return textMessagingSubscription;
    }
}
