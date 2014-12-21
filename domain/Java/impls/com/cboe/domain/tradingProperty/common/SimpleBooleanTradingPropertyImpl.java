//
// -----------------------------------------------------------------------------------
// Source file: SimpleBooleanTradingPropertyImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty.common;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty.common;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.tradingProperty.SimpleBooleanTradingProperty;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;

/**
 * Represents a TradingProperty for a single simple boolean
 */
public class SimpleBooleanTradingPropertyImpl extends AbstractTradingProperty
        implements SimpleBooleanTradingProperty, Comparable
{
    protected TradingPropertyType tradingPropertyType;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public SimpleBooleanTradingPropertyImpl(String propertyName, String sessionName, int classKey)
    {
        super(propertyName, sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the Property to initialize
     * the trading property data with.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public SimpleBooleanTradingPropertyImpl(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param booleanValue boolean for the trading property
     */
    public SimpleBooleanTradingPropertyImpl(String propertyName, String sessionName, int classKey, boolean booleanValue)
    {
        this(propertyName, sessionName, classKey);
        setBooleanValue(booleanValue);
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public SimpleBooleanTradingPropertyImpl(String propertyName, String sessionName, int classKey, String value)
    {
        super(propertyName, sessionName, classKey, value);
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

    /**
     * Compares this object with the passed object based on the boolean value.
     */
    public int compareTo(Object object)
    {
        int result;
        boolean myValue = getBooleanValue();
        boolean theirValue = ((SimpleBooleanTradingProperty) object).getBooleanValue();
        result = (myValue == theirValue ? 0 : (myValue ? 1 : -1));
        return result;
    }

    /**
     * Gets the boolean for the trading property
     */
    public boolean getBooleanValue()
    {
        int value = getInteger1();
        return convertInt(value);
    }

    /**
     * Sets the boolean for the trading property
     */
    public void setBooleanValue(boolean booleanValue)
    {
        setInteger1(convertBoolean(booleanValue));
    }

    /**
     * Gets the TradingPropertyType for this group that identifies the type of this group.
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Allows the TradingPropertyType to be set for this TradingPropertyGroup since this implements a default container
     * for a simple boolean.
     */
    public void setTradingPropertyType(TradingPropertyType tradingPropertyType)
    {
        this.tradingPropertyType = tradingPropertyType;
    }
}
