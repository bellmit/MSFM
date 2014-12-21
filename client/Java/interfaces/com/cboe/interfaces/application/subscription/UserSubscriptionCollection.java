package com.cboe.interfaces.application.subscription;

public interface UserSubscriptionCollection extends SubscriptionCollection
{
    public Subscription getOrderSubscription();
    public Subscription getQuoteSubscription();
    public Subscription getQuoteLockSubscription();
    public Subscription getTextMessagingSubscription();
    public Subscription getAuctionSubscription();
    public Subscription getPropertySubscription();
    public Subscription getUserTimeoutWarningSubscription();
}
