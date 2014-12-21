package com.cboe.interfaces.application;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

public interface SessionManagerV7Home extends SessionManagerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionManagerV7Home";

    /**
     * Creates an instance of the V7 session manager.
     * @return reference to V7 session manager
     */
    public SessionManagerV7 createSessionManagerV7(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException;
}
