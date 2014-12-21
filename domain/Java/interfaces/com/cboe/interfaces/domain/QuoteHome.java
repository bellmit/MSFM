package com.cboe.interfaces.domain;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.UpdateFailedException;
import com.cboe.idl.cmiQuote.QuoteStructV3;
import com.cboe.idl.cmiQuote.QuoteStructV4;

/**
 * Define what the external interface to an QuoteHome is, and also implements
 * the singleton pattern to access the configured type of QuoteHome.
 */
public interface QuoteHome
{
    public final static String HOME_NAME = "QuoteHome";

    public abstract Quote create(int classKey, QuoteStructV3 aQuoteStruct)
            throws TransactionFailedException, AlreadyExistsException;
    
    public abstract Quote create(int classKey, QuoteStructV4 aQuoteStruct)
    throws TransactionFailedException, AlreadyExistsException;

    public abstract void delete(Quote aQuote)
            throws UpdateFailedException;

    public abstract Quote findQuote(int productKey, String memberKey);

    public Quote[] findQuotesForProduct(int productKey);

    public Quote[] findQuotesForProduct(int productKey, boolean sortedByBidOrAsk);

    public abstract Quote[] findQuotesForUser(String userId);

    public Quote[] findQuotesForUser(String userId, int classKey);

    public String[] findUsersForProduct(int productKey, boolean withActiveQuoteOnly);

    public int getActiveQuotes( String userId );
    
    public Quote[] findQuotesAndSortByBidBookedTime(int productKey);
    
    public Quote[] findQuotesAndSortByAskBookedTime(int productKey);
    
    public void createMapForUser(String p_userId, int p_productClassKey);
    
    public int getMaxBootStrapSize();
    
}
