//
// -----------------------------------------------------------------------------------
// Source file: DefaultDestinationGroup.java
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

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.common.DestinationBaseProperty;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.common.DestinationBasePropertyImpl;

/**
 * This defines a BasePropertyGroup containing one DestinationBaseProperty which
 * represents a DefaultDestination.
 */
public abstract class DefaultDestinationGroup/*<T extends DestinationBaseProperty>*/ extends AbstractRoutingPropertyGroup
{
    public static final String DESTINATION = "Destination";

    protected DestinationBaseProperty destination;

    public DefaultDestinationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public DefaultDestinationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public DefaultDestinationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public DefaultDestinationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public DefaultDestinationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                   List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public DefaultDestinationGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BaseProperty getProperty(String name)
            throws DataValidationException
    {
        if(name.equals(DESTINATION))
        {
            return destination;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public Destination getDestination()
    {
        return destination.getDestination();
    }

    public void setDestination(Destination destination)
    {
        this.destination.setDestination(destination);
        firePropertyChange();
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = destination;

        return properties;
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        DefaultDestinationGroup newGroup = (DefaultDestinationGroup) super.clone();
        newGroup.destination = (DestinationBaseProperty) destination.clone();

        return newGroup;
    }

    protected void initializeProperties()
    {
        destination = new DestinationBasePropertyImpl(getPropertyCategoryType(), DESTINATION, getPropertyKey(), getType());
    }
}
