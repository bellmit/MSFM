package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: FirmTradingParameterGroup
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// Created: Aug 23, 2006 10:29:48 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractFirmPropertyGroup;
import com.cboe.domain.routingProperty.FirmPropertyTypeImpl;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class FirmTradingParameterGroup extends AbstractFirmPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = FirmPropertyTypeImpl.FIRM_TRADING_PARAM;
    
    public static final String CLEARING = "CLEARING";

    private FirmTradingParameter clearingType;

    public FirmTradingParameterGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public FirmTradingParameterGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public FirmTradingParameterGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public FirmTradingParameterGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public FirmTradingParameterGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                     List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public FirmTradingParameterGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(CLEARING))
        {
            return clearingType;
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
        properties[0] = clearingType;

        return properties;

    }

    protected void initializeProperties()
    {
        clearingType = new FirmTradingParameter(getPropertyCategoryType(), CLEARING, getPropertyKey(), getType());
        try
        {
            clearingType.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + CLEARING + " category="
                          + getPropertyCategoryType() + "type=" + getType(), e);
        }
    }

    public String getClearingType()
    {
        return clearingType.getClearingType();
    }

    public void setClearingType(String value)
    {
        this.clearingType.setClearingType(value);
//        firePropertyChange();
    }


    public Object clone() throws CloneNotSupportedException
    {
        FirmTradingParameterGroup newGroup = (FirmTradingParameterGroup) super.clone();
        newGroup.clearingType = (FirmTradingParameter) clearingType.clone();

        return newGroup;
    }
}
