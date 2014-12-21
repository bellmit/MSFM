package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: NewBobOriginCodeContingencyTypeMappingGroup
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
import com.cboe.interfaces.domain.routingProperty.common.ContingencyTypeListBaseProperty;
import com.cboe.interfaces.domain.routingProperty.common.ContingencyType;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.ContingencyTypeListBasePropertyImpl;

public class NewBobOriginCodeContingencyTypeMappingGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING;

    public static final String NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING = "NewBobOriginCodeContigencyTypeMapping";

    private ContingencyTypeListBaseProperty originCodeContingencyTypeMapping;

    public NewBobOriginCodeContingencyTypeMappingGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public NewBobOriginCodeContingencyTypeMappingGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public NewBobOriginCodeContingencyTypeMappingGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public NewBobOriginCodeContingencyTypeMappingGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public NewBobOriginCodeContingencyTypeMappingGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                            List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public NewBobOriginCodeContingencyTypeMappingGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }


    protected void initializeProperties()
    {
        originCodeContingencyTypeMapping = 
            new ContingencyTypeListBasePropertyImpl(getPropertyCategoryType(), NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING, getPropertyKey(), getType());
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING))
        {
            return originCodeContingencyTypeMapping;
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
        properties[0] = originCodeContingencyTypeMapping;

        return properties;
    }

    public ContingencyType[] getOriginCodeContingencyTypeMapping()
    {
        return originCodeContingencyTypeMapping.getContingencyTypeListValue();
    }

    public void setOriginCodeContingencyTypeMapping(ContingencyType[] flag)
    {
        originCodeContingencyTypeMapping.setContingencyTypeListValue(flag);
        firePropertyChange();
    }

    public Object clone() throws CloneNotSupportedException
    {
        NewBobOriginCodeContingencyTypeMappingGroup newGroup = (NewBobOriginCodeContingencyTypeMappingGroup) super.clone();
        newGroup.originCodeContingencyTypeMapping = (ContingencyTypeListBaseProperty) originCodeContingencyTypeMapping.clone();

        return newGroup;
    }
}
