/**
 * @author Jing Chen
 */
package com.cboe.interfaces.cfix;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;

import java.util.Map;

public interface CfixSessionManagerHome {
    	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "CfixSessionManagerHome";

    public CfixSessionManager createCfixSession(SessionProfileUserStructV2 validUser, String sessionId, CfixUserSessionAdminConsumer clientListener, short sessionType, boolean gmdTextMessaging)
      throws DataValidationException, SystemException;

    public void remove(CfixSessionManager session, String userId);

    public Map getActiveSessions();
}
