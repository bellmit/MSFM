//
// -----------------------------------------------------------------------------------
// Source file: LoginSessionMode.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

/**
 * Formats LoginSessionMode's
 */
public class LoginSessionMode
{
    public static final char PRODUCTION = com.cboe.idl.cmiConstants.LoginSessionModes.PRODUCTION;
    public static final char NETWORK_TEST = com.cboe.idl.cmiConstants.LoginSessionModes.NETWORK_TEST;
    public static final char STAND_ALONE_TEST = com.cboe.idl.cmiConstants.LoginSessionModes.STAND_ALONE_TEST;

    private static final String PRODUCTION_STRING = "Production";
    private static final String NETWORK_TEST_STRING = "Network Test";
    private static final String STAND_ALONE_TEST_STRING = "Stand Alone Test";
    private static final String UNKNOWN_STRING = "Unknown";

    /**
     * Hide the default constructor from the public interface
     */
    private LoginSessionMode()
    {
    }

    /**
     * Returns a string representation of the passed LoginSessionMode
     * @param mode to be convert to String representation
     * @return a string representation of the mode
     * @see com.cboe.idl.cmiConstants.LoginSessionModes
     */
    public static String toString(char mode)
    {
        switch(mode)
        {
            case PRODUCTION:
                return PRODUCTION_STRING;
            case NETWORK_TEST:
                return NETWORK_TEST_STRING;
            case STAND_ALONE_TEST:
                return STAND_ALONE_TEST_STRING;
            default:
                return new StringBuffer(20).append(UNKNOWN_STRING).append(" ").append(mode).toString();
        }
    }
}