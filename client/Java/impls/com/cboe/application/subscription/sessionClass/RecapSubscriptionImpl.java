package com.cboe.application.subscription.sessionClass;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.events.IECRecapConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class RecapSubscriptionImpl extends SessionClassSubscriptionImpl
{
    protected IECRecapConsumerHome recapConsumerHome;

    public RecapSubscriptionImpl(SessionKeyWrapper sessionClass, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(sessionClass, subscriptionCollection);
        recapConsumerHome = ServicesHelper.getRecapConsumerHome();
    }
    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        recapConsumerHome.addFilter(channelKey);
        channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS_SEQ, Integer.valueOf(sessionClass.getKey()));
        recapConsumerHome.addFilter(channelKey);
        setSubscriptionFlag(true);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, Integer.valueOf(sessionClass.getKey()));
        recapConsumerHome.removeFilter(channelKey);
        channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS_SEQ, Integer.valueOf(sessionClass.getKey()));
        recapConsumerHome.removeFilter(channelKey);
        setSubscriptionFlag(false);
    }
}
