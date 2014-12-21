package com.cboe.application.subscription.user;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.events.IECOrderStatusConsumerHome;
import com.cboe.interfaces.events.IECOrderStatusConsumerV2Home;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class OrderSubscriptionImpl extends UserSubscriptionImpl
{
    protected IECOrderStatusConsumerV2Home orderStatusConsumerHome;

    public OrderSubscriptionImpl(SessionProfileUserStructV2 userInformation, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(userInformation, subscriptionCollection);
        orderStatusConsumerHome = ServicesHelper.getOrderStatusConsumerV2Home();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.NEW_ORDER, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.CANCEL_REPORT, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_UPDATE, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_QUERY_EXCEPTION, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_STATUS_UPDATE, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS, userStruct.userInfo.userId);
        orderStatusConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.NEW_ORDER, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.CANCEL_REPORT, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_UPDATE, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_QUERY_EXCEPTION, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_STATUS_UPDATE, userStruct.userInfo.userId);
        orderStatusConsumerHome.removeFilter(channelKey);
    }
}
