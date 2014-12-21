package com.cboe.interfaces.domain.routingProperty.common;

public interface DirectedAIMFirmRegistration extends Comparable<DirectedAIMFirmRegistration>
{	
	public boolean getIsRegistered();
	
	public void setIsRegistered(boolean isRegistered);
	
	public long getLastUpdatedTime();
	
	public void setLastUpdatedTime(long lastUpdateTime);
	
	public int getVersionNumber();
	
	public void setVersionNumber(int versionNumber);

}
