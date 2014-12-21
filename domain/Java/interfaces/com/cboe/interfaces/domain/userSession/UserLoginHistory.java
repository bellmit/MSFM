package com.cboe.interfaces.domain.userSession;

import java.util.Date;

/**
 *  @author Steven Sinclair
 */
public interface UserLoginHistory
{
	public static final int CREATE_SESSION       = 1;
	public static final int JOIN_SESSION         = 2;
	public static final int CLOSE_SESSION        = 3;
	public static final int LEAVE_SESSION        = 4;
	public static final int FORCED_CLOSE_SESSION = 13;
	public static final int FORCED_LEAVE_SESSION = 14;

	public int getSessionKey();
	public int getAction();
	public String getUserId();
	public String getSourceComponent();
	public String getDescription();
	public Date getTime();
	public void setSessionKey(int sessionKey);
	public void setAction(int action);
	public void setUserId(String userId);
	public void setSourceComponent(String sourceComponent);
	public void setDescription(String description);
	public void setTime(Date time);
}
