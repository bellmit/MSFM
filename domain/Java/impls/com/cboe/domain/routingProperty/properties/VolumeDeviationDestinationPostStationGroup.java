//
//-----------------------------------------------------------------------------------
//Source file: VolumeDeviationDestinationPostStationGroup.java
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

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

/**
 * This is a VolumeDeviationDestinationGroup that has has the BasePropertyType "FirmCorrBranchOMT".
 */
public abstract class VolumeDeviationDestinationPostStationGroup extends VolumeDeviationDestinationGroup
{   
    public VolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public VolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public VolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public VolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey, List<Validator> validators
    )
    {
        super(basePropertyKey, validators);
    }

    public VolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey,
                                                      PropertyServicePropertyGroup propertyGroup,
                                                      List<Validator> validators
    )
            throws DataValidationException,
                   InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public VolumeDeviationDestinationPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber,
                                                      List<Validator> validators
    )
    {
        super(basePropertyKey, versionNumber, validators);
    }
}