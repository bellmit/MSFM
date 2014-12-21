package com.cboe.interfaces.application;

/**
 * This is the common interface for the user trading parameters home
 * @author Mike Pyatetsky
 */
public interface UserTradingParametersHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserTradingParametersHome";
    /**
    * Creates an instance of the user preference query service.
    *
    * @return reference to user preference query service
    *
    * @author Mike Pyatetsky
    */
    public UserTradingParametersV5 create(SessionManager sessionManager);
}
