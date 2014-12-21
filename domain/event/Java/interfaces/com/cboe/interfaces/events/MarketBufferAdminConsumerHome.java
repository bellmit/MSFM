package com.cboe.interfaces.events;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;


/**
 */
public interface MarketBufferAdminConsumerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "MarketBufferAdminConsumerHome";

    /**
     * Returns a reference to the MarketBufferConsumer service.
     * 
     * @return reference to MarketBufferConsumer service
     * 
     */
    public MarketBufferAdminConsumer find();

    public void addUnfilteredConsumer(MarketBufferAdminConsumer consumer) throws SystemException, DataValidationException;
    public void addConsumerForGroup(MarketBufferAdminConsumer consumer, int groupKey) throws SystemException, DataValidationException;
    public void addConsumerForGroupAndCodec(MarketBufferAdminConsumer consumer, int groupKey, int codecId) throws SystemException, DataValidationException;
}
