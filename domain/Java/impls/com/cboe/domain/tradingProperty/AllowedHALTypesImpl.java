//
// -----------------------------------------------------------------------------------
// Source file: AllowedHALTypesImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.AllowedHALTypes;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Represents a TradingProperty for an individual allowed HAL Type.
 */
public class AllowedHALTypesImpl extends AbstractTradingProperty implements AllowedHALTypes, Comparable
{
    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AllowedHALTypesImpl(String sessionName, int classKey)
    {
        super(AllowedHALTypesGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the Property to initialize
     * the trading property data with.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public AllowedHALTypesImpl(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param allowedHALType The allowed type
     */
    public AllowedHALTypesImpl(String sessionName, int classKey, short allowedHALType)
    {
        this(sessionName, classKey);
        setAllowedHALType(allowedHALType);
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public AllowedHALTypesImpl(String sessionName, int classKey, String value)
    {
        super(AllowedHALTypesGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey, value);
    }

    /**
     * Overridden to return the auction type.
     */
    public int hashCode()
    {
        return getAllowedHALType();
    }

    public int compareTo(Object object)
    {
        int result;
        int myValue = getAllowedHALType();
        int theirValue = ((AllowedHALTypes) object).getAllowedHALType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overridden to return the HAL type.
     */
    public String getPropertyName()
    {
        return Short.toString(getAllowedHALType());
    }

    public short getAllowedHALType()
    {
        return(short) getInteger1();
    }

    public void setAllowedHALType(short allowedHALType)
    {
        setInteger1(allowedHALType);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return AllowedHALTypesGroup.TRADING_PROPERTY_TYPE;
    }
}
