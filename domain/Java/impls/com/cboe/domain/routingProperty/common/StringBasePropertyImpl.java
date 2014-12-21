package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: StringBasePropertyImpl
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 27, 2006 10:44:49 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.StringBaseProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;


public class StringBasePropertyImpl extends AbstractBaseProperty implements StringBaseProperty
{
    public static final String STRING_CHANGE_EVENT = "StringValue";
    private String string = "";

    public StringBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                  BasePropertyType type)
    {
        this(propertyCategory, propertyName, key, type, "");
    }

    public StringBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                  BasePropertyType type, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, "", validators);
    }

    public StringBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                  BasePropertyType type, String string)
    {
        super(propertyCategory, propertyName, key, type);
        setStringValue(string);
    }

    public StringBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                  BasePropertyType type, String string, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
        setStringValue(string);
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public int compareTo(Object object)
    {
        String objectString = ((StringBaseProperty) object).getStringValue();
        return string.compareTo(objectString);
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
                this.string = elements[0];
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
        valueList.add(string);

        return valueList;
    }

    public String getStringValue()
    {
        return string;
    }

    public void setStringValue(String stringValue)
    {
        String oldValue = this.string;
        this.string = stringValue;
        firePropertyChange(STRING_CHANGE_EVENT, oldValue, stringValue);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(85);
        buffer.append(getPropertyName()).append("=");
        buffer.append(getStringValue());

        return buffer.toString();
    }
}
