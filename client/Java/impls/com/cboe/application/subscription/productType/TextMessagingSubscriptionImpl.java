package com.cboe.application.subscription.productType;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.application.subscription.Subscription;
import com.cboe.interfaces.events.IECTextMessageConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class TextMessagingSubscriptionImpl extends ProductTypeSubscriptionImpl implements Subscription
{
    protected IECTextMessageConsumerHome textMessageConsumerHome;

    public TextMessagingSubscriptionImpl(short productType, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(productType, subscriptionCollection);
        textMessageConsumerHome = ServicesHelper.getTextMessageConsumerHome();
    }
    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_TYPE, Short.valueOf(productType));
        textMessageConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_TYPE, Short.valueOf(productType));
        textMessageConsumerHome.removeFilter(channelKey);
    }
}