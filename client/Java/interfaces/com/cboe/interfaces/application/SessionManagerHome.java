package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

/**
 * This is the common interface for the session manager Home
 * @author Jeff Illian
 */
public interface SessionManagerHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionManagerHome";

    /**
    * Creates an instance of the session manager service.
    *
    * @return reference to session manager service
    *
    * @author Jeff Illian
    */
    public SessionManager create(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey, CMIUserSessionAdmin clientListener, short sessionType, boolean gmdTextMessaging)
      throws DataValidationException, SystemException;

    /**
    * Removes an instance of the session.
    *
    * @param validUser the valid user information
    * @param session the session to be removed in association with the user
    * @author Jeff Illian
    */
    public void remove(SessionManager session, String userId);

    public String getUserSessionIor(SessionManager session);
}
