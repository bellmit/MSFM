package com.cboe.domain.routingProperty.properties;

//-----------------------------------------------------------------------------------
//Source file: FirmVolumePostStationGroup
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
 * This defines a group of RoutingProperties needed for "Firm Volume Limit" routing for the supplied
 * BasePropertyKey. This contains 1 BaseProperty to set the value for volume.
 * 
 * The BasePropertyType for this group is RoutingPropertyTypeImpl.FIRM_VOLUME_LIMIT_POST_STATION.
 */
public class FirmVolumeComplexPostStationGroup extends FirmVolumeLimitGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.FIRM_VOLUME_LIMIT_COMPLEX_POST_STATION;

    public FirmVolumeComplexPostStationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public FirmVolumeComplexPostStationGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public FirmVolumeComplexPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public FirmVolumeComplexPostStationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public FirmVolumeComplexPostStationGroup(BasePropertyKey basePropertyKey,
                                             PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public FirmVolumeComplexPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber,
                                             List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    @Override
    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }
}