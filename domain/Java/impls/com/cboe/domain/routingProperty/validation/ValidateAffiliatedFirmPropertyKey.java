package com.cboe.domain.routingProperty.validation;

import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.MutableBasePropertyKey;

import com.cboe.domain.routingProperty.AbstractAffiliatedFirmPropertyGroup;

// -----------------------------------------------------------------------------------
// Source file: ValidateAffiliatedFirmPropertyKey
//
// PACKAGE: com.cboe.internalPresentation.routingProperties.validation
// 
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class ValidateAffiliatedFirmPropertyKey extends AbstracBasePropertyGroupValidator
{
    public ValidateAffiliatedFirmPropertyKey()
    {
    }

    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
        MutableBasePropertyKey mutableBasePropertyKey = (MutableBasePropertyKey) basePropertyGroup.getPropertyKey();
        boolean isValid = true;

        if(basePropertyGroup instanceof AbstractAffiliatedFirmPropertyGroup)
        {
            //verify that an affiliated firm acronym was entered
            String firm = mutableBasePropertyKey.getFirmNumber();
            if(firm == null || firm.length() == 0)
            {
                validationReport.append("Missing Affiliated Firm Acronym");
                isValid = false;
            }
        }
        else
        {
            validationReport.append("Validation of non-Affiliated Firm Property\n");
            isValid = false;
        }

        return isValid;
    }


}
