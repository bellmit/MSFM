//
//-----------------------------------------------------------------------------------
//Source file: VolumeDeviationDestinationGroup.java
//
//PACKAGE: com.cboe.domain.routingProperty.properties
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.common.IntegerBasePropertyImpl;
import com.cboe.domain.routingProperty.common.VolumeDevDestinationBasePropertyImpl;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * This is a VolumeDeviationDestinationGroup that has has the BasePropertyType "FirmCorrBranchOMT".
 */
public abstract class VolumeDeviationDestinationGroup extends AbstractRoutingPropertyGroup
{   

    public static final String WORKSTATION = "Workstation";
    public static final String DEVIATION   = "Deviation";
    public static final String VOLUME      = "Volume";

    protected DeviationWorkstationParameter workstation;
    protected VolumeDeviationParameter deviation;
    protected IntegerBasePropertyImpl  volume;


    public VolumeDeviationDestinationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public VolumeDeviationDestinationGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public VolumeDeviationDestinationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public VolumeDeviationDestinationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public VolumeDeviationDestinationGroup(BasePropertyKey basePropertyKey,
                                           PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public VolumeDeviationDestinationGroup(BasePropertyKey basePropertyKey, int versionNumber,
                                           List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BaseProperty getProperty(String name)
        throws DataValidationException
    {
        if(name.equals(WORKSTATION))
        {
            return workstation;
        }
        else if(name.equals(DEVIATION))
        {
            return deviation;
        }
        else if(name.equals(VOLUME))
        {
            return volume;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                   ". Could not find class type to handle.", 0);
        }
    }
    
    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[3];
        properties[VolumeDevDestinationBasePropertyImpl.VOLUME_INDEX     ] = volume;
        properties[VolumeDevDestinationBasePropertyImpl.DEVIATION_INDEX  ] = deviation;
        properties[VolumeDevDestinationBasePropertyImpl.WORKSTATION_INDEX] = workstation;

        return properties;
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        VolumeDeviationDestinationGroup newGroup = (VolumeDeviationDestinationGroup) super.clone();
        newGroup.volume      = (IntegerBasePropertyImpl ) volume.clone();
        newGroup.deviation   = (VolumeDeviationParameter) deviation.clone();
        newGroup.workstation = (DeviationWorkstationParameter  ) workstation.clone();

        return newGroup;
    }

    // convenience methods
    public String getWorkstation()
    {
        return workstation.getDeviationWorkstation();
    }

    public String getDeviation()
    {
        return deviation.getVolumeDeviation();
    }

    public int getVolume()
    {
        return volume.getIntegerValue();
    }
    
    protected void initializeProperties()
    {
        //workstation = new DeviationWorkstationParameter (getPropertyCategoryType(), WORKSTATION, getPropertyKey(), getType());
        volume      = new IntegerBasePropertyImpl(getPropertyCategoryType(), VOLUME     , getPropertyKey(), getType());

        deviation = new VolumeDeviationParameter(getPropertyCategoryType(), DEVIATION,getPropertyKey(), getType());

        workstation = new DeviationWorkstationParameter(getPropertyCategoryType(), WORKSTATION,getPropertyKey(), getType());
        try
        {
            deviation.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + DEVIATION + " category="
                          + getPropertyCategoryType() + "type=" + getType(), e);
        }

        try
        {
            workstation.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + WORKSTATION + " category=" +
                          getPropertyCategoryType() + "type=" + getType(), e);
        }

    }
} 