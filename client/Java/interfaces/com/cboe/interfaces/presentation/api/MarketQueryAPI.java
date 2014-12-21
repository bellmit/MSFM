//
// -----------------------------------------------------------------------------------
// Source file: MarketQueryAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.NotAcceptedException;

import com.cboe.idl.cmiMarketData.MarketDataHistoryStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.util.event.EventChannelListener;

import com.cboe.interfaces.presentation.marketData.PersonalBestBook;
import com.cboe.interfaces.presentation.marketData.UserMarketDataStruct;
import com.cboe.interfaces.presentation.marketData.StrategyImpliedMarketWrapper;
import com.cboe.interfaces.presentation.bookDepth.BookDepth;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.domain.CurrentMarketProductContainer;
import org.omg.CORBA.UserException;

/**
 * This interface represents the Market Query application API to the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/04/1999
 */
public interface MarketQueryAPI
{
    /**
     * Subscribes the given client listener for market Recap
     * data for the given class.
     *
     * @param classKey the class key to retrieve market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public void subscribeRecapForClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the given client listener for market Recap
     * data for the given product.
     *
     * @param productKey the product key to retrieve market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void subscribeRecapForProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the given client listener for current market
     * data for the given product.
     *
     * @param productKey the product key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void subscribeCurrentMarketForProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the given client listener for current market
     * data for the given class.
     *
     * @param classKey the class key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void subscribeCurrentMarketForClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the given client listener for NBBO
     * data for the given class.
     *
     * @param classKey the class key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void subscribeNBBOForClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the given client listener for NBBO
     * data for the given product key.
     *
     * @param classKey the class key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void subscribeNBBOForProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the given client listener for NBBO
     * data for the given class.
     *
     * @param classKey the class key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void unsubscribeNBBOForClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the given client listener for NBBO
     * data for the given product key.
     *
     * @param classKey the class key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void unsubscribeNBBOForProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


    /**
     * Retrieves the book depth for the given product.
     * @param sessionProduct to retrieve Book Depth for.
     * @return BookDepth
     */
    public BookDepth getCmiBookDepth(SessionProduct sessionProduct)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, NotFoundException;

    /**
     * Subscribes for top of book depth for the given product.
     * @param sessionProduct to subscribe for
     * @param clientListener to subscribe as listener
     */
    public void subscribeBookDepth(SessionProduct sessionProduct, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Subscribes for book depth updates for the given product.
     * @param sessionProduct to subscribe for
     * @param clientListener to subscribe as listener
     */
    public void subscribeBookDepthUpdate(SessionProduct sessionProduct,  EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Subscribes the given client listener for market
     * ticker for the given product.
     *
     * @param productKey the product key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public void subscribeTicker(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the given client listener for current market
     * data for the given product.
     *
     * @param productKey the product key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void unsubscribeCurrentMarketForProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes for top of book depth for the given product.
     * @param sessionProduct to unsubscribe for
     * @param clientListener to unsubscribe as listener
     */
    public void unsubscribeBookDepth(SessionProduct sessionProduct, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Unsubscribes for book depth updates for the given product.
     * @param sessionProduct to unsubscribe for
     * @param clientListener to unsubscribe as listener
     */
    public void unsubscribeBookDepthUpdate(SessionProduct sessionProduct,  EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Unsubscribes the given client listener for current market
     * data for the given class.
     *
     * @param classKey the class key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void unsubscribeCurrentMarketForClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsbscribes the given client listener for market Recap
     * data for the given product.
     *
     * @param productKey the product key to retrieve market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception UnknownProductException
     *
     * @author Connie Feng
     */
    public void unsubscribeRecapForProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the given client listener for market Recap
     * data for the given class.
     *
     * @param classKey the class key to retrieve market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public void unsubscribeRecapForClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the given client listener for market
     * ticker for the given product.
     *
     * @param productKey the product key to retrieve current market data for.
     * @param clientListener the client listener to receive continued the market
     *        data.
     * @return none.
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public void unsubscribeTicker(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Retrieves the market data history by time for the given product.
     *
     * @param productKey the product key to retrieve for.
     * @param startTime starting time for the history to receive data.
     * @return none.
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public MarketDataHistoryStruct getMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Subscribes the given client listener for expected opening price
     * data for the given class.
     *
     * @param classKey the class key to retrieve for.
     * @param clientListener the client listener to receive data.
     * @return none.
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public void subscribeExpectedOpeningPrice(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the given client listener for expected opening price
     * data for the given class.
     *
     * @param classKey the class key to retrieve for.
     * @param clientListener the client listener to receive data.
     * @return none.
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public void subscribeExpectedOpeningPriceByProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Unsubscribes the given client listener for expected opening price
     * data for the given class.
     *
     * @param classKey the class key to retrieve for.
     * @param clientListener the client listener to receive data.
     * @return none.
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public void unsubscribeExpectedOpeningPrice(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * getPersonalBestBook gets the traders personal best book for the given
     * product key and subscribes the client listener to receive continued
     * personal best book updates for that product.
     *
     * @return the personal best book struct for the given product key.
     * @param productKey the product key to get personal best book data for.
     * @param clientListener the client listener to subscribe for continued data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PersonalBestBook getPersonalBestBookByProduct( String sessionName, int productKey,EventChannelListener clientListener )
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Unsubscribes the client listener from receiving personal best book info
     * for the given product key.
     *
     * @param productKey the product key to unsubscribe personal best book data from.
     * @param clientListener the client listener to unsubscribe from receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribePersonalBestBookByProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * getPersonalBestBook gets the traders personal best book for the given
     * product key and subscribes the client listener to receive continued
     * personal best book updates for that product.
     *
     * @return the personal best book struct for the given product key.
     * @param productKey the product key to get personal best book data for.
     * @param clientListener the client listener to subscribe for continued data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PersonalBestBook[] getPersonalBestBookByClass( String sessionName, int classKey,EventChannelListener clientListener )
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    /**
     * Unsubscribes the client listener from receiving personal best book info
     * for the given product key.
     *
     * @param productKey the product key to unsubscribe personal best book data from.
     * @param clientListener the client listener to unsubscribe from receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribePersonalBestBookByClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


    /**
     * getUserMarketData gets the user market for the given
     * class key and subscribes the client listener to receive continued
     * User Market Data updates for that class.
     *
     * @return the array of User Market Data structs for the given class key.
     * @param classKey the product key to get personal best book data for.
     * @param clientListener the client listener to subscribe for continued data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public UserMarketDataStruct[] getUserMarketData( String sessionName, int classKey, EventChannelListener clientListener )
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * getUserMarketDataByProduct gets the user market for the given
     * product key and subscribes the client listener to receive continued
     * User Market Data updates for that product.
     *
     * @return the array of User Market Data structs for the given product key.
     * @param productKey the product key to get personal best book data for.
     * @param clientListener the client listener to subscribe for continued data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public UserMarketDataStruct getUserMarketDataByProduct( String sessionName, int productKey, EventChannelListener clientListener )
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Unsubscribes the client listener from receiving User Market Data info
     * for the given product key.
     *
     * @param productKey the product key to unsubscribe for
     * @param clientListener the client listener to unsubscribe from receiving data
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    void unsubscribeUserMarketDataByProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Unsubscribes the client listener from receiving User Market Data info
     * for the given class key.
     *
     * @param classKey the product key to unsubscribe personal best book data from.
     * @param clientListener the client listener to unsubscribe from receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeUserMarketData(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Get the most recent CurrentMarket for a session product
     */
    public CurrentMarketProductContainer getCurrentMarketSnapshotForProduct(int timeout, String sessionName, int productKey)
            throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    /**
     * Get the most recent CurrentMarket for a session product
     */
    public CurrentMarketProductContainer getCurrentMarketSnapshotForProduct(String sessionName, int productKey)
        throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Get the most recent recap for the underlying product for a SessionProductClass
     */
    public RecapStruct getUnderlyingRecapSnapshotForClass(int timeout, SessionProductClass productClass)
        throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Get the most recent recap for the underlying product for a SessionProductClass
     */
    public RecapStruct getUnderlyingRecapSnapshotForClass(SessionProductClass productClass)
            throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Get the most recent recap for the underlying product for a SessionReportingClass
     */
    public RecapStruct getUnderlyingRecapSnapshotForReportingClass(int timeout, SessionReportingClass reportingClass)
        throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Get the most recent recap for the underlying product for a SessionReportingClass
     */
    public RecapStruct getUnderlyingRecapSnapshotForReportingClass(SessionReportingClass reportingClass)
            throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Calculates the implied same/opposite for a SessionStrategy.  If any of the legs are for an
     * Equity or Index product (e.g., the Strategy is a Buy/Write), this will return null.
     */
    public StrategyImpliedMarketWrapper getStrategyImpliedMarket(SessionStrategy strategyProduct)
            throws UserException;
}

