package com.cboe.domain.routingProperty.validation;

// -----------------------------------------------------------------------------------
// Source file: ValidateMultiDestination
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
//
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyValidator;
import com.cboe.interfaces.domain.routingProperty.common.Destination;


public abstract class ValidateMultiDestination extends AbstracBasePropertytValidator
{
    private BasePropertyValidator[] basePropertyValidator;


    protected ValidateMultiDestination(String validatorName, String propName)
    {
        super(validatorName, propName);
    }

    protected ValidateMultiDestination(String propName)
    {
        super(propName);
    }

    public boolean isValid(BaseProperty baseProperty, StringBuffer validationReport)
    {
        boolean valid = true;

        createValidators(baseProperty);

        for (BasePropertyValidator bpValidator : basePropertyValidator)
        {
            if (! bpValidator.isValid(baseProperty, validationReport))
            {
                valid = false;
            }
        }

        return valid;
    }

    protected abstract Destination[] getDestinations(BaseProperty baseProperty);

    protected abstract String[] getDestinationNames(BaseProperty baseProperty);

    protected abstract BasePropertyValidator createdDestinationValidator(BaseProperty baseProperty, int num);

    protected void createValidators(BaseProperty baseProperty)
    {
        Destination[] destinations = getDestinations(baseProperty);
        int destinationCount = destinations.length;

        if (baseProperty.isOptional())
        {
            // if it is optional and all destinations are empty, don't bother creating a validator
            StringBuffer buf = new StringBuffer(20);
            for (Destination destination : destinations)
            {
                buf.append(destination.getWorkstation());
            }

            if (buf.length() == 0)
            {
                destinationCount = 0;
            }
        }

        basePropertyValidator = new BasePropertyValidator[destinationCount];
        for(int i = 0; i < destinationCount; ++i)
        {
            basePropertyValidator[i] = createdDestinationValidator(baseProperty, i);
        }
    }
}
