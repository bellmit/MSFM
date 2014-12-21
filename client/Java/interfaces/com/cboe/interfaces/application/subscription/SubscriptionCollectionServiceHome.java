package com.cboe.interfaces.application.subscription;

public interface SubscriptionCollectionServiceHome
{
    public final static String HOME_NAME = "SubscriptionCollectionServiceHome";
    public SubscriptionCollectionService create();
    public SubscriptionCollectionService find();
}
