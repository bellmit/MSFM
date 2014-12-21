//
// -----------------------------------------------------------------------------------
// Source file: EligibleSeriesGroup.java
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;

/**
 * BasePropertyGroup that contains one BooleanBaseProperty for RSSEligible property.
 */
@Deprecated
public class EligibleSeriesGroup extends AbstractRoutingPropertyGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = null;  // was: RoutingPropertyTypeImpl.ELIGIBLE_SERIES;

    public static final String RSS_ELIGIBLE = "RSSEligible";

    private BooleanBaseProperty rssEligible;

    public EligibleSeriesGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public EligibleSeriesGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public EligibleSeriesGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public EligibleSeriesGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public EligibleSeriesGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                               List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public EligibleSeriesGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name)
            throws DataValidationException
    {
        if(name.equals(RSS_ELIGIBLE))
        {
            return rssEligible;
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
        properties[0] = rssEligible;

        return properties;

    }

    protected void initializeProperties()
    {
        rssEligible = new BooleanBasePropertyImpl(getPropertyCategoryType(), RSS_ELIGIBLE, getPropertyKey(), getType());
    }

    public boolean isRssEligible()
    {
        return rssEligible.getBooleanValue();
    }

    public void setRss(boolean rssEligible)
    {
        this.rssEligible.setBooleanValue(rssEligible);
        firePropertyChange();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        EligibleSeriesGroup newGroup = (EligibleSeriesGroup) super.clone();
        newGroup.rssEligible = (BooleanBaseProperty) rssEligible.clone();

        return newGroup;
    }
}
