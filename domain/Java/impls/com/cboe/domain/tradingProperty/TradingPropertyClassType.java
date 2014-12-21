//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyClassType.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.lang.reflect.Constructor;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Private class to encapsulate the trading property type and the associated class name of the implementation
 * responsible for that trading property.
 */
public class TradingPropertyClassType
{
    /**
     * Parameter set to try to find a constructor for.
     */
    private static final Class[] DEFAULT_CONSTRUCTOR_PARMS = {String.class, int.class};

    private TradingPropertyType tradingPropertyType;
    private Class classType;

    /**
     * Constructor that accepts DEFAULT_CONSTRUCTOR_PARMS as parameters, otherwise, if not found, null.
     */
    private Constructor constructor;

    /**
     * Constructor that verifies legal parameters.
     * @param tradingPropertyType that classType will be the implementation for. Must not be null.
     * @param classType that will be the implementation group responsible for tradingPropertyName. Must not be null and
     * must be able to be upcasted as a TradingPropertyGroup interface.
     * @throws IllegalArgumentException is thrown if tradingPropertyName is null or empty, or if classType is null or
     * not able to be upcasted as a TradingPropertyGroup interface.
     */
    protected TradingPropertyClassType(TradingPropertyType tradingPropertyType, Class classType)
    {
        super();
        if(tradingPropertyType == null)
        {
            throw new IllegalArgumentException("tradingPropertyName may not be null.");
        }
        if(classType == null || !TradingPropertyGroup.class.isAssignableFrom(classType))
        {
            throw new IllegalArgumentException("classType must be a TradingPropertyGroup.");
        }

        this.classType = classType;
        this.tradingPropertyType = tradingPropertyType;

        //doing it here caches it for later instantiation so that reflection does not impact getters.
        try
        {
            constructor = classType.getConstructor(DEFAULT_CONSTRUCTOR_PARMS);
        }
        catch(NoSuchMethodException e)
        {
            //constructor will stay null. affected code can use newInstance of classType.
        }
    }

    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof TradingPropertyClassType))
        {
            return false;
        }

        final TradingPropertyClassType tradingPropertyClassType = (TradingPropertyClassType) o;

        if(!tradingPropertyType.equals(tradingPropertyClassType.tradingPropertyType))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return tradingPropertyType.hashCode();
    }

    /**
     * Gets the class that will be the implementation responsible for tradingPropertyName
     * @return guaranteed to be an implementation of the TradingPropertyGroup interface.
     */
    public Class getClassType()
    {
        return classType;
    }

    /**
     * Gets the tradingPropertyName that classType will be the implementation for
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * If a constructor that accepts only (String, int) was found, it is returned. Otherwise, null is returned.
     */
    public Constructor getConstructor()
    {
        return constructor;
    }
}
