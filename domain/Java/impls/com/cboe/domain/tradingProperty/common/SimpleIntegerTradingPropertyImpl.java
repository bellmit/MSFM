//
// -----------------------------------------------------------------------------------
// Source file: SimpleIntegerTradingPropertyImpl.java
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
import com.cboe.interfaces.domain.tradingProperty.SimpleIntegerTradingProperty;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;

/**
 * Represents a TradingProperty for a single simple integer
 */
public class SimpleIntegerTradingPropertyImpl extends AbstractTradingProperty
        implements SimpleIntegerTradingProperty, Comparable
{
    protected TradingPropertyType tradingPropertyType;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public SimpleIntegerTradingPropertyImpl(String propertyName, String sessionName, int classKey)
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
    public SimpleIntegerTradingPropertyImpl(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param integerValue integer for the trading property
     */
    public SimpleIntegerTradingPropertyImpl(String propertyName, String sessionName, int classKey, int integerValue)
    {
        this(propertyName, sessionName, classKey);
        setIntegerValue(integerValue);
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public SimpleIntegerTradingPropertyImpl(String propertyName, String sessionName, int classKey, String value)
    {
        super(propertyName, sessionName, classKey, value);
    }

    /**
     * Compares this object with the passed object based on the integer value.
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getIntegerValue();
        int theirValue = ((SimpleIntegerTradingProperty) object).getIntegerValue();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Gets the integer for the trading property
     */
    public int getIntegerValue()
    {
        return getInteger1();
    }

    /**
     * Sets the integer for the trading property
     */
    public void setIntegerValue(int integerValue)
    {
        setInteger1(integerValue);
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
     * for a simple integer.
     */
    public void setTradingPropertyType(TradingPropertyType tradingPropertyType)
    {
        this.tradingPropertyType = tradingPropertyType;
    }
}
