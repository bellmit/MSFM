package com.cboe.domain.routingProperty.validation;

// -----------------------------------------------------------------------------------
// Source file: AbstractValidator
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
//
// Created: Apr 24, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.Validator;


public abstract class AbstractValidator implements Validator
{
    private String validatorName;


    protected AbstractValidator()
    {
        this(null);
    }

    protected AbstractValidator(String name)
    {
        String valName = name;
        if (name == null)
        {
            valName = getClass().getSimpleName();
        }
        if (valName == null  || valName.length() == 0)
        {
            valName = "AbstractValidator";
        }
        validatorName = valName;
    }

    public String getName()
    {
        return validatorName;
    }
}