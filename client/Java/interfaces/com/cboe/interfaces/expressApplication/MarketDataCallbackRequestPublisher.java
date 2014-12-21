//
// -----------------------------------------------------------------------------------
// Source file: MarketDataCallbackRequestPublisher.java
//
// PACKAGE: com.cboe.interfaces.expressApplication
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.expressApplication;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;

public interface MarketDataCallbackRequestPublisher
{
    public void publishCurrentMarketV4Subscription(Object source, String userId, String userIor,
                                                   int classKey, Object clientListener, short actionOnQueue,
                                                   boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishCurrentMarketV4Unsubscription(Object source, String userId, String userIor,
                                                     int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishRecapV4Subscription(Object source, String userId, String userIor,
                                           int classKey, Object clientListener, short actionOnQueue,
                                           boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishRecapV4Unsubscription(Object source, String userId, String userIor,
                                             int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishTickerV4Subscription(Object source, String userId, String userIor,
                                            int classKey, Object clientListener, short actionOnQueue,
                                            boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishTickerV4Unsubscription(Object source, String userId, String userIor,
                                              int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishNBBOV4Subscription(Object source, String userId, String userIor,
            							int classKey, Object clientListener, short actionOnQueue)
    		throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

	public void publishNBBOV4Unsubscription(Object source, String userId, String userIor,
	              						  int classKey, Object clientListener)
			throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishCurrentMarketV4SubscriptionForProduct(Object source, String userId, String userIor,
                                                   int classKey, int productKey, Object clientListener, short actionOnQueue,
                                                   boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishCurrentMarketV4UnsubscriptionForProduct(Object source, String userId, String userIor,
                                                     int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishRecapV4SubscriptionForProduct(Object source, String userId, String userIor,
    									   int classKey,int productKey, Object clientListener, short actionOnQueue,
                                           boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishRecapV4UnsubscriptionForProduct(Object source, String userId, String userIor,
    										int classKey,int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishTickerV4SubscriptionForProduct(Object source, String userId, String userIor,
    										int classKey,int productKey, Object clientListener, short actionOnQueue,
                                            boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishTickerV4UnsubscriptionForProduct(Object source, String userId, String userIor,
    										int classKey,int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

    public void publishNBBOV4SubscriptionForProduct(Object source, String userId, String userIor,
    										int classKey,int productKey, Object clientListener, short actionOnQueue)
    		throws DataValidationException, AuthorizationException, SystemException, CommunicationException;

	public void publishNBBOV4UnsubscriptionForProduct(Object source, String userId, String userIor,
											int classKey,int productKey, Object clientListener)
			throws DataValidationException, AuthorizationException, SystemException, CommunicationException;
	
	public void removeMarketDataRequestSource(Object source);
}