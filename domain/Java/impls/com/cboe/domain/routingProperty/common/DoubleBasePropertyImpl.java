package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: DoubleBasePropertyImpl
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 27, 2006 9:27:41 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.DoubleBaseProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;

public class DoubleBasePropertyImpl extends AbstractBaseProperty implements DoubleBaseProperty, Comparable
{
    public static final String DOUBLE_CHANGE_EVENT = "DoubleValue";
    private double doubleValue = 0.0;

    public DoubleBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                  BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
    }

    public DoubleBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                  BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
    }

    public DoubleBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                  BasePropertyType type, double doubleValue)
    {
        super(propertyCategory, propertyName, key, type);
        setDoubleValue(doubleValue);
    }

    public DoubleBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                  BasePropertyType type, double doubleValue, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, validators);
        setDoubleValue(doubleValue);
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public int compareTo(Object object)
    {
        int result;
        double otherValue = ((DoubleBaseProperty) object).getDoubleValue();
        result = (getDoubleValue() < otherValue ? -1 : (getDoubleValue() == otherValue ? 0 : 1));
        return result;
    }

    /**
     * Parses the field values from the passed String
     * @param value to parse
     */
    protected void decodeValue(String value)
    {
        if (value != null && value.length() > 0)
        {
            String[] elements = BasicPropertyParser.parseArray(value);
            if (elements.length > 0)
            {
                this.doubleValue = Double.parseDouble(elements[0]);
            }
        }
    }

    /**
     * Get all the Property values as a List of Strings
     * @return List of Strings
     */
    protected List getEncodedValuesAsStringList()
    {
        List valueList = new ArrayList(1);
        valueList.add(new Double(doubleValue));

        return valueList;
    }

    public double getDoubleValue()
    {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue)
    {
        double oldValue = this.doubleValue;
        this.doubleValue = doubleValue;
        firePropertyChange(DOUBLE_CHANGE_EVENT, oldValue, doubleValue);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(55);
        buffer.append(getPropertyName()).append("=");
        buffer.append(getDoubleValue());

        return buffer.toString();
    }
}
