package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;
// -----------------------------------------------------------------------------------
// Source file: RestrictedSeriesGroup
//
// PACKAGE: com.cboe.domain.routingProperty.properties
// 
// Created: Aug 23, 2006 10:29:48 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class RestrictedSeriesGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.RESTRICTED_SERIES;
    
    public static final String RSS = "RSS";

    private BooleanBaseProperty rss;
    
    public RestrictedSeriesGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public RestrictedSeriesGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public RestrictedSeriesGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public RestrictedSeriesGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public RestrictedSeriesGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                 List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public RestrictedSeriesGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(RSS))
        {
            return rss;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = rss;

        return properties;

    }

    protected void initializeProperties()
    {
        rss = new BooleanBasePropertyImpl(getPropertyCategoryType(), RSS, getPropertyKey(), getType());
    }

    public boolean isRss()
    {
        return rss.getBooleanValue();
    }

    public void setRss(boolean rss)
    {
        this.rss.setBooleanValue(rss);
        firePropertyChange();
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        RestrictedSeriesGroup newGroup = (RestrictedSeriesGroup) super.clone();
        newGroup.rss = (BooleanBaseProperty) rss.clone();

        return newGroup;
    }
}
