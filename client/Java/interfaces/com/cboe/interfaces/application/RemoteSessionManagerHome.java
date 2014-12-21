/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Sep 25, 2002
 * Time: 2:29:10 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

import com.cboe.idl.cmiUser.UserStruct;
public interface RemoteSessionManagerHome {
    /**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "RemoteSessionManagerHome";

    public SessionManager[] find(String userId);
    public SessionManager findRemoteSession(String userId, com.cboe.idl.cmi.UserSessionManager corbaSession );
    public void addRemoteSession(SessionManager sessionManager, com.cboe.idl.cmi.UserSessionManager corbaSession);
    public void removeRemoteSession(com.cboe.idl.cmi.UserSessionManager corbaSession);
}

