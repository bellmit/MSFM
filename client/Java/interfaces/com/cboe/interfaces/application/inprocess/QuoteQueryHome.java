package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.inprocess.QuoteEntry;

/**
 * @author Jing Chen
 */
public interface QuoteQueryHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteQueryHome";
    public QuoteQuery create(InProcessSessionManager sessionManager);
}
