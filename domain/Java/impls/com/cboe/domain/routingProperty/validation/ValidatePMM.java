package com.cboe.domain.routingProperty.validation;

// -----------------------------------------------------------------------------------
// Source file: ValidateDestination
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
//
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.BaseProperty;

import com.cboe.domain.routingProperty.properties.PMMListProperty;


public class ValidatePMM extends AbstracBasePropertytValidator
{
    public ValidatePMM(String displayName)
    {
        super(displayName);
    }

    public boolean isValid(BaseProperty baseProperty, StringBuffer validationReport)
    {
        boolean valid = false;
        PMMListProperty pmmListProperty = (PMMListProperty) baseProperty;

        String[] pmmString = pmmListProperty.getPMM();

        if (pmmString == null  ||  pmmString.length == 0)
        {
            validationReport.append("PMM string is empty\n");
        }
        else if (pmmString.length > 5)
        {
            validationReport.append("PMM count > 5\n");
        }
        else
        {
            valid = true;
            for (int i = 0; i < pmmString.length; ++i)
            {
                if (pmmString[i] == null  ||  pmmString[i].length() < 1)
                {
                    validationReport.append("PMM # " + i + " is empty or blank\n");
                    valid = false;
                }
                else
                {
                    String pmm = pmmString[i];
                    if (pmm.length() != 3  ||  pmm.indexOf(' ') >= 0  ||  pmm.indexOf('\t') >= 0)
                    {
                        validationReport.append("PMM # " + i + " must be 3 non-space characters\n");
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }
}