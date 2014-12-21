package com.cboe.domain.routingProperty.properties;

import java.util.List;

import com.cboe.domain.routingProperty.FirmPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class DirectedAIMNotificationFirmInfo extends StringBasePropertyImpl
{
    public DirectedAIMNotificationFirmInfo(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
	            BasePropertyType type)
	{
    	super(propertyCategory, propertyName, basePropertyKey, type);
	}

	public DirectedAIMNotificationFirmInfo(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
	                    BasePropertyType type, List<Validator> validators)
	{
	    super(propertyCategory, propertyName, basePropertyKey, type, validators);
	}

	public String getDirectedAIMNotificationInfo()
	{
		return super.getStringValue();
	}

	public void setDirectedAIMNotificationInfo(String value)
	{
		super.setStringValue(value);
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public BasePropertyType getPropertyType()
	{
		return FirmPropertyTypeImpl.DIRECTED_AIM_NOTIFICATION_FIRM_INFO_PARAM; 
	}

	public int compareTo(Object other)
	{
		return super.compareTo(other);
	}
}
