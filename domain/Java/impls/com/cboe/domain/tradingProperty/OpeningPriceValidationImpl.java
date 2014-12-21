//
// -----------------------------------------------------------------------------------
// Source file: OpeningPriceValidationImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.OpeningPriceValidation;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Represents a TradingProperty for Opening Price Validation.
 */
public class OpeningPriceValidationImpl extends AbstractTradingProperty
        implements OpeningPriceValidation
{
    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public OpeningPriceValidationImpl(String sessionName, int classKey)
    {
        super(OpeningPriceValidationGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the Property to initialize
     * the trading property data with.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public OpeningPriceValidationImpl(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param validationType The validation type to use
     */
    public OpeningPriceValidationImpl(String sessionName, int classKey, short validationType)
    {
        this(sessionName, classKey);
        setValidationType(validationType);
        
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public OpeningPriceValidationImpl(String sessionName, int classKey, String value)
    {
        super(OpeningPriceValidationGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey, value);
    }

    /**
     * Compares based on getValidationType()
     */
    public int compareTo(Object object)
    {
        int result;
        short myValue = getValidationType();
        short theirValue = ((OpeningPriceValidation) object).getValidationType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getValidationType()
     */
    public int hashCode()
    {
        return getValidationType();
    }
    
    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return OpeningPriceValidationGroup.TRADING_PROPERTY_TYPE;
    }

    /**
     * Overriden to return the getValidationType() as a String
     */
    public String getPropertyName()
    {
        return Integer.toString(getValidationType());
    }

    /**
     * Returns the super's integer1 attribute
     */
    public short getValidationType()
    {
        return (short)getInteger1();
    }

    /**
     * Sets the super's integer1 attribute
     */
    public void setValidationType(short type)
    {
        setInteger1(type);
    }
}
