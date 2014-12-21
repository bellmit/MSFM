package com.cboe.domain.routingProperty.key;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;

// -----------------------------------------------------------------------------------
// Source file: PartialPropertyKey
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Jul 25, 2006 4:01:40 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class PartialPropertyKey implements BasePropertyKey
{
    protected Object[] keyValues;
    protected String propertyKey;

    public PartialPropertyKey(Object[] keyValues)
    {
        this.keyValues = keyValues;
    }

    public String getPropertyKey()
    {
        if (propertyKey == null)
        {
            propertyKey = RoutingKeyHelper.createPropertyKey(keyValues);
        }

        return propertyKey;
    }

    public Object clone() throws CloneNotSupportedException
    {
        PartialPropertyKey newKey = (PartialPropertyKey) super.clone();
        newKey.keyValues = keyValues.clone();

        return newKey;
    }

    public String getPropertyName()
    {
        return "";
    }

    public String getFirmNumber()
    {
        return "";
    }

    public String getExchangeAcronym()
    {
        return "";
    }

    public ExchangeFirmStruct getExchangeFirmStruct()
    {
        return new ExchangeFirmStruct(getExchangeAcronym(), getFirmNumber());
    }

    public String getSessionName()
    {
        return "";
    }
}
