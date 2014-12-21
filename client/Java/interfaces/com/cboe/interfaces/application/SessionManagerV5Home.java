package com.cboe.interfaces.application;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

public interface SessionManagerV5Home extends SessionManagerHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionManagerV5Home";

    /**
     * Creates an instance of the V5 session manager.
     * @return reference to V5 session manager
     */
    public SessionManagerV5 createSessionManagerV5(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
    		throws DataValidationException, SystemException;

}
