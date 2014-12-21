package com.cboe.interfaces.domain;

import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;


/**
 * @author: baranski
 *
 */
public interface OrderRelationshipHome {

    public final static String HOME_NAME = "OrderRelationshipHome";
      /**
       *  Create an order relationship domain object
       * @param leftOid
       * @param rightOid
       * @param type
       * @throws TransactionFailedException
       * @throws DataValidationException
       * @throws SystemException
       */
      public void create(long leftOid1, long rightOid, short type, String relatedSession)
              throws TransactionFailedException, DataValidationException, SystemException;

      public OrderRelationship createForQuery();

}
