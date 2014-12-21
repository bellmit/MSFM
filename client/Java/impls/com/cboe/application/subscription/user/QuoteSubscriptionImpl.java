package com.cboe.application.subscription.user;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.events.IECQuoteStatusConsumerV2Home;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class QuoteSubscriptionImpl extends UserSubscriptionImpl
{
    protected IECQuoteStatusConsumerV2Home quoteStatusConsumerHome;

    public QuoteSubscriptionImpl(SessionProfileUserStructV2 userInformation, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(userInformation, subscriptionCollection);
        quoteStatusConsumerHome = ServicesHelper.getQuoteStatusConsumerV2Home();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT, userStruct.userInfo.userId);
        quoteStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT, userStruct.userInfo.userId);
        quoteStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_DELETE_REPORT, userStruct.userInfo.userId);
        quoteStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_STATUS_UPDATE, userStruct.userInfo.userId);
        quoteStatusConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT, userStruct.userInfo.userId);
        quoteStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT, userStruct.userInfo.userId);
        quoteStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_DELETE_REPORT, userStruct.userInfo.userId);
        quoteStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_STATUS_UPDATE, userStruct.userInfo.userId);
        quoteStatusConsumerHome.removeFilter(channelKey);
    }

}
