package com.cboe.application.quote.common;

import com.cboe.idl.cmiQuote.*;

import com.cboe.idl.cmiConstants.QuoteUpdateControlValues;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import org.omg.CORBA.UserException;


/**
 * User: huange
 * Date: Apr 30, 2004
 * Gijo on 2/12/9: Created a shallow-clone version of few methods from domain.util.QuoteStructBuilder.	
 */
public class QuoteStructBuilder {

    BaseSessionManager                    sessionManager;
    protected String                      thisUserId;

    public QuoteStructBuilder(BaseSessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
        try
        {
            thisUserId = sessionManager.getUserId();
        }
        catch(UserException e)
        {
            Log.alarm("QuoteStructBuilder-> fatal error in getting the userId");
        }
    }

    public QuoteStruct buildQuoteStruct(QuoteEntryStruct theQuoteEntry)
           throws SystemException,CommunicationException, AuthorizationException, DataValidationException
    {
        QuoteStruct theStruct = new QuoteStruct();
        theStruct.productKey = theQuoteEntry.productKey;
        theStruct.userId = sessionManager.getUserId();
        theStruct.askPrice = theQuoteEntry.askPrice;
        theStruct.askQuantity = theQuoteEntry.askQuantity;
        theStruct.bidPrice = theQuoteEntry.bidPrice;
        theStruct.bidQuantity = theQuoteEntry.bidQuantity;
        theStruct.userAssignedId = theQuoteEntry.userAssignedId;
        theStruct.sessionName = theQuoteEntry.sessionName;
        return theStruct;
    }

    public QuoteStructV3 buildQuoteStructV3(QuoteEntryStructV3 theQuoteEntry)
           throws SystemException,CommunicationException, AuthorizationException, DataValidationException
    {
        QuoteStruct theStruct = buildQuoteStruct(theQuoteEntry.quoteEntry);
        QuoteStructV3 theStructV3 = new QuoteStructV3(theStruct, theQuoteEntry.quoteUpdateControlId);
        return theStructV3;
    }

    public QuoteStructV4 buildQuoteStructV4(QuoteEntryStructV4 theQuoteEntry)
           throws SystemException,CommunicationException, AuthorizationException, DataValidationException
    {
        QuoteStructV3 theStruct = buildQuoteStructV3(theQuoteEntry.quoteEntryV3);
        QuoteStructV4 theStructV4 = new QuoteStructV4(theStruct, theQuoteEntry.sellShortIndicator, theQuoteEntry.extensions);
        return theStructV4;
    }

    public QuoteStruct[] buildQuoteStructs(QuoteEntryStruct[] quotes)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int size = quotes.length;
        QuoteStruct[] quoteStructs = new QuoteStruct[size];
        for(int i=0; i<size; i++)
        {
           quoteStructs[i] = buildQuoteStruct(quotes[i]);
        }
        return quoteStructs;
    }

    public QuoteStructV3[] buildQuoteStructsV3(QuoteEntryStructV3[] quotes)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int size = quotes.length;
        QuoteStructV3[] quoteStructsV3 = new QuoteStructV3[size];
        for(int i=0; i<size; i++)
        {
           quoteStructsV3[i] = buildQuoteStructV3(quotes[i]);
        }
        return quoteStructsV3;
    }

    public QuoteStructV4[] buildQuoteStructsV4(QuoteEntryStructV4[] quotes)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int size = quotes.length;
        QuoteStructV4[] quoteStructsV4 = new QuoteStructV4[size];
        for(int i=0; i<size; i++)
        {
           quoteStructsV4[i] = buildQuoteStructV4(quotes[i]);
        }
        return quoteStructsV4;
    }

    public QuoteStructV3[] buildQuoteStructsV3(QuoteEntryStruct quote)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        QuoteEntryStructV3 quoteV3 = new QuoteEntryStructV3(quote, QuoteUpdateControlValues.CONTROL_DISABLED);
        QuoteStructV3[] quoteStructsV3 = new QuoteStructV3[1];
        quoteStructsV3[0]=(buildQuoteStructV3(quoteV3));
        return quoteStructsV3;
    }

    public QuoteStructV4[] buildQuoteStructsV4(QuoteEntryStructV4 quote)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        QuoteStructV4[] quoteStructsV4 = new QuoteStructV4[1];
        quoteStructsV4[0]=(buildQuoteStructV4(quote));
        return quoteStructsV4;
    }

    public QuoteStructV3[] buildQuoteStructsV3(QuoteEntryStruct[] quotes)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int size = quotes.length;
        QuoteStructV3[] quoteStructsV3 = new QuoteStructV3[size];
        for(int i=0; i<size; i++)
        {
           quoteStructsV3[i] = new QuoteStructV3(buildQuoteStruct(quotes[i]),
                                                 QuoteUpdateControlValues.CONTROL_DISABLED );
        }
        return quoteStructsV3;
    }

    public ClassQuoteResultStruct[] buildClassQuoteResultsV1(ClassQuoteResultStructV3[] resultsV3)
    {
        int size = resultsV3.length;
        ClassQuoteResultStruct[] results = new ClassQuoteResultStruct[size];
        for(int i=0; i<size; i++)
        {
           results[i] = new ClassQuoteResultStruct(resultsV3[i].quoteResult.productKey,
                                                   resultsV3[i].quoteResult.errorCode );
        }
        return results;
    }

    public ClassQuoteResultStructV2[] buildClassQuoteResultsV2(ClassQuoteResultStructV3[] resultsV3)
    {
        int size = resultsV3.length;
        ClassQuoteResultStructV2[] results = new ClassQuoteResultStructV2[size];
        for(int i=0; i<size; i++)
        {
           results[i] = resultsV3[i].quoteResult;
        }
        return results;
    }
    
    public static QuoteStruct cloneQuoteStruct(QuoteStruct quote)
    {
        QuoteStruct cloned = null;

        if (quote != null )
        {
            cloned                           = new QuoteStruct();
            cloned.bidPrice                  = quote.bidPrice;
            cloned.bidQuantity               = quote.bidQuantity;
            cloned.askPrice                  = quote.askPrice;
            cloned.askQuantity               = quote.askQuantity;
            cloned.userId                    = quote.userId;
            cloned.productKey                = quote.productKey;
            cloned.quoteKey                  = quote.quoteKey;
            cloned.transactionSequenceNumber = quote.transactionSequenceNumber;
            cloned.userAssignedId = quote.userAssignedId;
            cloned.sessionName = quote.sessionName;
        }
        return cloned;
    }
    
    public static QuoteDetailStruct cloneQuoteDetailStruct(QuoteDetailStruct quote)
    {
        QuoteDetailStruct cloned = null;

        if (quote != null )
        {
            cloned = new QuoteDetailStruct();
            cloned.productKeys = quote.productKeys;
            cloned.productName = quote.productName;
            cloned.statusChange = quote.statusChange;
            cloned.quote = cloneQuoteStruct(quote.quote);
        }
        return cloned;
    }

    public static QuoteDetailStruct[] cloneQuoteDetailStructs(QuoteDetailStruct[] quotes)
    {
        if ( quotes == null )
        {
            return null;
        }
        else
        {
            QuoteDetailStruct[] cloned = new QuoteDetailStruct[quotes.length];
            for ( int i = 0; i < quotes.length; i++ )
            {
                cloned[i] = cloneQuoteDetailStruct(quotes[i]);
            }

            return cloned;
        }
    }

}
