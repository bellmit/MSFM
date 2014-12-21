//
// -----------------------------------------------------------------------------------
// Source file: DestinationListBasePropertyImpl.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.VolumeDevDestination;
import com.cboe.interfaces.domain.routingProperty.common.VolumeDevDestinationBaseProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class VolumeDevDestinationBasePropertyImpl extends AbstractBaseProperty implements VolumeDevDestinationBaseProperty
{
    
    public static final int VOLUME_INDEX = 0;
    public static final int DEVIATION_INDEX = 1;
    public static final int WORKSTATION_INDEX = 2;
     
     /**
      * DESTINATION_CHANGE_EVENT is the property name used when PropertyChangeEvents are fired
      */
     public static final String DESTINATION_CHANGE_EVENT = "VolumeDevDestinationValue";

     private VolumeDevDestination destination;

     public VolumeDevDestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                        BasePropertyType type)
     {
         this(propertyCategory, propertyName, key, type, 0, "", "");
     }

     public VolumeDevDestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                           BasePropertyType type, int volume, String deviation, String workstation)
     {
         this(propertyCategory, propertyName, key, type, new VolumeDevDestinationImpl(volume, deviation, workstation));
     }

    public VolumeDevDestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                                BasePropertyType type, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, 0, "", "", validators);
    }

    public VolumeDevDestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                                BasePropertyType type, int volume, String deviation, String workstation,
                                                List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, new VolumeDevDestinationImpl(volume, deviation, workstation),
             validators);
    }

    public VolumeDevDestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                                BasePropertyType type, VolumeDevDestination destination)
    {
        super(propertyCategory, propertyName, key, type);
        setVolumeDevDestination(destination);
    }

    public VolumeDevDestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                                BasePropertyType type, VolumeDevDestination destination,
                                                List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
        setVolumeDevDestination(destination);
    }

    public int compareTo(Object object)
     {
         return destination.toString().compareTo(object.toString());
     }

     /**
      * Parses the field values from the passed String
      * @param value to parse
      */
     protected void decodeValue(String value)
     {
         if(value != null && value.length() > 0)
         {
             String[] parts = BasicPropertyParser.parseArray(value);
             destination = new VolumeDevDestinationImpl(Integer.parseInt(parts[VOLUME_INDEX]),
                                                   parts[DEVIATION_INDEX],
                                                   parts[WORKSTATION_INDEX]);
         }

     }

     /**
      * Get all the Property values as a List of Strings
      * @return List of Strings
      */
     protected List getEncodedValuesAsStringList()
     {
         List<String> valueList = new ArrayList<String>(3);
         valueList.add(VOLUME_INDEX, Integer.toString(destination.getVolume()));
         valueList.add(DEVIATION_INDEX, destination.getDeviation());
         valueList.add(WORKSTATION_INDEX, destination.getWorkstation());
         return valueList;
     }

     public VolumeDevDestination getVolumeDevDestination()
     {
         return destination;
     }

     public void setVolumeDevDestination(VolumeDevDestination destination)
     {
         VolumeDevDestination oldValue = this.destination;
         this.destination = destination;
         firePropertyChange(DESTINATION_CHANGE_EVENT, oldValue, destination);
     }

     public String toString()
     {
         StringBuffer buffer = new StringBuffer();
         buffer.append(getPropertyName()).append("=");
         buffer.append(destination.toString());
         return buffer.toString();
     }

    public String getWorkstation()
    {
        return destination.getWorkstation();
    }

    public void setWorkstation(String workstation)
    {
        VolumeDevDestination oldValue = destination;
        destination.setWorkstation(workstation);
        firePropertyChange(DESTINATION_CHANGE_EVENT, oldValue, destination);
    }

    public String getDeviation()
    {
        return destination.getDeviation();
    }

    public void setDeviation(String deviation)
    {
        VolumeDevDestination oldValue = destination;
        destination.setDeviation(deviation);
        firePropertyChange(DESTINATION_CHANGE_EVENT, oldValue, destination);
    }

    public int getVolume()
    {
        return destination.getVolume();
    }

    public void setVolume(int volume)
    {
        VolumeDevDestination oldValue = destination;
        destination.setVolume(volume);
        firePropertyChange(DESTINATION_CHANGE_EVENT, oldValue, destination);
    }
}
