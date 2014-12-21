package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: FirmTradingParameter
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// Created: Aug 23, 2006 10:29:48 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.List;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;


public class VolumeDeviationParameter extends StringBasePropertyImpl
{
    public VolumeDeviationParameter(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                BasePropertyType type)
    {
        super(propertyCategory, propertyName, basePropertyKey, type);
    }

    public VolumeDeviationParameter(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, basePropertyKey, type, validators);
    }

    public String getVolumeDeviation()
    {
        return super.getStringValue();
    }

    public void setVolumeDeviation(String value)
    {
        super.setStringValue(value);
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public int compareTo(Object other)
    {
        return super.compareTo(other);
    }
}