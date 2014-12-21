//
// -----------------------------------------------------------------------------------
// Source file: RFQAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.util.event.EventChannelListener;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.presentation.rfq.RFQ;

/**
   RFQCache defines the interface prototype for managing cached RFQ's
   @author Will McNabb
 */
public interface RFQAPI {

    /**
       Gets the RFQ's for the given class and session.
       @param String sessionName
       @param int classKey
       @return RFQ[]
       @roseuid 3A8401C60178
     */
    public RFQ[] getRFQsForClass(String sessionName, int classKey, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
       Gets the RFQ's for the given product and session.
       @param String sessionName
       @param int productKey
       @return RFQ[]
       @roseuid 3A8401E500BE
     */
    public RFQ getRFQsForProduct(String sessionName, int productKey, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public RFQ[] getCachedRFQsForSession(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void registerGenericRFQListener(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unregisterGenericRFQListener(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
       Subscribes an EventChannelListener to receive events for the given session and class. This method returns all of the current RFQs for the given criteria.
       @param String sessionName
       @param int classKey
       @return RFQ[]
       @roseuid 3A8946F8009F
     */
    public void subscribeRFQForClass(String sessionName, int classKey, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
       Unsubscribes an EventChannelListener for the given session and class.
       @param String sessionName
       @param int classKey
       @roseuid 3A895BEC0168
     */
    public void unsubscribeRFQForClass(String sessionName, int classKey, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
