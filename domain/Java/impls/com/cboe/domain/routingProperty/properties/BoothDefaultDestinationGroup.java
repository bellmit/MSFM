//
// -----------------------------------------------------------------------------------
// Source file: BartDefaultDestinationGroup.java
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
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.AbstractDropCopyPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;

/**
 * This is a DefaultDestinationGroup that has has the BasePropertyType "BoothDefaultDestination".
 */
public class BoothDefaultDestinationGroup extends AbstractDropCopyPropertyGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.BOOTH_DEFAULT_DESTINATION;

    public BoothDefaultDestinationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public BoothDefaultDestinationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public BoothDefaultDestinationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public BoothDefaultDestinationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public BoothDefaultDestinationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                        List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public BoothDefaultDestinationGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        BoothDefaultDestinationGroup newGroup = (BoothDefaultDestinationGroup) super.clone();
        return newGroup;
    }
}
