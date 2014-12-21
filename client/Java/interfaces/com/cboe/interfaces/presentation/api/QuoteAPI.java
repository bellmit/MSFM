package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;

import com.cboe.util.event.EventChannelListener;

import com.cboe.interfaces.presentation.rfq.RFQ;
import com.cboe.interfaces.presentation.product.SessionProductClass;


/**
 * This interface represents the MarketMaker application API into the CAS,
 * It extends the TraderAPI with the addition of the Quote interface.
 *
 * @author Derek T. Chambers-Boucher
 * @version 10/12/1999
 */

public interface QuoteAPI extends RFQAPI
{
    public SessionProductClass[] getAllQuotedClasses();

    public SessionProductClass[] getAllQuotedClassesForSession(String sessionName);

    /**
     * Initializes callback listener to CAS
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void initializeQuoteCallbackListener()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * acceptQuote accepts quotes for products from a marketmaker.
     *
     * @param quote the quote entry struct to submit for acceptance.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedExceptio
     * */
    public void acceptQuote(QuoteEntryStruct quote)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

    /**
     * accepts the quote entry for class.
     *
     * @param classKey class key
     * @param quotes quote entries to be accepted
     * @return ClassQuoteResultStruct sequence
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public ClassQuoteResultStruct[] acceptQuotesForClass(int classKey, QuoteEntryStruct[] quotes)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

    /**
     * getQuote gets all quotes for the given product key and subscribes the
     * client listener to receive continued quote updates for that product.
     *
     * @return a quote detail struct containing the quote for the given product.
     * @param productKey the product key to retrieve the quote for.
     * @param clientListener the client listener to receive continued quote data
     *        for the given product.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public QuoteDetailStruct getQuote(String sessionName, int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * getQuote gets all quotes for the given product key and subscribes the
     * client listener to receive continued quote updates for that product.
     *
     * @return a quote detail struct containing the quote for the given product.
     * @param productKey the product key to retrieve the quote for.
     * @param clientListener the client listener to receive continued quote data
     *        for the given product.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public QuoteDetailStruct[] getQuotesForProduct(int productKey, EventChannelListener consumer)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * getAllQuotes gets all of a marketmakers quotes and subscribes the client
     * listener for continued receipt of quote data.
     *
     * @return a sequence of quote detail structs containing the marketmakers
     *         current quotes.
     * @param clientListener the client listener to receive continued quote data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated
     */
    public void subscribeQuoteStatus(EventChannelListener clientListener)
           throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    /**
     * cancelQuote cancels a marketmakers quotes for the given product.
     *
     * @param productKey the product key to cancel quotes for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception TransactionFailedException
     * @exception NotAcceptedException
     * @exception NotFoundException
     */
    public void cancelQuote(String sessionName, int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException;

    /**
     * cancelAllQuotes cancels all of a marketmakers quotes.
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public void cancelAllQuotes(String sessionName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException;

    /**
     * cancelQuotesByClass cancels a marketmakers quotes for all products that
     * belong to each of the given classes.
     *
     * @param classKeys the sequence of class keys to cancel quotes for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception TransactionFailedException
     * @exception NotAcceptedException
     * @exception NotFoundException
     */
    public void cancelQuotesByClass(String sessionName, int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException;

    /**
     * Unsubscribes the listener for quote status information for the given classes.
     *
     * @param classKeys the array of product classes to unsubscribe for.
     * @param clientListener the unsubscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated
     */
    public void unsubscribeQuoteStatusByClass(int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * 
     * @param productKey
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @deprecated
     */ 
    public void unsubscribeQuoteStatusByProduct(int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes listening for the given product classes by the given listener.
     *
     * @param classKeys an array of all interested class keys.
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */

// Covered by a different interface now
/*
    public void subscribeRFQ(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
*/

    /**
     * Unsubscribes listening for the given product classes by the given listener.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param classKeys an array of all interested class keys.
     * @param clientListener the unsubscribing listener.
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */

// Covered by a different interface now
/*
    public void unsubscribeRFQ(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
*/

    /**
     * Subscribes the listener for quote filled report information for all of
     * the users filled quotes.
     *
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated
     */
    public void subscribeQuoteFilledReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the listener for quote filled report information for all of
     * the users filled quotes.
     *
     * @param clientListener the unsubscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated
     */
    public void unsubscribeQuoteFilledReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the listener for quote filled report information for all of
     * the user's firm's filled quotes.
     *
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void subscribeQuoteFilledReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the listener for quote filled report information for all of
     * the current user's firm's filled quotes.
     *
     * @param clientListener the unsubscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void unsubscribeQuoteFilledReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribe the client listener from receiving quote information.
     *
     * @param clientListener the unsubscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated
     */
    public void unsubscribeAllQuoteStatus(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

/*
     * Gets the quote history for the given class.
     *
     * @author Connie Feng
     *
     * @return quotes activity history.
     * @param classKey the class key for which to get quote activities
     * @param startTime the start time to query for
     * @param directions the directions to query for
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException

     public ActivityHistoryStruct[] getQuoteActivity(int classKey, DateTimeStruct startTime, short direction)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
*/

    /**
     * getAllQuotes gets all of a marketmakers quotes and subscribes the client
     * listener for continued receipt of quote data.
     *
     * @return a sequence of quote detail structs containing the marketmakers
     *         current quotes.
     * @param clientListener the client listener to receive continued quote data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public QuoteDetailStruct[] getAllQuotes(EventChannelListener clientListener)
           throws SystemException, CommunicationException, DataValidationException, AuthorizationException;

    public QuoteFilledReportStruct[] getQuoteFilledReports(EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public QuoteDetailStruct[] getQuotesByClass(int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the client listener to receive quote
     * bust report information.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to receive continued order bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated
     */
    public void subscribeQuoteBustReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    /**
     * Subscribes the client listener to receive order
     * bust report information for firm.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to receive continued quote bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void subscribeQuoteBustReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener to receive order
     * bust and reinstate report information.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to unsubscribe continued quote bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated
     */
    public void unsubscribeQuoteBustReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    /**
     * Unsubscribes the client listener to receive order
     * bust and reinstate report information for firm.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to unsubscribe continued quote bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void unsubscribeQuoteBustReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public QuoteBustReportStruct[] getQuoteBustReports(EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
