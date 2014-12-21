package com.cboe.infrastructureServices.sessionManagementService;

import com.cboe.idl.infrastructureServices.sessionManagementService.UserLoginStruct;

/**
 *  The callback interface with which to register with the
 *  SessionManagementService fascade for user session events.
 *  @author Steven Sinclair
 */
public interface SessionManagementServiceUserConsumer
{
	void acceptSessionOpened(int sessionKey,String userId);
	void acceptSessionClosed(int sessionKey, String userId, boolean forced, String message);
	void acceptLogin(String userName, String securitySessionId, String sourceComponent, int sessionKey);
	void acceptLogout(String userName, String sourceComponent, boolean forced, int sessionKey, String message);
    void acceptOpenSessions(UserLoginStruct[] userSession);
}
