package com.cboe.domain.routingProperty.properties;
//-----------------------------------------------------------------------------------
//Source file: FirmVolumeLimitGroup
//
//PACKAGE: com.cboe.domain.firmRoutingProperty.test.properties
//
//Created: Jun 28, 2006 9:46:00 AM
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;

/**
* This defines a group of RoutingProperties needed for "Firm Volume Limit"
* routing for the supplied BasePropertyKey.  This contains 1 BaseProperty to
* set the value for volume.
*
* The BasePropertyType for this group is RoutingPropertyTypeImpl.FIRM_VOLUME_LIMIT.
*/
public class FirmVolumeLimitSimpleGroup extends FirmVolumeLimitGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.FIRM_VOLUME_LIMIT_SIMPLE;

    public FirmVolumeLimitSimpleGroup(BasePropertyKey basePropertyKey)
    {
     super(basePropertyKey);
    }

    public FirmVolumeLimitSimpleGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
         throws DataValidationException, InvocationTargetException
    {
     super(basePropertyKey, propertyGroup);
    }

    public FirmVolumeLimitSimpleGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
     super(basePropertyKey, versionNumber);
    }

    public FirmVolumeLimitSimpleGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public FirmVolumeLimitSimpleGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                      List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public FirmVolumeLimitSimpleGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    @Override
    public BasePropertyType getType()
    {
     return ROUTING_PROPERTY_TYPE;
    }
}