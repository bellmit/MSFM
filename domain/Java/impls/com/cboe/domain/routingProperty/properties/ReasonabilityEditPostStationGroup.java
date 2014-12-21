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
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;

public class ReasonabilityEditPostStationGroup extends ReasonabilityEditGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.REASONABILITY_EDIT_POST_STATION;

    public ReasonabilityEditPostStationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public ReasonabilityEditPostStationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public ReasonabilityEditPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public ReasonabilityEditPostStationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public ReasonabilityEditPostStationGroup(BasePropertyKey basePropertyKey,
                                             PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public ReasonabilityEditPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber,
                                             List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }
}
