package com.cboe.application.subscription.sessionClass;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.events.IECRFQConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class RFQSubscriptionImpl extends SessionClassSubscriptionImpl
{
    private IECRFQConsumerHome rfqConsumerHome;
    public RFQSubscriptionImpl(SessionKeyWrapper sessionClass, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(sessionClass, subscriptionCollection);
        rfqConsumerHome = ServicesHelper.getRFQConsumerHome();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.RFQ, sessionClass);
        rfqConsumerHome.addFilter(channelKey);
        setSubscriptionFlag(true);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.RFQ, sessionClass);
        rfqConsumerHome.removeFilter(channelKey);
        setSubscriptionFlag(false);
    }
}
