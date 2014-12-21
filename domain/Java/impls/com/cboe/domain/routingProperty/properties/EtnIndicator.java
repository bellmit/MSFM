package com.cboe.domain.routingProperty.properties;

import java.util.List;

import com.cboe.domain.routingProperty.FirmPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class EtnIndicator extends StringBasePropertyImpl
{
    public EtnIndicator(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
            BasePropertyType type)
{
super(propertyCategory, propertyName, basePropertyKey, type);
}

public EtnIndicator(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                    BasePropertyType type, List<Validator> validators)
{
    super(propertyCategory, propertyName, basePropertyKey, type, validators);
}

public String getEtnIndicator()
{
return super.getStringValue();
}

public void setEtnIndicator(String value)
{
super.setStringValue(value);
}

public Object clone() throws CloneNotSupportedException
{
return super.clone();
}

public BasePropertyType getPropertyType()
{
return FirmPropertyTypeImpl.FIRM_ETN_PARAM; // same as: FirmTradingParameterGroup.ROUTING_PROPERTY_TYPE;
}

public int compareTo(Object other)
{
return super.compareTo(other);
}
}
