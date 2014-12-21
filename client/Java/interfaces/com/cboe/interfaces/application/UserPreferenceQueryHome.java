package com.cboe.interfaces.application;

/**
 * This is the common interface for the user preference query home
 * @author Derek T. Chambers-Boucher
 */
public interface UserPreferenceQueryHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserPreferenceQueryHome";
    /**
    * Creates an instance of the user preference query service.
    *
    * @return reference to user preference query service
    *
    * @author Derek T. Chambers-Boucher
    */
    public UserPreferenceQuery create(SessionManager sessionManager);
}
