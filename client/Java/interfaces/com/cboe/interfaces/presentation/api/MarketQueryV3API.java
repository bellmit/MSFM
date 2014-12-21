
//
// ------------------------------------------------------------------------
// FILE: MarketQueryV3API.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.api;

import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.util.event.EventChannelListener;

/**
 *
 * MarketQueryV3API is a subscription type communication to be notified when a event occurs on the
 * registered data.
 * The subscription is sent to the MDCAS (Market Data CAS) via the CAS.
 * The MDCAS will send back to the client via the CAS all events that occurred.
 *
 */

public interface MarketQueryV3API extends MarketQueryV2API
{
	void subscribeCurrentMarketForClassV3(String session, int classKey,  EventChannelListener clientListener)
			throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

	void subscribeCurrentMarketForProductV3(String session, int productKey,  EventChannelListener clientListener)
			throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

	void unsubscribeCurrentMarketForClassV3(String session, int classKey,  EventChannelListener clientListener)
			throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

	void unsubscribeCurrentMarketForProductV3(String session, int productKey,  EventChannelListener clientListener)
			throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Retrieves the detail market data history by time for the given product.
     *
     * @param productKey the product key to retrieve for.
     * @param startTime starting time for the history to receive data.
     * @return none.
     * @exception NotFoundException
     *
     */
    public MarketDataHistoryDetailStruct getDetailMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Retrieves the priority market data history by time for the given product.
     *
     * @param productKey the product key to retrieve for.
     * @param startTime starting time for the history to receive data.
     * @return none.
     * @exception NotFoundException
     *
     */
    public MarketDataHistoryDetailStruct getPriorityMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    
}

