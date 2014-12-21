package com.cboe.ffInterfaces;

import com.cboe.ffidl.ffExceptions.SystemException;

public interface TradeReportConsumerHome
{
    static final String HOME_NAME="TradeReportConsumerHome";
    
    /**
     * Returns the publisher
     */
    TradeReportConsumer create();
    
    /**
     * Returns the publisher
     */
    TradeReportConsumer find();

    /**
     * Sets consumerImpl to be a consumer of this channel
     */
    void addConsumer(TradeReportConsumer consumer) throws SystemException;
}
