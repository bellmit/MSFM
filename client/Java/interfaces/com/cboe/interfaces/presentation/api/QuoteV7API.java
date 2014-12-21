package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteEntryStructV4;

public interface QuoteV7API extends QuoteV3API
{
	ClassQuoteResultStructV3[] acceptQuotesForClassV7(int classKey,QuoteEntryStructV4[] quotes)
                throws SystemException,
                	   CommunicationException,
                	   AuthorizationException,
                	   DataValidationException,
                	   NotAcceptedException,
                	   TransactionFailedException;
                
	void acceptQuoteV7(QuoteEntryStructV4 quote)
                throws SystemException,
                       CommunicationException,
                       AuthorizationException,
                       DataValidationException,
                       NotAcceptedException,
                       TransactionFailedException;

}
