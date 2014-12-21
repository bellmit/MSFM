//
// -----------------------------------------------------------------------------------
// Source file: AbstractDropCopyPropertyGroup.java
//
// PACKAGE: com.cboe.domain.routingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.common.DropCopyBaseProperty;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.common.DropCopyBasePropertyImpl;

public abstract class AbstractDropCopyPropertyGroup extends AbstractRoutingPropertyGroup
{
    public static final String DESTINATIONS = "Destinations";
    protected DropCopyBaseProperty destinations;


    protected AbstractDropCopyPropertyGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    protected AbstractDropCopyPropertyGroup(BasePropertyKey basePropertyKey,
                                           PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    protected AbstractDropCopyPropertyGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    protected AbstractDropCopyPropertyGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    protected AbstractDropCopyPropertyGroup(BasePropertyKey basePropertyKey,
                                            PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    protected AbstractDropCopyPropertyGroup(BasePropertyKey basePropertyKey, int versionNumber,
                                            List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if(name.equals(DESTINATIONS))
        {
            return destinations;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.",
                                                           0);
        }
    }

    public Destination[] getDestinations()
    {
        return destinations.getDropCopyListValue().getDestinations();
    }

    public void setDestinations(Destination[] destinations)
    {
        this.destinations.getDropCopyListValue().setDestinations(destinations);
        firePropertyChange();
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = destinations;

        return properties;
    }

    public Object clone() throws CloneNotSupportedException
    {
        AbstractDropCopyPropertyGroup newGroup = (AbstractDropCopyPropertyGroup) super.clone();
        newGroup.destinations = (DropCopyBaseProperty) destinations.clone();

        return newGroup;
    }

    protected void initializeProperties()
    {
        destinations = new DropCopyBasePropertyImpl(getPropertyCategoryType(), DESTINATIONS,
                                                           getPropertyKey(), getType());
    }

    public Destination getDirectRoute()
    {
        return destinations.getDirectRoute();
    }

    public void setDirectRoute(Destination destination)
    {
        destinations.setDirectRoute(destination);
        firePropertyChange();
    }

    public Destination getCancelDropCopy()
    {
        return destinations.getCancelDropCopy();
    }

    public void setCancelDropCopy(Destination destination)
    {
        destinations.setCancelDropCopy(destination);
        firePropertyChange();
    }

    public Destination getFillDropCopy()
    {
        return destinations.getFillDropCopy();
    }

    public void setFillDropCopy(Destination destination)
    {
        destinations.setFillDropCopy(destination);
        firePropertyChange();
    }
}
