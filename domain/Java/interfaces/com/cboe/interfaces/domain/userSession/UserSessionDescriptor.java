package com.cboe.interfaces.domain.userSession;

import java.util.*;

/**
 *  @author Steven Sinclair
 */
public interface UserSessionDescriptor
{
	public Collection getUserLogins();
	public String getUserId();
	public String getLastLoginSource();
	public int getSessionKey();
	public boolean isSessionOpen();
	public void setUserId(String userId);
	public void setLastLoginSource(String lastLoginSource);
	public void setSessionOpen(boolean sessionOpen);
	public void addLogin(UserLoginDescriptor userLogin);
	public void removeLogin(UserLoginDescriptor userLogin);
	public void login(String sourceComponent);
	public void logout(String sourceComponent);
	public UserLoginDescriptor findBySourceComponent(String sourceComponent);
}
