package com.cboe.interfaces.application.subscription;

public interface SessionClassSubscriptionCollection extends SubscriptionCollection
{
    public Subscription getCurrentMarketSubscription();
    public Subscription getRecapSubscription();
    public Subscription getTickerSubscription();
    public Subscription getBookDepthSubscription();
    public Subscription getExpectedOpeningPriceSubscription();
    public Subscription getAuctionSubscription();
    public Subscription getRFQSubscription();
    public Subscription getTextMessagingSubscription();
    public Subscription getLargeTradeLastSaleSubscription(); 
}
