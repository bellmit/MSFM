package com.cboe.application.subscription.firm;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.ExchangeFirmStructWrapper;
import com.cboe.interfaces.events.IECOrderStatusConsumerHome;
import com.cboe.interfaces.events.IECOrderStatusConsumerV2Home;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class OrderSubscriptionImpl extends FirmSubscriptionImpl
{
    protected IECOrderStatusConsumerV2Home orderStatusConsumerHome;

    public OrderSubscriptionImpl(ExchangeFirmStructWrapper exchangeFirm, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(exchangeFirm, subscriptionCollection);
        orderStatusConsumerHome = ServicesHelper.getOrderStatusConsumerV2Home();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
          ChannelKey channelKey;
          channelKey = new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM, exchangeFirm);
          orderStatusConsumerHome.addFilter(channelKey);

          channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, exchangeFirm);
          orderStatusConsumerHome.addFilter(channelKey);

          channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, exchangeFirm);
          orderStatusConsumerHome.addFilter(channelKey);

          channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_FIRM, exchangeFirm);
          orderStatusConsumerHome.addFilter(channelKey);

          channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, exchangeFirm);
          orderStatusConsumerHome.addFilter(channelKey);

          channelKey = new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM, exchangeFirm);
          orderStatusConsumerHome.addFilter(channelKey);

          channelKey = new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM, exchangeFirm);
          orderStatusConsumerHome.addFilter(channelKey);

          channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM, exchangeFirm);
          orderStatusConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM, exchangeFirm);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, exchangeFirm);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, exchangeFirm);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_FIRM, exchangeFirm);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, exchangeFirm);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM, exchangeFirm);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM, exchangeFirm);
        orderStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM, exchangeFirm);
        orderStatusConsumerHome.removeFilter(channelKey);
    }

}
