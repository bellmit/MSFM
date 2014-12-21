package com.cboe.domain.routingProperty.validation;

// -----------------------------------------------------------------------------------
// Source file: ValidateAcronym
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
//
// Created: Mar 17, 2009
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.domain.routingProperty.properties.FirmClassAcronymPreferenceGroup;
import com.cboe.domain.routingProperty.properties.AcronymProperty;


public class ValidateAcronym extends AbstracBasePropertytValidator
{
    public static final String DEFAULT_FIELD_NAME = "Acronym";


    public ValidateAcronym(String displayName)
    {
        super(displayName);
    }

    public boolean isValid(BaseProperty baseProperty, StringBuffer validationReport)
    {
        boolean valid = false;
        AcronymProperty acronymProperty = (AcronymProperty) baseProperty;

        String acronymString = acronymProperty.getStringValue();

        if (acronymString == null  ||  acronymString.length() == 0)
        {
            validationReport.append("cmiUser Acronym string is empty\n");
        }
        else
        {
            valid = true;
        }

        return valid;
    }
}