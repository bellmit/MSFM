package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: BoothWireOrderPreferenceGroup
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// Created: Feb 8, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
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

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class BoothWireOrderPreferenceGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.BOOTH_WIRE_ORDER_PREFERENCE;
    
    public static final String ORDER_PREFERENCE = "Order Preference";

    private BoothWireOrderPreference orderPrefrence;


    public BoothWireOrderPreferenceGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public BoothWireOrderPreferenceGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public BoothWireOrderPreferenceGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public BoothWireOrderPreferenceGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public BoothWireOrderPreferenceGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                         List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public BoothWireOrderPreferenceGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(ORDER_PREFERENCE ))
        {
            return orderPrefrence;
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
        properties[0] = orderPrefrence;

        return properties;

    }

    protected void initializeProperties()
    {
        orderPrefrence = new BoothWireOrderPreference(getPropertyCategoryType(), ORDER_PREFERENCE, getPropertyKey(), getType());
        try
        {
            orderPrefrence.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + ORDER_PREFERENCE + " category="
                          + getPropertyCategoryType() + "type=" + getType(), e);
        }
    }

    public String getOrderPreference()
    {
        return orderPrefrence.getOrderPreference();
    }

    public void setOrderPreference(String value)
    {
        this.orderPrefrence.setOrderPreference(value);
//        firePropertyChange();
    }


    public Object clone() throws CloneNotSupportedException
    {
        BoothWireOrderPreferenceGroup newGroup = (BoothWireOrderPreferenceGroup) super.clone();
        newGroup.orderPrefrence= (BoothWireOrderPreference) orderPrefrence.clone();

        return newGroup;
    }
}
