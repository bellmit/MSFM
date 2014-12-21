package com.cboe.application.subscription.user;

import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.application.subscription.Subscription;
import com.cboe.interfaces.application.subscription.SubscriptionGroup;
import com.cboe.interfaces.application.subscription.UserSubscriptionCollection;

public class UserSubscriptionCollectionImpl extends SubscriptionAbstractCollection implements UserSubscriptionCollection
{
    protected Subscription orderSubscription;
    protected Subscription quoteSubscription;
    protected Subscription quoteLockNotificationSubscription;
    protected Subscription textMessagingSubscription;
    protected Subscription userTimeoutWarningSubscription;
    protected Subscription propertyUpdateSubscription;
    protected Subscription auctionSubscription;
    protected SessionProfileUserStructV2 userStruct;

    public UserSubscriptionCollectionImpl(SessionProfileUserStructV2 userStruct, boolean defaultSubscriptionOn)
    {
        super(defaultSubscriptionOn);
        this.userStruct = userStruct;
        orderSubscription = new OrderSubscriptionImpl(userStruct, this);
        quoteSubscription = new QuoteSubscriptionImpl(userStruct, this);
        quoteLockNotificationSubscription = new QuoteLockNotificationSubscriptionImpl(userStruct, this);
        propertyUpdateSubscription = new PropertyUpdateSubscriptionImpl(userStruct, this);
        textMessagingSubscription = new TextMessagingUserSubscriptionImpl(userStruct, this);
        userTimeoutWarningSubscription = new UserTimeoutWarningSubscriptionImpl(userStruct, this);
        auctionSubscription = new AuctionSubscriptionImpl(userStruct, this);
        if(defaultSubscriptionOn)
        {
            orderSubscription.setDefaultSubscriptionFlag(true);
            quoteSubscription.setDefaultSubscriptionFlag(true);
            propertyUpdateSubscription.setDefaultSubscriptionFlag(true);
            textMessagingSubscription.setDefaultSubscriptionFlag(true);
            userTimeoutWarningSubscription.setDefaultSubscriptionFlag(true);
        }
//This override is a hack to solve FE and CAS order/quote status subscription count out of synch problem
//which usually happens when there is a processWatcher disconnect and a RTServer buffer overflow to CAS
//        orderSubscription.setSkipSubscriptionCheckingFlag(true);
//        quoteSubscription.setSkipSubscriptionCheckingFlag(true);
    }

    public Subscription getOrderSubscription()
    {
        return orderSubscription;
    }

    public Subscription getPropertySubscription()
    {
        return propertyUpdateSubscription;
    }

    public Subscription getQuoteSubscription()
    {
        return quoteSubscription;
    }

    public Subscription getAuctionSubscription()
    {
        return auctionSubscription;
    }

    public Subscription getTextMessagingSubscription()
    {
         return textMessagingSubscription;
    }

    public Subscription getQuoteLockSubscription()
    {
        return quoteLockNotificationSubscription;
    }

    public Subscription getUserTimeoutWarningSubscription()
    {
        return userTimeoutWarningSubscription;
    }

}
