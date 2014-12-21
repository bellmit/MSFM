package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: AbstractFirmFirmRoutingPropertyGroup
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Jun 20, 2006 2:40:11 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;
import com.cboe.interfaces.domain.routingProperty.Validator;

public abstract class AbstractRoutingPropertyGroup extends AbstractBasePropertyGroup
{
    public static final String PROPERTY_GROUP_CHANGE_EVENT = "RoutingPropertyGroup"; // PropertyCategoryTypes.ROUTING_PROPERTIES + "Group";
    public static final String PROPERTY_CHANGE_EVENT       = "RoutingProperty"; // PropertyCategoryTypes.ROUTING_PROPERTIES;

    protected AbstractRoutingPropertyGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    protected AbstractRoutingPropertyGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    protected AbstractRoutingPropertyGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    protected AbstractRoutingPropertyGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    protected AbstractRoutingPropertyGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                           List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    protected AbstractRoutingPropertyGroup(BasePropertyKey basePropertyKey, int versionNumber,
                                           List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    @Override
    protected String getPropertyCategoryType()
    {
        return PropertyCategoryTypes.ROUTING_PROPERTIES;
    }

    @Override
    protected BasePropertyFactory getPropertyFactory()
    {
        return RoutingPropertyFactoryHome.find();
    }

    @Override
    protected String getPropertyGroupChangeEvent()
    {
        return PROPERTY_GROUP_CHANGE_EVENT;
    }

    @Override
    protected String getPropertyChangeEvent()
    {
        return PROPERTY_CHANGE_EVENT;
    }
}