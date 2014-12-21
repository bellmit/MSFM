package com.cboe.consumers.callback;

/**
 * Provides access to the singleton SubscriptionManager object for reference
 * counting of callback consumers
 */
public class SubscriptionManagerFactory
{
    private static SubscriptionManagerImpl instance;

    public SubscriptionManagerFactory()
    {
        super();
    }

    public static SubscriptionManagerImpl create()
    {
        if (instance == null)
        {
            instance = new SubscriptionManagerImpl();
        }

        return instance;
    }

    public static SubscriptionManagerImpl find()
    {
        return create();
    }
}
