package com.cboe.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.common.DirectedAIMFirmRegistration;

public class DirectedAIMFirmRegistrationImpl implements DirectedAIMFirmRegistration
{
    public boolean isRegistered;
    public long	lastUpdatedTime;
    private String displayStr = null;
    private int versionNumber = 0;

    public DirectedAIMFirmRegistrationImpl(boolean isRegistered, long lastUpdatedTime)
    {
        this.isRegistered = isRegistered;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String toString()
    {
        if (displayStr == null)
        {
            StringBuffer sb = new StringBuffer(20);
            sb.append("[");
            sb.append("isRegistered=").append(isRegistered);
            sb.append("  ").append(":").append("  ");
            sb.append("lastUpdatedTime=").append(lastUpdatedTime);
            sb.append("]");
            displayStr = sb.toString();
        }
        return displayStr;
    }

    public int compareTo(DirectedAIMFirmRegistration otherDest)
    {
        return toString().compareTo(otherDest.toString());
    }

	public boolean getIsRegistered() 
	{
		return isRegistered;
	}

	public long getLastUpdatedTime() 
	{
		return lastUpdatedTime;
	}

	public void setIsRegistered(boolean isRegistered) 
	{
		this.isRegistered = isRegistered;
	}

	public void setLastUpdatedTime(long lastUpdateTime) 
	{
		this.lastUpdatedTime = lastUpdateTime;
	}

    public boolean isRegistered()
    {
        return isRegistered;
    }

	public int getVersionNumber()
	{
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) 
	{
		this.versionNumber = versionNumber;
	}
}

