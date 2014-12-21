package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: BasePropertyClassType
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Jul 25, 2006 9:08:17 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.Constructor;
import java.util.List;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;

/**
 * Private class to encapsulate the firm routing property type and the associated class name of the implementation
 * responsible for that firm routing property.
 */
public class BasePropertyClassType
{
    /**
     * Parameter set to try to find a constructor for.
     */
    private static final Class[] DEFAULT_CONSTRUCTOR_PARMS = {BasePropertyKey.class};
    private static final Class[] CONSTRUCTOR_WINT_VALIDATOR_PARMS = {BasePropertyKey.class, List.class};

    private BasePropertyType propertyType;
    private Class classType;

    /**
     * Constructor that accepts DEFAULT_CONSTRUCTOR_PARMS as parameters, otherwise, if not found, null.
     */
    private Constructor constructor;
    private Constructor constructorWithValidator;

    /**
     * Constructor that verifies legal parameters.
     * @param propertyType that classType will be the implementation for. Must not be null.
     * @param classType that will be the implementation group responsible for firmRoutingPropertyName. Must not be null
     * and must be able to be upcasted as a BasePropertyGroup interface.
     * @throws IllegalArgumentException is thrown if firmRoutingPropertyName is null or empty, or if classType is null
     * or not able to be upcasted as a BasePropertyGroup interface.
     */
    protected BasePropertyClassType(BasePropertyType propertyType, Class classType)
    {
        super();
        if (propertyType == null)
        {
            throw new IllegalArgumentException("PropertyName may not be null.");
        }
        if (classType == null || !BasePropertyGroup.class.isAssignableFrom(classType))
        {
            throw new IllegalArgumentException("classType must be a BasePropertyGroup.");
        }

        this.classType = classType;
        this.propertyType = propertyType;

        //doing it here caches it for later instantiation so that reflection does not impact getters.
        try
        {
            constructor = classType.getConstructor(DEFAULT_CONSTRUCTOR_PARMS);
            constructorWithValidator = classType.getConstructor(CONSTRUCTOR_WINT_VALIDATOR_PARMS);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("BasePropertyGroup Missing Constructor: " + e.getMessage());
        }
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof BasePropertyClassType))
        {
            return false;
        }

        final BasePropertyClassType firmPropertyClassType = (BasePropertyClassType) o;

        if (!propertyType.equals(firmPropertyClassType.propertyType))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return propertyType.hashCode();
    }

    /**
     * Gets the class that will be the implementation responsible for firmRoutingPropertyName
     * @return guaranteed to be an implementation of the BasePropertyGroup interface.
     */
    public Class getClassType()
    {
        return classType;
    }

    /**
     * Gets the firmRoutingPropertyName that classType will be the implementation for
     */
    public BasePropertyType getPropertyType()
    {
        return propertyType;
    }

    /**
     * If a constructor that accepts only (String, int) was found, it is returned. Otherwise, null is returned.
     */
    public Constructor getConstructor()
    {
        return constructor;
    }

    public Constructor getConstructorWithValidator()
    {
        return constructorWithValidator;
    }
}
