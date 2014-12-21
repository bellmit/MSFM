package com.cboe.application.subscription.sessionClass;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.events.IECAuctionConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class AuctionSubscriptionImpl extends SessionClassSubscriptionImpl
{
    private IECAuctionConsumerHome auctionConsumerHome;
    public AuctionSubscriptionImpl(SessionKeyWrapper sessionClass, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(sessionClass, subscriptionCollection);
        auctionConsumerHome = ServicesHelper.getAuctionConsumerHome();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.AUCTION, Integer.valueOf(sessionClass.getKey()));
        auctionConsumerHome.addFilter(channelKey);
        setSubscriptionFlag(true);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
       /* Heavy-handed fix for SEDL #SYS005548, better would be to find out
           why this method is being called at inappropriate times.
        ChannelKey channelKey = new ChannelKey(ChannelType.AUCTION, Integer.valueOf(sessionClass.getKey()));
        auctionConsumerHome.removeFilter(channelKey);
        */
        setSubscriptionFlag(false);
    }
}