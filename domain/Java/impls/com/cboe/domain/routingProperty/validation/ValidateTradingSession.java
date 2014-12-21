package com.cboe.domain.routingProperty.validation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.MutableBasePropertyKey;

import com.cboe.domain.routingProperty.key.AbstractBasePropertyKey;
import com.cboe.domain.routingProperty.AbstractAffiliatedFirmPropertyGroup;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

// -----------------------------------------------------------------------------------
// Source file: ValidateTradingSession
//
// PACKAGE: com.cboe.internalPresentation.routingProperties.validation
// 
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class ValidateTradingSession extends AbstracBasePropertyGroupValidator
{
    public ValidateTradingSession()
    {
    }

    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
        String sessionName = basePropertyGroup.getSessionName();
        boolean isValid = true;

        if(sessionName == null || sessionName.length() == 0)
        {
            validationReport.append("Must specify a trading session\n");
            isValid = false;
        }

        return isValid;
    }


}
