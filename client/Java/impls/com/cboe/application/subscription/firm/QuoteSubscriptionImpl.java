package com.cboe.application.subscription.firm;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.ExchangeFirmStructWrapper;
import com.cboe.interfaces.events.IECQuoteStatusConsumerV2Home;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class QuoteSubscriptionImpl extends FirmSubscriptionImpl
{
    protected IECQuoteStatusConsumerV2Home quoteStatusConsumerHome;

    public QuoteSubscriptionImpl(ExchangeFirmStructWrapper exchangeFirm, SubscriptionAbstractCollection subscriptionCollection)
    {                                                                                            
        super(exchangeFirm, subscriptionCollection);
        quoteStatusConsumerHome = ServicesHelper.getQuoteStatusConsumerV2Home();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, exchangeFirm);
        quoteStatusConsumerHome.addFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, exchangeFirm);
        quoteStatusConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, exchangeFirm);
        quoteStatusConsumerHome.removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, exchangeFirm);
        quoteStatusConsumerHome.removeFilter(channelKey);
    }

}
