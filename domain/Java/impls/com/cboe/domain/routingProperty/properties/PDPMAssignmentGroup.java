package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: PDPMAssignmentGroup
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
import com.cboe.interfaces.domain.routingProperty.common.StringListBaseProperty;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.StringListBasePropertyImpl;

public class PDPMAssignmentGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.PDPM_ASSIGNMENT;

    public static final String PMM = "PMM";

    private PMMListProperty pdpmAcronyms;

    public PDPMAssignmentGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public PDPMAssignmentGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public PDPMAssignmentGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public PDPMAssignmentGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public PDPMAssignmentGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                               List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public PDPMAssignmentGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }


    protected void initializeProperties()
    {
        pdpmAcronyms = new PMMListProperty(getPropertyCategoryType(), PMM, getPropertyKey(), getType());
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(PMM))
        {
            return pdpmAcronyms;
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
        properties[0] = pdpmAcronyms;

        return properties;
    }

    public String[] getPMM()
    {
        return pdpmAcronyms.getStringListValue();
    }

    public void setPMM(String[] pdpmAcronyms)
    {
        this.pdpmAcronyms.setStringListValue(pdpmAcronyms);
        firePropertyChange();
    }

    public Object clone() throws CloneNotSupportedException
    {
        PDPMAssignmentGroup newGroup = (PDPMAssignmentGroup) super.clone();
        newGroup.pdpmAcronyms = (PMMListProperty) pdpmAcronyms.clone();

        return newGroup;
    }
}
