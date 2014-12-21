package com.cboe.interfaces.application.subscription;

public interface FirmSubscriptionCollection extends SubscriptionCollection
{
    Subscription getOrderSubscription();
    Subscription getQuoteSubscription();
}
