package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: BoothWireOrderPreference
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// Created: Feb 8, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.List;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.common.StringListBasePropertyImpl;
import com.cboe.domain.routingProperty.BasePropertyValidationFactoryHome;


public class PMMListProperty extends StringListBasePropertyImpl
{
    public PMMListProperty(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                    BasePropertyType type)
    {
        super(propertyCategory, propertyName, basePropertyKey, type);
    }

    public PMMListProperty(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                    BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, basePropertyKey, type, validators);
    }

    public String[] getPMM()
    {
        return getStringListValue();
    }

    public void setPMM(String[] pdpmAcronyms)
    {
        setStringListValue(pdpmAcronyms);
    }

    @Override
    protected List<Validator> getDefaultValidators()
    {
        return BasePropertyValidationFactoryHome.find().createPMMValidators(getPropertyName());
    }
}