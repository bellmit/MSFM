package com.cboe.domain.routingProperty.properties;

import java.util.List;

import com.cboe.domain.routingProperty.common.LongBasePropertyImpl;
import com.cboe.domain.routingProperty.common.DateTimeContainerImpl;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.DateTimeContainer;

public class DirectedAIMFirmRegistrationDateParameter extends LongBasePropertyImpl {

    private DateTimeContainer dateTimeContainer;

    public DirectedAIMFirmRegistrationDateParameter(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
            BasePropertyType type)
	{
    	super(propertyCategory, propertyName, basePropertyKey, type);
	}

	public DirectedAIMFirmRegistrationDateParameter(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
	            BasePropertyType type, List<Validator> validators)
	{
		super(propertyCategory, propertyName, basePropertyKey, type, validators);
	}

    /**
     * These two following methods are the ones that were originally specified by the new property descriptor
     * in DirectedAIMFirmRegistrationDateParameterBeanInfo
     */


    public long getDirectedAIMFirmRegistrationDate()
	{
		return super.getLongValue();
	}
	
	public void setDirectedAIMFirmRegistrationDate(long value)
	{
		super.setLongValue(value);
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	public int compareTo(Object other)
	{
		return super.compareTo(other);
	}

    public DateTimeContainer getDateTimeContainer()
    {
        return new DateTimeContainerImpl(super.getLongValue());
    }

    public void setDateTimeContainer(DateTimeContainer dtc)
    {
        dateTimeContainer = new DateTimeContainerImpl(dtc.getTime());
        super.setLongValue(dtc.getTime());
    }

}
