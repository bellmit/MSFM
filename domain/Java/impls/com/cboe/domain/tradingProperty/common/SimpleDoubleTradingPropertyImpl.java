//
// -----------------------------------------------------------------------------------
// Source file: SimpleDoubleTradingPropertyImpl.java
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
import com.cboe.interfaces.domain.tradingProperty.SimpleDoubleTradingProperty;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;

/**
 * Represents a TradingProperty for a single simple double
 */
public class SimpleDoubleTradingPropertyImpl extends AbstractTradingProperty
        implements SimpleDoubleTradingProperty, Comparable
{
    protected TradingPropertyType tradingPropertyType;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public SimpleDoubleTradingPropertyImpl(String propertyName, String sessionName, int classKey)
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
    public SimpleDoubleTradingPropertyImpl(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param doubleValue double for the trading property
     */
    public SimpleDoubleTradingPropertyImpl(String propertyName, String sessionName, int classKey, double doubleValue)
    {
        this(propertyName, sessionName, classKey);
        setDoubleValue(doubleValue);
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public SimpleDoubleTradingPropertyImpl(String propertyName, String sessionName, int classKey, String value)
    {
        super(propertyName, sessionName, classKey, value);
    }

    /**
     * Compares this object with the passed object based on the double value.
     */
    public int compareTo(Object object)
    {
        int result;
        double myValue = getDoubleValue();
        double theirValue = ((SimpleDoubleTradingProperty) object).getDoubleValue();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Gets the double for the trading property
     */
    public double getDoubleValue()
    {
        return getDouble1();
    }

    /**
     * Sets the double for the trading property
     */
    public void setDoubleValue(double doubleValue)
    {
        setDouble1(doubleValue);
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
     * for a simple double.
     */
    public void setTradingPropertyType(TradingPropertyType tradingPropertyType)
    {
        this.tradingPropertyType = tradingPropertyType;
    }
}
