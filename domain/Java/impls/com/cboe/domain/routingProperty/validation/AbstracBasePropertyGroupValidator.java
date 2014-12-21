package com.cboe.domain.routingProperty.validation;

// -----------------------------------------------------------------------------------
// Source file: AbstracBasePropertyGroupValidator
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
//
// Created: Apr 24, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.BasePropertyGroupValidator;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;


public abstract class AbstracBasePropertyGroupValidator extends AbstractValidator implements BasePropertyGroupValidator
{
    protected AbstracBasePropertyGroupValidator()
    {
        this(null);
    }

    protected AbstracBasePropertyGroupValidator(String valName)
    {
        super(valName);
    }

    public abstract boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport);
}