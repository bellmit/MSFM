// -----------------------------------------------------------------------------------
// Source file: AuctionAPI
//
// PACKAGE: com.cboe.interfaces.presentation.api
// 
// Created: Sep 9, 2004 3:22:09 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.interfaces.presentation.auction.Auction;
import com.cboe.util.event.EventChannelListener;
import com.cboe.exceptions.*;

public interface AuctionAPI
{
    /**
       Gets the Auction's for the given class and session.
       @param sessionName
       @param classKey
       @return Auction[]
     */
    public Auction[] getAuctionForClass(String sessionName, int classKey, short[] types, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
       Gets the Auction's for the given product and session.
       @param sessionName
       @param productKey
       @return Auction[]
     */
    public Auction[] getAuctionForProduct(String sessionName, int productKey, short[] types, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public Auction[] getCachedAuctionForSession(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
       Subscribes an EventChannelListener to receive events for the given session and class. This method returns all of the current Auctions for the given criteria.
       @param sessionName
       @param classKey
     */
    public void subscribeAuctionForClass(String sessionName, int classKey, short[] auctionType, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
       Unsubscribes an EventChannelListener for the given session and class.
       @param sessionName
       @param classKey
     */
    public void unsubscribeAuctionForClass(String sessionName, int classKey, short[] auctionType, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public void registerAuctionListener(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unregisterAuctionListener(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


}
