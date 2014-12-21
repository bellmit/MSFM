package com.cboe.interfaces.domain;

import com.cboe.idl.cmiQuote.*;
import com.cboe.exceptions.*;


public interface RFQHistoryHome
{
    public final static String HOME_NAME = "RFQHistoryHome";

    /**
    * Creates and stores a new instance of RFQStruct into the database
    * @author Alex Torres
    */
    public RFQHistory create(String userId, RFQStruct aStruct, short eventType, short productState) throws SystemException;
    public RFQHistory[] findProductRfqsByTime(String userId, int productKey, long startTime, short direction);
    public RFQHistory[] findClassRfqsByTime(String userId, int classKey, long startTime, short direction);
}
