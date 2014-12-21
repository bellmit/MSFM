package com.cboe.interfaces.domain;

import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;



/**
 * @author: baranski
 *
 */
public interface TradedThroughOrderHome {

    public final static String HOME_NAME = "TradedThroughOrderHome";
    /**
     * Create an instance of the class
     * @param alert
     * @param orderId
     *
     * @throws com.cboe.exceptions.TransactionFailedException
     * @throws com.cboe.exceptions.DataValidationException
     * @throws com.cboe.exceptions.SystemException
     */
   public TradedThroughOrder create(Alert alert, long orderId)
            throws TransactionFailedException, DataValidationException, SystemException;

}