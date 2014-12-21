package com.cboe.application.subscription.user;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.events.IECTextMessageConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class TextMessagingUserSubscriptionImpl extends UserSubscriptionImpl
{
    protected IECTextMessageConsumerHome textMessageConsumerHome;

    public TextMessagingUserSubscriptionImpl(SessionProfileUserStructV2 userStruct, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(userStruct, subscriptionCollection);
        textMessageConsumerHome = ServicesHelper.getTextMessageConsumerHome();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_USER, userStruct.userInfo.userId);
        textMessageConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_USER, userStruct.userInfo.userId);
        textMessageConsumerHome.removeFilter(channelKey);
    }
}
