package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: BoothWireOrderPreference
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// Created: Feb 8, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.List;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;

public class BoothWireOrderPreference extends StringBasePropertyImpl
{
    public static final String ORDER_PREFERENCE_BOOTH = "B";
    public static final String ORDER_PREFERENCE_WIRE  = "W";


    public BoothWireOrderPreference(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                    BasePropertyType type)
    {
        super(propertyCategory, propertyName, basePropertyKey, type);
    }

    public BoothWireOrderPreference(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                    BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, basePropertyKey, type, validators);
    }

    public String getOrderPreference()
    {
        return super.getStringValue();
    }

    public void setOrderPreference(String value)
    {
        super.setStringValue(value);
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public BasePropertyType getPropertyType()
    {
        return RoutingPropertyTypeImpl.BOOTH_WIRE_ORDER_PREFERENCE;
    }

    public int compareTo(Object other)
    {
        return super.compareTo(other);
    }

    public boolean isBooth()
    {
        return getOrderPreference().equals(ORDER_PREFERENCE_BOOTH);
    }

    public boolean isWire()
    {
        return getOrderPreference().equals(ORDER_PREFERENCE_WIRE);
    }
}
