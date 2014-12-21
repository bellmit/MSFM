package com.cboe.domain.routingProperty.validation;

// -----------------------------------------------------------------------------------
// Source file: ValidateKeyAcronym
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
//
// Created: Mar 17, 2009
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.MutableBasePropertyKey;
import com.cboe.domain.routingProperty.properties.FirmClassAcronymPreferenceGroup;
import com.cboe.domain.routingProperty.properties.AcronymProperty;


public class ValidateKeyAcronym extends AbstracBasePropertyGroupValidator
{
    public ValidateKeyAcronym(String displayName)
    {
        super(displayName);
    }

    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
        return true;
    }
}