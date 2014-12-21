package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: EligiblePDPMGroup
//
// PACKAGE: com.cboe.domain.routingProperty.properties
// 
// Created: Aug 4, 2006 3:09:05 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;

public class AllowIncomingISOGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.ALLOW_INCOMING_ISO;

    public static final String ALLOW_INCOMING_ISO = "AllowIncomingIso";

    private BooleanBaseProperty allowIncominISO;

    public AllowIncomingISOGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public AllowIncomingISOGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public AllowIncomingISOGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public AllowIncomingISOGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public AllowIncomingISOGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                            List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public AllowIncomingISOGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }


    protected void initializeProperties()
    {
        allowIncominISO = new BooleanBasePropertyImpl(getPropertyCategoryType(), ALLOW_INCOMING_ISO, getPropertyKey(), getType());
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equalsIgnoreCase(ALLOW_INCOMING_ISO))
        {
            return allowIncominISO;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = allowIncominISO;

        return properties;
    }

    public boolean isAllowIncomingISO()
    {
        return allowIncominISO.getBooleanValue();
    }

    public void setAllowIncomingIso(boolean flag)
    {
        allowIncominISO.setBooleanValue(flag);
        firePropertyChange();
    }

    public Object clone() throws CloneNotSupportedException
    {
        AllowIncomingISOGroup newGroup = (AllowIncomingISOGroup) super.clone();
        newGroup.allowIncominISO = (BooleanBaseProperty) allowIncominISO.clone();

        return newGroup;
    }
}
