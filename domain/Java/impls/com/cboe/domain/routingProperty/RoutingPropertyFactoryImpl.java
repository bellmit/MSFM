package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: RoutingPropertyFactoryImpl
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Jun 20, 2006 2:32:44 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;

public class RoutingPropertyFactoryImpl extends BasePropertyFactoryImpl
{
    protected String getPropertyCategoryType()
    {
        return PropertyCategoryTypes.ROUTING_PROPERTIES;
    }

    protected Map<String, BasePropertyClassType> getPropertyMap()
    {
        return RoutingPopertyFactoryHelper.PROPERTY_NAME_CLASS_TYPE_MAP;
    }

    protected BasePropertyFactory getPropertyFactoryHome()
    {
        return RoutingPropertyFactoryHome.find();
    }
}
