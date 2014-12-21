//
//-----------------------------------------------------------------------------------
//Source file: BuyWriteOrderVolumeDeviationDestinationPostStationGroup.java
//
//PACKAGE: com.cboe.domain.routingProperty.properties
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;

/**
 * This is a VolumeDeviationDestinationGroup that has has the BasePropertyType
 * "BuyWriteOrderVolumeDeviationDestinationGroup".
 */
public class BuyWriteOrderVolumeDeviationDestinationPostStationGroup extends VolumeDeviationDestinationPostStationGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.VOLUME_DEV_DESINATION_CHECK_BUYWRITE_DN_POST_STATION;

    public BuyWriteOrderVolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public BuyWriteOrderVolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public BuyWriteOrderVolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey,
            int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public BuyWriteOrderVolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey,
                                                                   List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public BuyWriteOrderVolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey,
                                                                   PropertyServicePropertyGroup propertyGroup,
                                                                   List<Validator> validators)
            throws DataValidationException,
                   InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public BuyWriteOrderVolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey,
                                                                   int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

}