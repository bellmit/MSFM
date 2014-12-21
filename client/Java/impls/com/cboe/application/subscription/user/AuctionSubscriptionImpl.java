package com.cboe.application.subscription.user;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.events.IECAuctionConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;


public class AuctionSubscriptionImpl extends UserSubscriptionImpl
{
    private IECAuctionConsumerHome auctionConsumerHome;
    public AuctionSubscriptionImpl(SessionProfileUserStructV2 userStruct, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(userStruct, subscriptionCollection);
        auctionConsumerHome = ServicesHelper.getAuctionConsumerHome();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	
        ChannelKey channelKey = new ChannelKey(ChannelType.AUCTION_USER, Integer.valueOf(userStruct.userKey));
        auctionConsumerHome.addFilter(channelKey);
 
        channelKey = new ChannelKey(ChannelType.DAIM_USER, Integer.valueOf(userStruct.userKey));
        auctionConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.AUCTION_USER, Integer.valueOf(userStruct.userKey));
        auctionConsumerHome.removeFilter(channelKey);
        
        channelKey = new ChannelKey(ChannelType.DAIM_USER, Integer.valueOf(userStruct.userKey));
        auctionConsumerHome.removeFilter(channelKey);
    }
}