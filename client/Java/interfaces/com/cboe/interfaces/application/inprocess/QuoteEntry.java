package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteStructV4;
import com.cboe.exceptions.*;
import com.cboe.exceptions.AuthorizationException;

/**
 * @author Jing Chen
 */
public interface QuoteEntry
{
    public void acceptQuote(QuoteStructV4 quoteDetail)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;
    public ClassQuoteResultStructV3[] acceptQuotesForClass(int classKey, QuoteStructV4[] quoteStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;
    public void cancelQuote(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException;
    public void cancelQuotesByClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException;
    public void cancelAllQuotes(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException;
}
