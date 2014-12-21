package com.cboe.interfaces.application;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
/**
 * @author Arun Ramachandran Nov 12, 2009
 *
 */
public interface SessionManagerV8Home extends SessionManagerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionManagerV8Home";

    /**
     * Creates an instance of the V8 session manager.
     * @return reference to V8 session manager
     */
    public SessionManagerV8 createSessionManagerV8(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException;
}
