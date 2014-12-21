package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.inprocess.MarketQuery;

/**
 * @author Jing Chen
 */
public interface MarketQueryHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "MarketQueryHome";
    public MarketQuery create(InProcessSessionManager sessionManager);
}
