package com.cboe.application.subscription.sessionClass;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.events.IECCurrentMarketConsumerHome;
import com.cboe.interfaces.events.IECMarketBufferConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class CurrentMarketSubscriptionImpl extends SessionClassSubscriptionImpl
{
    protected IECCurrentMarketConsumerHome currentMarketConsumerHome;
    protected IECMarketBufferConsumerHome marketBufferConsumerHome;

    public CurrentMarketSubscriptionImpl( SessionKeyWrapper sessionClass, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(sessionClass, subscriptionCollection);
        currentMarketConsumerHome = ServicesHelper.getCurrentMarketConsumerHome();
        try
        {
            marketBufferConsumerHome = ServicesHelper.getMarketBufferConsumerHome();
        }
        catch (Exception e)
        {
            // ServicesHelper already logged an exception, no need to do it here.
            // We're running in a client that does not have a MarketBufferConsumerHome
            // so just set our variable to null.
            marketBufferConsumerHome = null;
        }
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        currentMarketConsumerHome.addFilter(channelKey);
        channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        currentMarketConsumerHome.addFilter(channelKey);
        channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS_SEQ, Integer.valueOf(sessionClass.getKey()));
        currentMarketConsumerHome.addFilter(channelKey);
        if (marketBufferConsumerHome != null)
        {
            marketBufferConsumerHome.addClassKeyFilter(sessionClass.getKey());
        }
        setSubscriptionFlag(true);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        currentMarketConsumerHome.removeFilter(channelKey);
        channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        currentMarketConsumerHome.removeFilter(channelKey);
        channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS_SEQ, Integer.valueOf(sessionClass.getKey()));
        currentMarketConsumerHome.removeFilter(channelKey);
        if (marketBufferConsumerHome != null)
        {
            marketBufferConsumerHome.removeClassKeyFilter(sessionClass.getKey());
        }
        setSubscriptionFlag(false);
    }
}
