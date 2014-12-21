//
// -----------------------------------------------------------------------------------
// Source file: MarketQueryV4API.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiMarketData.NBBOStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.util.event.EventChannelListener;

/**
 *
 * MarketQueryV4API is subscription type communication to be notified when a particular event
 * occurs on the registered data.
 *
 * The subscription is done to the MDX (Market Data Express) via the CAS.
 * All notifications will be sent from the MDX to the client directly without passing by the CAS.
 *
 */

public interface MarketQueryV4API
{
    public boolean isV4ToV3MDConversionEnabled();

    public boolean isMDXSupportedSession(String session);

    public void subscribeCurrentMarketV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeCurrentMarketV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeRecapLastSaleV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeRecapLastSaleV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeTickerV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeTickerV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeNBBOForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeNBBOForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Get the NBBOStruct for a product key for a session name.
     * @param sessionName to subscribe to.
     * @param productKey to query.
     * @return the NBBOStruct for a product key.
     * @throws UserException sent by server.
     */
    public NBBOStruct getNbboSnapshotForProduct(String sessionName, int productKey) throws UserException;

    /**
     * Get the NBBOStruct for a product key for a session name within a period of subscription time.
     * @param timeout to limit the subscribtion time in millisecond.
     * @param sessionName to subscribe to.
     * @param productKey to query.
     * @return the NBBOStruct for a product key.
     * @throws UserException sent by server.
     */
    public NBBOStruct getNbboSnapshotForProduct(int timeout, String sessionName, int productKey)
            throws UserException;
}
