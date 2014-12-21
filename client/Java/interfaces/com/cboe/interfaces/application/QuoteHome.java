
package com.cboe.interfaces.application;

public interface QuoteHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserQuoteHome";

    /**
    * Creates an instance of the quote service.
    *
    * @author Emily Huang
    */
    public Quote create(SessionManager sessionManager);
}