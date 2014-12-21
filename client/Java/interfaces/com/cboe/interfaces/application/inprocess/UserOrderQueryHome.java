package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.inprocess.OrderEntry;

/**
 * @author Jing Chen
 */
public interface UserOrderQueryHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserOrderQueryHome";
    public UserOrderQuery create(InProcessSessionManager sessionManager);
}
