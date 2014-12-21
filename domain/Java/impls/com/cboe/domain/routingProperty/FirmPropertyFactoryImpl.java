package com.cboe.domain.routingProperty;

import java.util.*;

import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;
// -----------------------------------------------------------------------------------
// Source file: FirmPropertyFactoryImpl
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: 
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class FirmPropertyFactoryImpl extends BasePropertyFactoryImpl
{
    protected String getPropertyCategoryType()
    {
        return PropertyCategoryTypes.FIRM_PROPERTIES;
    }

    protected Map<String, BasePropertyClassType> getPropertyMap()
    {
        return FirmPropertyFactoryHelper.PROPERTY_NAME_CLASS_TYPE_MAP;
    }

    protected BasePropertyFactory getPropertyFactoryHome()
    {
        return FirmPropertyFactoryHome.find();
    }
}
