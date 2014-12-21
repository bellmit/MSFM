package com.cboe.interfaces.domain.userSession;

/**
 *  @author Steven Sinclair
 */
public interface UserLoginDescriptor
{
	public UserSessionDescriptor getUserSession();
	public String getSourceComponent();
	public int getReferenceCount();
	public void setUserSession(UserSessionDescriptor userSession);
	public void setSourceComponent(String sourceComponent);
	public void setReferenceCount(int referenceCount);
	public void incrReferenceCount();
	public void decrReferenceCount();
}
