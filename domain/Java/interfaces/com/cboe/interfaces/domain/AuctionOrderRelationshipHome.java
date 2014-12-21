package com.cboe.interfaces.domain;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

public interface AuctionOrderRelationshipHome
{
    public final static String HOME_NAME = "AuctionOrderRelationshipHome";
    
    /**
     * Create an auction order relationship domain object
     * @param auction database identifier
     * @param related order database identifier
     * @param relation type
     * @throws TransactionFailedException
     * @throws DataValidationException
     * @throws SystemException
     */
    public void create(long auctionDBid, long orderDBid, short type)
            throws TransactionFailedException, DataValidationException, SystemException;

}
