package com.cboe.interfaces.application;

/**
 * This is the common interface for the User Market Query Home
 * @author Jeff Illian
 */
public interface UserQuoteHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserQuoteHome";

    /**
    * Creates an instance of the quote service.
    *
    * @author Connie Feng
    */
    public QuoteV7 create(SessionManager sessionManager);
}
