package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: IntegerBasePropertyImpl
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 26, 2006 4:01:59 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.IntegerBaseProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;


public class IntegerBasePropertyImpl extends AbstractBaseProperty implements IntegerBaseProperty, Comparable
{
    public static final String INTEGER_CHANGE_EVENT = "IntegerValue";
    private int intValue = 0;

    public IntegerBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
    }

    public IntegerBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
    }

    public IntegerBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type, int intValue)
    {
        this(propertyCategory, propertyName, key, type);
        setIntegerValue(intValue);
    }

    public IntegerBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type, int intValue, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, validators);
        setIntegerValue(intValue);
    }

    public int getIntegerValue()
    {
        return intValue;
    }

    public void setIntegerValue(int integerValue)
    {
        int oldValue = intValue;
        intValue = integerValue;
        firePropertyChange(INTEGER_CHANGE_EVENT, oldValue, intValue);
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public int compareTo(Object object)
    {
        int result;
        int otherValue = ((com.cboe.interfaces.domain.routingProperty.common.IntegerBaseProperty) object).getIntegerValue();
        result = (getIntegerValue() < otherValue ? -1 : (getIntegerValue() == otherValue ? 0 : 1));
        return result;
    }

    /**
     * Get all the Property values aas a List of Strings
     * @return List of Strings
     */
    protected List getEncodedValuesAsStringList()
    {
        List valueList = new ArrayList(1);
        valueList.add(new Integer(intValue));

        return valueList;
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
                this.intValue = Integer.parseInt(elements[0]);
            }
        }
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(55);
        buffer.append(getPropertyName()).append("=");
        buffer.append(getIntegerValue());

        return buffer.toString();
    }
}
