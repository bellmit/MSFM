package com.cboe.interfaces.application;

/**
 * This is the common interface for the User Market Query Home
 * @author Jeff Illian
 */
public interface WebServerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "WebServerHome";

    /**
    * Creates an instance of the quote service.
    *
    * @author Connie Feng
    */
    public WebServer create(int tcp_port);
}
