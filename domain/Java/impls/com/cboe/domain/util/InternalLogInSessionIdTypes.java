package com.cboe.domain.util;

import com.cboe.idl.constants.LoginSessionOperations;

/**
 * This class is designed to provide the facility to define internal user login session Id
 * types
 */

public class InternalLogInSessionIdTypes implements LoginSessionOperations {

    public static final short LOGGED_OUT = -100;
    public static final short NOT_INITIALIZED = -999;

    public InternalLogInSessionIdTypes() {
    }
}