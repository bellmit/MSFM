package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: EligiblePDPMGroup
//
// PACKAGE: com.cboe.domain.routingProperty.properties
// 
// Created: Aug 4, 2006 3:09:05 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

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

public class EligibleCOAGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.COA_ELIGIBILITY;

    public static final String COA = "COA";

    private BooleanBaseProperty coa;

    public EligibleCOAGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public EligibleCOAGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public EligibleCOAGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public EligibleCOAGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public EligibleCOAGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                            List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public EligibleCOAGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }


    protected void initializeProperties()
    {
        coa = new BooleanBasePropertyImpl(getPropertyCategoryType(), COA, getPropertyKey(), getType());
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(COA))
        {
            return coa;
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
        properties[0] = coa;

        return properties;
    }

    public boolean isCOA()
    {
        return coa.getBooleanValue();
    }

    public void setCOA(boolean flag)
    {
        coa.setBooleanValue(flag);
        firePropertyChange();
    }

    public Object clone() throws CloneNotSupportedException
    {
        EligibleCOAGroup newGroup = (EligibleCOAGroup) super.clone();
        newGroup.coa = (BooleanBaseProperty) coa.clone();

        return newGroup;
    }
}
