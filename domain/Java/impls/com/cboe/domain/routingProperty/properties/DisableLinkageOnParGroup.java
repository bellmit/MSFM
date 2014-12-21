package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: 
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

public class DisableLinkageOnParGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.DISABLE_LINKAGE_ON_PAR;

    public static final String DISABLE_LINKAGE_ON_PAR = "DisableLinkageOnPar";

    private BooleanBaseProperty disableLinkageOnPar;

    public DisableLinkageOnParGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public DisableLinkageOnParGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public DisableLinkageOnParGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public DisableLinkageOnParGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public DisableLinkageOnParGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                            List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public DisableLinkageOnParGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }


    protected void initializeProperties()
    {
        disableLinkageOnPar = new BooleanBasePropertyImpl(getPropertyCategoryType(), DISABLE_LINKAGE_ON_PAR, getPropertyKey(), getType());
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equalsIgnoreCase(DISABLE_LINKAGE_ON_PAR))
        {
            return disableLinkageOnPar;
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
        properties[0] = disableLinkageOnPar;

        return properties;
    }

    public boolean getDisableLinkageOnPar()
    {
        return disableLinkageOnPar.getBooleanValue();
    }

    public void setDisableLinkageOnPar(boolean flag)
    {
        disableLinkageOnPar.setBooleanValue(flag);
        firePropertyChange();
    }

    public Object clone() throws CloneNotSupportedException
    {
        DisableLinkageOnParGroup newGroup = (DisableLinkageOnParGroup) super.clone();
        newGroup.disableLinkageOnPar = (BooleanBaseProperty) disableLinkageOnPar.clone();

        return newGroup;
    }
}
