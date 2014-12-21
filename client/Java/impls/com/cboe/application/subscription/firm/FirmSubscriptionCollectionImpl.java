package com.cboe.application.subscription.firm;

import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.application.subscription.SubscriptionImpl;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.application.subscription.FirmSubscriptionCollection;
import com.cboe.interfaces.application.subscription.Subscription;
import com.cboe.interfaces.application.subscription.SubscriptionGroup;
import com.cboe.interfaces.domain.ExchangeFirmStructWrapper;

public class FirmSubscriptionCollectionImpl extends SubscriptionAbstractCollection implements FirmSubscriptionCollection
{
    ExchangeFirmStructWrapper exchangeFirm;
    Subscription orderSubscription;
    Subscription quoteSubscription;
    public FirmSubscriptionCollectionImpl(ExchangeFirmStructWrapper exchangeFirm, boolean defaultSubscriptionOn)
    {
        super(defaultSubscriptionOn);
        this.exchangeFirm = exchangeFirm;
        orderSubscription = new OrderSubscriptionImpl(exchangeFirm, this);
        quoteSubscription = new QuoteSubscriptionImpl(exchangeFirm, this);
        if(defaultSubscriptionOn)
        {
            orderSubscription.setDefaultSubscriptionFlag(true);
            quoteSubscription.setDefaultSubscriptionFlag(true);
        }
    }

    public Subscription getOrderSubscription()
    {
        return orderSubscription;
    }

    public Subscription getQuoteSubscription()
    {
        return quoteSubscription;
    }

}
