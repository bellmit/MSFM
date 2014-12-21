//
// -----------------------------------------------------------------------------------
// Source file: AlternateDestinationsGroup.java
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.common.DestinationListBaseProperty;
import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.DestinationListBasePropertyImpl;

public class AlternateDestinationsGroup extends AbstractRoutingPropertyGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.ALTERNATE_DESTINATIONS;

    public static final String DESTINATIONS = "Destinations";

    protected DestinationListBaseProperty destinations;

    public AlternateDestinationsGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public AlternateDestinationsGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public AlternateDestinationsGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public AlternateDestinationsGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public AlternateDestinationsGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                      List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public AlternateDestinationsGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BaseProperty getProperty(String name)
            throws DataValidationException
    {
        if(name.equals(DESTINATIONS))
        {
            return destinations;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public Destination[] getDestinations()
    {
        return destinations.getDestinationListValue();
    }

    public void setDestinations(Destination[] destinations)
    {
        this.destinations.setDestinationListValue(destinations);
        firePropertyChange();
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = destinations;

        return properties;
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        AlternateDestinationsGroup newGroup = (AlternateDestinationsGroup) super.clone();
        newGroup.destinations = (DestinationListBaseProperty) destinations.clone();

        return newGroup;
    }

    protected void initializeProperties()
    {
        destinations = new DestinationListBasePropertyImpl(getPropertyCategoryType(), DESTINATIONS, getPropertyKey(), getType());
        destinations.setOptional(false);
    }
}
