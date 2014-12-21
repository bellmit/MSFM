package com.cboe.domain.util;

/** Determine if invocation is for normal or quick startup. */
public class ClientQuickstart
{
    /** If this system property is defined, we want to start up faster. */
    private static final String PROP_QUICKSTART = "Client.Quickstart";

    private static boolean initialized = false;
    private static boolean isQuick;

    private ClientQuickstart() { } // do not instantiate this class

    /** Indicate request for normal or quick startup.
     * @return false for normal startup, true for quick startup.
     */
    public static final synchronized boolean isQuickstart()
    {
        if (!initialized)
        {
            isQuick = (null != System.getProperty(PROP_QUICKSTART));
            initialized = true;
        }
        return isQuick;
    }
}
