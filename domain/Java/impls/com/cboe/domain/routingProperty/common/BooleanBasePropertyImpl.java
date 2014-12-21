package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: BooleanBasePropertyImpl
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 27, 2006 10:27:45 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;


public class BooleanBasePropertyImpl extends AbstractBaseProperty implements BooleanBaseProperty
{
    public static final String BOOLEAN_CHANGE_EVENT = "BooleanValue";
    private int value;

    public BooleanBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
    }

    public BooleanBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
    }

    public BooleanBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key, boolean value,
                                   BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
        setBooleanValue(value);
    }

    public BooleanBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key, boolean value,
                                   BasePropertyType type, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, validators);
        setBooleanValue(value);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(55);
        buffer.append(getPropertyName()).append("=");
        buffer.append(String.valueOf(convertInt(value)));

        return buffer.toString();
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public int compareTo(Object object)
    {
        int result;
        boolean thisValue = getBooleanValue();
        boolean objectValue = ((com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty) object).getBooleanValue();
        result = (thisValue == objectValue ? 0 : (thisValue ? 1 : -1));
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
                this.value = Integer.parseInt(elements[0]);
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
        valueList.add(new Integer(value));

        return valueList;
    }

    public boolean getBooleanValue()
    {
        return convertInt(value);
    }

    public void setBooleanValue(boolean booleanValue)
    {
        boolean oldValue = convertInt(value);
        value = convertBoolean(booleanValue);
        firePropertyChange(BOOLEAN_CHANGE_EVENT, oldValue, booleanValue);
    }

    /**
     * Converts a boolean value to an int for internal storage
     * @param value boolean to convert
     * @return 1 if value is true, 0 if value is false
     */
    public static int convertBoolean(boolean value)
    {
        return value ? 1 : 0;
    }

    /**
     * Converts an int value to a boolean for internal storage
     * @param value int to convert
     * @return false if value is 0, otherwise true
     */
    public static boolean convertInt(int value)
    {
        return (value == 0 ? false : true);
    }
}
