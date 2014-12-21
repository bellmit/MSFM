package com.cboe.interfaces.application;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

/**
 * @author Jing Chen
 */
public interface MarketDataRequestPublisher
{
    public void publishBookDepthSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishBookDepthUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishBookDepthSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishBookDepthUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishCurrentMarketSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishCurrentMarketUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishCurrentMarketSubscriptionV3(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishCurrentMarketUnSubscriptionV3(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishCurrentMarketSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishCurrentMarketUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishNBBOSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishNBBOUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishNBBOSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishNBBOUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishRecapSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishRecapUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishRecapSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishRecapUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishTickerSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishTickerUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishTickerSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishTickerUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishExpectedOpeningPriceSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishExpectedOpeningPriceUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object  clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishExpectedOpeningPriceSubscription(Object source, String userId, String userIor, String sessionName, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishExpectedOpeningPriceUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishLargeTradeLastSaleSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQue)
    		throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void publishLargeTradeLastSaleUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
    		throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
    public void removeMarketDataRequestSource(Object source);
}
