package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

/**
 * @author Jing Chen
 */
public interface InProcessSessionManagerHome
{
	public final static String HOME_NAME = "InProcessSessionManagerHome";

    public InProcessSessionManager createInProcessSession(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey, UserSessionAdminConsumer clientListener)
      throws DataValidationException, SystemException;

    public void remove(InProcessSessionManager session, String userId);

}
