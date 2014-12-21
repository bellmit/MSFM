package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: BasePropertyTypeImpl
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Jul 21, 2006 1:27:28 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKeyType;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class BasePropertyTypeImpl implements BasePropertyType
{
    private String name;
    private String fullName;
    private String propertyCategory;
    private BasePropertyKeyType propertyKeyType;
    private int[][] masks;

    protected static Map<String, BasePropertyType> allPropertyTypes = new HashMap<String, BasePropertyType>(50);

    /**
     * Constructor
     * @param propertyCategory to use
     * @param name programmatic name
     * @param fullName full english name for display trading session
     */
    protected BasePropertyTypeImpl(String propertyCategory, String name, String fullName, BasePropertyKeyType propertyKeyType)
    {
        setName(name);
        setFullName(fullName);
        setPropertyCategory(propertyCategory);
        setPropertyKeyType(propertyKeyType);        
    }
    
    /**
     * Constructor
     * @param propertyCategory to use
     * @param name programmatic name
     * @param fullName full english name for display trading session
     */
    protected BasePropertyTypeImpl(String propertyCategory, String name, String fullName, BasePropertyKeyType propertyKeyType,int[][] masks)
    {
        setName(name);
        setFullName(fullName);
        setPropertyCategory(propertyCategory);
        setPropertyKeyType(propertyKeyType);
        setMasks(masks);
    }

    public static BasePropertyType findPropertyType(String propertyName)
    {
        return allPropertyTypes.get(propertyName);
    }

    /**
     * Compares equality based on getName().
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof BasePropertyType))
        {
            return false;
        }

        final BasePropertyType routingPropertyType = (BasePropertyType) o;

        if (!getName().equals(routingPropertyType.getName()))
        {
            return false;
        }

        return true;
    }

    /**
     * Returns the hashCode of getName();
     * @return hashcode
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /**
     * Returns the programmatic name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the English formatted name
     */
    public String getFullName()
    {
        return fullName;
    }

    public String toString()
    {
        return getFullName();
    }

    public BasePropertyKeyType getKeyType()
    {
        return propertyKeyType;
    }

    public int[][] getMasks()    
    {
        return masks;
    }
    /**
     * Gets the property category that will contain this type.
     */
    public String getPropertyCategory()
    {
        return propertyCategory;
    }

    private void setName(String name)
    {
        this.name = name;
    }

    private void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    private void setPropertyCategory(String propertyCategory)
    {
        this.propertyCategory = propertyCategory;
    }

    private void setPropertyKeyType(BasePropertyKeyType propertyKeyType)
    {
        this.propertyKeyType = propertyKeyType;
    }
    
    private void setMasks(int[][] masks)
    {
        this.masks = masks;
    }
}
