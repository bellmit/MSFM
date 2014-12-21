package com.cboe.interfaces.application;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.util.channel.ChannelListener;

public interface MarketDataRequestPublisherExt extends com.cboe.interfaces.application.MarketDataRequestPublisher {

    public void removeBookDepthSubscriptionsV2(Object source, Object clientListener);
    public void removeCurrentMarketSubscriptionsV2(Object source, Object clientListener);
    public void removeExpectedOpeningPriceSubscriptionsV2(Object source, Object clientListener);
    public void removeNBBOSubscriptionsV2(Object source, Object clientListener);
    public void removeRecapSubscriptionsV2(Object source, Object clientListener);
    public void removeTickerSubscriptionsV2(Object source, Object clientListener);
    
    public void republishBookDepthSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException; 
    public void republishCurrentMarketSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException; 
    public void republishExpectedOpeningPriceSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException; 
    public void republishNBBOSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException; 
    public void republishRecapSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException; 
    public void republishTickerSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException; 
}
