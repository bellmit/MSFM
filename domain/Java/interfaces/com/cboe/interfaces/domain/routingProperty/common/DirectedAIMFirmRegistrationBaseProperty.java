package com.cboe.interfaces.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;

public interface DirectedAIMFirmRegistrationBaseProperty extends BaseProperty
{
	DirectedAIMFirmRegistration getDirectedAIMFirmRegistration();

    void setDirectedAIMFirmRegistration(DirectedAIMFirmRegistration value);

    public boolean getIsRegistered();
	
	public void setIsRegistered(boolean isRegistered);
	
	public long getLastUpdatedTime();
	
	public void setLastUpdatedTime(long lastUpdateTime);

}
