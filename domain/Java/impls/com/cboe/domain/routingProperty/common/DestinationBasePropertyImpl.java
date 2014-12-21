//
// -----------------------------------------------------------------------------------
// Source file: DestinationBasePropertyImpl.java
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
import com.cboe.interfaces.domain.routingProperty.common.DestinationBaseProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;
import com.cboe.domain.routingProperty.BasePropertyValidationFactoryHome;

public class DestinationBasePropertyImpl extends AbstractBaseProperty implements DestinationBaseProperty
{
    public static final int WORKSTATION_INDEX = 0;
    
    /**
     * DESTINATION_CHANGE_EVENT is the property name used when PropertyChangeEvents are fired
     */
    public static final String DESTINATION_CHANGE_EVENT = "DestinationValue";

    private Destination destination;

    public DestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                       BasePropertyType type)
    {
        this(propertyCategory, propertyName, key, type, "");
    }

    public DestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                       BasePropertyType type, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, "", validators);
    }

    public DestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                          BasePropertyType type, String workstation)
    {
        this(propertyCategory, propertyName, key, type, new DestinationImpl(workstation));
    }

    public DestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                       BasePropertyType type, String workstation, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, new DestinationImpl(workstation), validators);
    }

    public DestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                       BasePropertyType type, Destination destination)
    {
        super(propertyCategory, propertyName, key, type);
        setDestination(destination);
    }

    public DestinationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                       BasePropertyType type, Destination destination, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
        setDestination(destination);
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
            destination = new DestinationImpl(parts[WORKSTATION_INDEX]);
        }
    }

    /**
     * Get all the Property values as a List of Strings
     * @return List of Strings
     */
    protected List getEncodedValuesAsStringList()
    {
        List<String> valueList = new ArrayList<String>(2);
        valueList.add(WORKSTATION_INDEX, destination.getWorkstation());        

        return valueList;
    }

    public Destination getDestination()
    {
        return destination;
    }

    public void setDestination(Destination destination)
    {
        Destination oldValue = this.destination;
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

    @Override
    protected List<Validator> getDefaultValidators()
    {
        return BasePropertyValidationFactoryHome.find().createDestinationValidators(getPropertyName());
    }
}
