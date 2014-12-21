package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.inprocess.OrderEntry;

/**
 * @author Jing Chen
 */
public interface OrderEntryHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserOrderEntryHome";
    public OrderEntry create(InProcessSessionManager sessionManager);
}
