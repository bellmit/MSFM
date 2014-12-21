package com.cboe.application.subscription.sessionClass;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.events.IECTextMessageConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class TextMessagingSubscriptionImpl extends SessionClassSubscriptionImpl
{
    protected IECTextMessageConsumerHome textMessageConsumerHome;

    public TextMessagingSubscriptionImpl(SessionKeyWrapper sessionClass, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(sessionClass, subscriptionCollection);
        textMessageConsumerHome = ServicesHelper.getTextMessageConsumerHome();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        textMessageConsumerHome.addFilter(channelKey);
        setSubscriptionFlag(true);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        textMessageConsumerHome.removeFilter(channelKey);
        setSubscriptionFlag(false);
    }
}
