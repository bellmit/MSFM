package com.cboe.domain.routingProperty.validation;

// -----------------------------------------------------------------------------------
// Source file: AbstracBasePropertytValidator
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
//
// Created: Apr 24, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.BasePropertyValidator;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;


public abstract class AbstracBasePropertytValidator extends AbstractValidator implements BasePropertyValidator
{
    private String fieldName;

    protected AbstracBasePropertytValidator(String validatorName, String fName)
    {
        super(validatorName);
        fieldName = fName;
    }

    protected AbstracBasePropertytValidator(String fName)
    {
        fieldName = fName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public abstract boolean isValid(BaseProperty baseProperty, StringBuffer validationReport);

    protected boolean isOptional(BaseProperty baseProperty)
    {
        return baseProperty.isOptional();
    }
}