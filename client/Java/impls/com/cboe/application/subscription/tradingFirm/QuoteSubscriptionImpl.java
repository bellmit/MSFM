package com.cboe.application.subscription.tradingFirm;

import com.cboe.interfaces.events.IECQuoteStatusConsumerV2Home;
import com.cboe.interfaces.domain.TradingFirmGroupWrapper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

/**
 * Author: mahoney
 * Date: Apr 22, 2008
 */
public class QuoteSubscriptionImpl extends TradingFirmSubscriptionImpl
{
    protected IECQuoteStatusConsumerV2Home quoteStatusConsumerHome;

    public QuoteSubscriptionImpl(TradingFirmGroupWrapper firm, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(firm, subscriptionCollection);
        quoteStatusConsumerHome = ServicesHelper.getQuoteStatusConsumerV2Home();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM, tradingFirm);
        quoteStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM, tradingFirm);
        quoteStatusConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM, tradingFirm);
        quoteStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM, tradingFirm);
        quoteStatusConsumerHome.removeFilter(channelKey);
    }
}
