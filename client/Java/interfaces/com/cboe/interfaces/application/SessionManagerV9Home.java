package com.cboe.interfaces.application;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
/**
 * @author Patel Piyush June 23, 2010
 *
 */

public interface SessionManagerV9Home extends SessionManagerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionManagerV9Home";

    /**
     * Creates an instance of the V9 session manager.
     * @return reference to V9 session manager
     */
    public SessionManagerV9 createSessionManagerV9(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException;
}