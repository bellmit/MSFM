package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.inprocess.QuoteEntry;

/**
 * @author Jing Chen
 */
public interface QuoteEntryHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteEntryHome";
    public QuoteEntry create(InProcessSessionManager sessionManager);
}
