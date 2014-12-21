/*
 * Created on Jul 14, 2004
 *
 */
package com.cboe.presentation.fix.session;

import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.interfaces.presentation.userSession.FixUserSessionManager;


/**
 * Source of new FIX session managers
 *
 * @author Don Mendelson
 *
 */
public class UserSessionManagerFixHome  {

	public final static String HOME_NAME = "UserSessionManagerFixHome";

	/**
	 * Creates a new UserSessionManagerFixHome
	 */
	public UserSessionManagerFixHome() {
		super();
	}

	/**
	 * Creates a new user session manager or finds it if it already exists
	 *
	 * @param userLogonStruct
	 *            identifies the user
	 * @param sessionType
	 * @param userSessionAdmin
	 * @return a Business Obect Interceptor that implements SessionManager
	 */
	public FixUserSessionManager createSession(UserLogonStruct userLogonStruct,
			short sessionType, CMIUserSessionAdmin userSessionAdmin) {
		String userName = userLogonStruct.userId;
			UserSessionManagerFixImpl bo = new UserSessionManagerFixImpl();
			bo.setSessionType(sessionType);
			bo.setUserSessionAdmin(userSessionAdmin);
		return  bo;
	}

}