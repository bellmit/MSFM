package com.cboe.application.subscription.sessionClass;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.events.IECBookDepthConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class BookDepthSubscriptionImpl extends SessionClassSubscriptionImpl
{
    protected IECBookDepthConsumerHome bookDepthConsumerHome;

    public BookDepthSubscriptionImpl(SessionKeyWrapper sessionClass, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(sessionClass, subscriptionCollection);
        bookDepthConsumerHome = ServicesHelper.getBookDepthConsumerHome();
    }
    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        bookDepthConsumerHome.addFilter(channelKey);
        channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS_SEQ, Integer.valueOf(sessionClass.getKey()));
        bookDepthConsumerHome.addFilter(channelKey);
        setSubscriptionFlag(true);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        bookDepthConsumerHome.removeFilter(channelKey);
        channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS_SEQ, Integer.valueOf(sessionClass.getKey()));
        bookDepthConsumerHome.removeFilter(channelKey);
        setSubscriptionFlag(false);
    }
}
