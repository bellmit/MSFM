package com.cboe.interfaces.application;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.user.SessionProfileUserStructV2;


public interface SessionManagerTMSHome  extends SessionManagerHome {
	 /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionManagerTMSHome";
    
    public SessionManagerTMS createSessionManagerTMS(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey, CMIUserSessionAdmin clientListener, short sessionType, boolean gmdTextMessaging)
    		throws DataValidationException, SystemException;
}
