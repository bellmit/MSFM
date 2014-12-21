package com.cboe.interfaces.application;

import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import java.util.Map;

/**
 * This is the common interface for the session manager Home
 * @author Jeff Illian
 */
public interface SessionManagerCleanupHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionManagerCleanupHome";

    /**
    * Removes an instance of the session.
    *
    * @param validUser the valid user information
    * @param session the session to be removed in association with the user
    * @author Jeff Illian
    */
    public void logoffAllUsers(boolean componentFailure);
    public void logoffUserSession(Object clientListener);
}
