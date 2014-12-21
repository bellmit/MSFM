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
import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.common.DestinationListBaseProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;
import com.cboe.domain.routingProperty.BasePropertyValidationFactoryHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class DestinationListBasePropertyImpl extends AbstractBaseProperty implements DestinationListBaseProperty
{
    public static final int WORKSTATION_INDEX = DestinationBasePropertyImpl.WORKSTATION_INDEX;
    
    public static final String DESTINATION_LIST_CHANGE_EVENT = "DestinationListValue";
    private Destination[] destinations = new Destination[0];

    public DestinationListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                           BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
        setOptional(true);
    }

    public DestinationListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                           BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
        setOptional(true);
    }

    public DestinationListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                           BasePropertyType type, Destination[] list)
    {
        this(propertyCategory, propertyName, key, type);
        setDestinationListValue(list);
    }

    public DestinationListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                           BasePropertyType type, Destination[] list, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, validators);
        setDestinationListValue(list);
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public int compareTo(Object object)
    {
        Destination[] thierList = ((DestinationListBaseProperty) object).getDestinationListValue();
        boolean isEqual = Arrays.equals(destinations, thierList);
        return isEqual ? 0 : destinations.length >= thierList.length ? 1 : -1;
    }

    /**
     * Parses the field values from the passed String
     * @param value to parse
     */
    protected void decodeValue(String value)
    {
        if(value != null && value.length() > 0)
        {
            String[] listValues = BasicPropertyParser.parseArray(value);
            int numDestinations = listValues.length;
            ArrayList<Destination> destList = new ArrayList<Destination>(numDestinations);

            int k=0;
            for(int i=0; i<numDestinations; i++)
            {
                try
                {
                    Destination destination = new DestinationImpl(listValues[k + WORKSTATION_INDEX]);
                    destList.add(destination);
                    k += 1;
                }
                catch(IllegalArgumentException e)
                {
                    Log.exception(e);
                }
            }
            destinations = destList.toArray(new Destination[destList.size()]);
        }
        else
        {
            destinations = new Destination[0];
        }
    }

    /**
     * Get all the Property values as a List of Strings
     * @return List of Strings
     */
    protected List getEncodedValuesAsStringList()
    {
        List<String> valueList = new ArrayList<String>(destinations.length);
        int k = 0;
        for(int i = 0; i < destinations.length; i++)
        {
            valueList.add(k + WORKSTATION_INDEX, destinations[i].getWorkstation());
            k += 1;
        }

        return valueList;
    }

    public Destination[] getDestinationListValue()
    {
        return destinations;
    }

    public void setDestinationListValue(Destination[] newDestinations)
    {
        Destination[] oldValue = this.destinations;
        Destination[] newArray = new Destination[newDestinations.length];
        System.arraycopy(newDestinations, 0, newArray, 0, newDestinations.length);
        this.destinations = newArray;
        firePropertyChange(DESTINATION_LIST_CHANGE_EVENT, oldValue, this.destinations);
    }

    public Destination getDestinationListValue(int index)
    {
        return destinations[index];
    }

    public void setDestinationListValue(int index, Destination destination)
    {
        int size = destinations.length;

        if(index >= size)
        {
            Destination[] newArray = new Destination[size + 1];
            System.arraycopy(destinations, 0, newArray, 0, size);

            destinations = newArray;
            destinations[size] = destination;
        }
        else
        {
            destinations[index] = destination;
        }

        // TODO do we need this ? make it a method call
//        firePropertyChange(DESTINATION_LIST_CHANGE_EVENT, oldValue, this.destinations);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(255);
        buffer.append(getPropertyName()).append("=");
        for(int i = 0; i < destinations.length; i++)
        {
            buffer.append(destinations[i].toString());
            if(i < destinations.length - 1)
            {
                buffer.append(',');
            }
        }

        return buffer.toString();
    }

    @Override
    protected List<Validator> getDefaultValidators()
    {
        return BasePropertyValidationFactoryHome.find().createDestinationListValidators(getPropertyName());
    }
}
