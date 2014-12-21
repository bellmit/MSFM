package com.cboe.interfaces.application;

/**
 * This is the common interface for the CASShutdown Home
 * @author Derek T. Chambers-Boucher
 */
public interface CASShutdownHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "CASShutdownHome";

    /**
     * Name/Alias of the BOHome to shutdown
     */
    public final static String SHUTDOWN_HOME_NAME = "ShutdownHome";

    /**
    * Find an instance of the CASShutdown.
    * Singleton Pattern will be used.
    *
    * @author Derek T. Chambers-Boucher
    */
    public CASShutdown find();

    /**
    * Creates an instance of the CASShutdown.
    *
    * @author Derek T. Chambers-Boucher
    */
    public CASShutdown create();

}
