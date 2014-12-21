package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.inprocess.RemoteMarketQuery;

/**
 * @author Magic Magee
 */
public interface RemoteMarketQueryHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "RemoteMarketQueryHome";
    public RemoteMarketQuery create(InProcessSessionManager sessionManager);
}
