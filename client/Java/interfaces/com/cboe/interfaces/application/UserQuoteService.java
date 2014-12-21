package com.cboe.interfaces.application;

import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.exceptions.*;

/**
 * @author Jing Chen
 */
public interface UserQuoteService
{
    public void verifyUserQuoteEntryEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserQuoteEntryEnablementForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserQuoteEntryEnablementForSession(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserQuoteStatusEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserQuoteStatusEnablementForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserRFQEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserRFQEnablementForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    int acceptQuote(QuoteStruct quoteStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    int acceptQuoteV7(QuoteStructV4 quoteStruct)
                throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    ClassQuoteResultStructV3[] acceptQuotesForClassV3(int classKey, QuoteStructV3[] quoteStructs)
                throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException, TransactionFailedException;
    ClassQuoteResultStructV3[] acceptQuotesForClassV7(int classKey, QuoteStructV4[] quoteStructs)
                throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException, TransactionFailedException;
    void cancelAllQuotes(String sessionName)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    int cancelQuote(String sessionName, int productKey)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    void cancelQuotesByClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException;
    QuoteDetailStruct getQuote(String sessionName, int productKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException;
    RFQStruct[] getRFQ(String sessionName, int classKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    void publishUnAckedQuotes()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void publishUnAckedQuotesForClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public int cancelQuoteV5(java.lang.String sessionName, int productKey, boolean sendCancelReports)
    	throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.TransactionFailedException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.NotFoundException;
    public void cancelQuotesByClassV5(java.lang.String sessionName, int classKey, boolean sendCancelReports)
		throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.TransactionFailedException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.NotFoundException;
    public void cancelAllQuotesV5(java.lang.String sessionName, boolean sendCancelReports)
		throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.TransactionFailedException;
}
