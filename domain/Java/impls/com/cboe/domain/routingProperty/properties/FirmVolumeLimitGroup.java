package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: FirmVolumeLimitGroup
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.properties
// 
// Created: Jun 28, 2006 9:46:00 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.IntegerBaseProperty;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.IntegerBasePropertyImpl;

/**
 * This defines a group of RoutingProperties needed for "Firm Volume Limit"
 * routing for the supplied BasePropertyKey.  This contains 1 BaseProperty to
 * set the value for volume.
 *
 * The BasePropertyType for this group is RoutingPropertyTypeImpl.FIRM_VOLUME_LIMIT.
 */
public abstract class FirmVolumeLimitGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    //public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.FIRM_VOLUME_LIMIT;

    private static final String VOLUME = "Volume";

    private IntegerBaseProperty volume;

    public FirmVolumeLimitGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public FirmVolumeLimitGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public FirmVolumeLimitGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public FirmVolumeLimitGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public FirmVolumeLimitGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public FirmVolumeLimitGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    /**
     * Gets the BasePropertyType for this group that identifies the type of this group.
     */
    public abstract BasePropertyType getType();
    
    protected void initializeProperties()
    {
        volume = new IntegerBasePropertyImpl(getPropertyCategoryType(), VOLUME, getPropertyKey(), getType());
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(VOLUME))
        {
            return volume;
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
        properties[0] = volume;

        return properties;
    }

    public Object clone() throws CloneNotSupportedException
    {
        FirmVolumeLimitGroup newGroup = (FirmVolumeLimitGroup) super.clone();
        newGroup.volume = (IntegerBaseProperty) volume.clone();

        return newGroup;
    }

    public int getVolume()
    {
        return volume.getIntegerValue();
    }

    public void setVolume(int intValue)
    {
        volume.setIntegerValue(intValue);
        firePropertyChange();
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries =  {VOLUME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}