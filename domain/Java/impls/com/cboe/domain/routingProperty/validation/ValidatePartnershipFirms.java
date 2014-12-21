package com.cboe.domain.routingProperty.validation;

// -----------------------------------------------------------------------------------
// Source file: ValidatePartnershipFirms
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
//
// Created: Mar 4, 2009
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.BaseProperty;

import com.cboe.domain.routingProperty.properties.DirectedAIMPartnerFirmsListProperty;

public class ValidatePartnershipFirms extends AbstracBasePropertytValidator
{
    public ValidatePartnershipFirms(String displayName)
    {
        super(displayName);
    }

    public boolean isValid(BaseProperty baseProperty, StringBuffer validationReport)
    {
        boolean valid = false;
        DirectedAIMPartnerFirmsListProperty firmsListProperty = (DirectedAIMPartnerFirmsListProperty) baseProperty;

        String[] partnerFirms = firmsListProperty.getDirectedAIMFirmPartners();

        if (partnerFirms == null  || partnerFirms.length == 0)
        {
            validationReport.append("No partnership clearing firms have been specified\n");
        }
        else
        {
            valid = true;
            for (String partnerFirm : partnerFirms)
            {
                if (partnerFirm == null  || partnerFirm.length() < 1)
                {
                    validationReport.append("Empty or blank partnership firm was entered\n");
                    valid = false;
                }
                else
                {
                    if (partnerFirm.indexOf(' ') >= 0  || partnerFirm.indexOf('\t') >= 0)
                    {
                        validationReport.append("Partnership firm '").append(partnerFirm).append("' must not contain spaces.\n");
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }

}