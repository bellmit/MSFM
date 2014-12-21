//
// -----------------------------------------------------------------------------------
// Source file: DeviationWorkstationParameter.java
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;

public class DeviationWorkstationParameter extends StringBasePropertyImpl
{
    public DeviationWorkstationParameter(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                    BasePropertyType type)
    {
        super(propertyCategory, propertyName, basePropertyKey, type);
    }

    public DeviationWorkstationParameter(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                                    BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, basePropertyKey, type, validators);
    }

    public String getDeviationWorkstation()
    {
        return super.getStringValue();
    }

    public void setDeviationWorkstation(String value)
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
