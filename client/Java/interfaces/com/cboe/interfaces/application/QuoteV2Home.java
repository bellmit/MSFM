
package com.cboe.interfaces.application;

public interface QuoteV2Home {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserQuoteHome";

    /**
    * Creates an instance of the quoteV2 service.
    *
    * @author Emily Huang
    */
    public QuoteV2 create(SessionManager sessionManager);
}