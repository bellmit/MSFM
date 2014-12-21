package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: ReasonabilityEditParameter
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// Created: Aug 23, 2006 10:29:48 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.List;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;

public class ReasonabilityEditParameter extends StringBasePropertyImpl
{
    public ReasonabilityEditParameter(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                  BasePropertyType type)
    {
        super(propertyCategory, propertyName, basePropertyKey, type);
    }

    public ReasonabilityEditParameter(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                      BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, basePropertyKey, type, validators);
    }

    public String getEditGroup()
    {
        return super.getStringValue();
    }

    public void setEditGroup(String value)
    {
        super.setStringValue(value);
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public BasePropertyType getPropertyType()
    {
        return RoutingPropertyTypeImpl.REASONABILITY_EDIT;
    }

    public int compareTo(Object other)
    {
        return super.compareTo(other);
    }
}
