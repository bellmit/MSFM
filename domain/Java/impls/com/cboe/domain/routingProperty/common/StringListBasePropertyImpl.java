package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: StringListBasePropertyImpl
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 27, 2006 10:53:25 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.StringListBaseProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;


public class StringListBasePropertyImpl extends AbstractBaseProperty implements StringListBaseProperty
{
    public static final String STRING_LIST_CHANGE_EVENT = "StringListValue";
    private String[] listValues = new String[0];

    public StringListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                      BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
    }

    public StringListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                      BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
    }

    public StringListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                      BasePropertyType type, String[] list)
    {
        this(propertyCategory, propertyName, key, type);
        setStringListValue(list);
    }

    public StringListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                      BasePropertyType type, String[] list, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, validators);
        setStringListValue(list);
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public int compareTo(Object object)
    {
        String[] thierList = ((StringListBaseProperty) object).getStringListValue();

        boolean isEqual = Arrays.equals(listValues, thierList);
        return isEqual ? 0 : listValues.length >= thierList.length ? 1 : -1;
    }

    /**
     * Parses the field values from the passed String
     * @param value to parse
     */
    protected void decodeValue(String value)
    {
        if (value != null && value.length() > 0)
        {
            listValues = BasicPropertyParser.parseArray(value);
        }
    }

    /**
     * Get all the Property values as a List of Strings
     * @return List of Strings
     */
    protected List getEncodedValuesAsStringList()
    {
        List valueList = new ArrayList(listValues.length);
        for (int i = 0; i < listValues.length; i++)
        {
            valueList.add(listValues[i]);
        }

        return valueList;
    }

    public String[] getStringListValue()
    {
        return listValues;
    }

    public void setStringListValue(String[] stringListValue)
    {
        String[] oldValue = listValues;
        String[] newArray = new String[stringListValue.length];
        System.arraycopy(stringListValue, 0, newArray, 0, stringListValue.length);
        listValues = newArray;
        firePropertyChange(STRING_LIST_CHANGE_EVENT, oldValue, listValues);
    }

    public String getStringListValue(int index)
    {
        return listValues[index];
    }

    public void setStringListValue(int index, String stringValue)
    {
        int size = listValues.length;

        if (index >= size)
        {
            String[] newArray = new String[size + 1];
            System.arraycopy(listValues, 0, newArray, 0, size);

            listValues = newArray;
            listValues[size] = stringValue;
        }
        else
        {
            listValues[index] = stringValue;
        }
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(255);
        buffer.append(getPropertyName()).append("=");
        for (int i = 0; i < listValues.length; i++)
        {
            buffer.append(listValues[i]);
            if (i < listValues.length - 1)
            {
                buffer.append(',');
            }
        }

        return buffer.toString();
    }
}
