package com.cboe.application.subscription.user;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.events.IECQuoteNotificationConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class QuoteLockNotificationSubscriptionImpl extends UserSubscriptionImpl
{
    protected IECQuoteNotificationConsumerHome quoteNotificationConsumerHome;

    public QuoteLockNotificationSubscriptionImpl(SessionProfileUserStructV2 userStruct, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(userStruct, subscriptionCollection);
        quoteNotificationConsumerHome = ServicesHelper.getQuoteNotificationConsumerHome();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_LOCKED_NOTIFICATION, Integer.valueOf(userStruct.userKey));
        quoteNotificationConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_LOCKED_NOTIFICATION, Integer.valueOf(userStruct.userKey));
        quoteNotificationConsumerHome.removeFilter(channelKey);
    }
}
