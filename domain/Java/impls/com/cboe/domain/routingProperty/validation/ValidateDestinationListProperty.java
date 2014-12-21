package com.cboe.domain.routingProperty.validation;

import com.cboe.interfaces.domain.routingProperty.common.DestinationListBaseProperty;
import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyValidator;

import com.cboe.domain.routingProperty.common.DestinationImpl;

// -----------------------------------------------------------------------------------
// Source file: ValidateDestinationListProperty
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty.validation
// 
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class ValidateDestinationListProperty extends ValidateMultiDestination
{
    public ValidateDestinationListProperty(String propName)
    {
        super(propName);
    }

    public ValidateDestinationListProperty(String validatorName, String propName)
    {
        super(validatorName, propName);
    }

    @Override
    protected Destination[] getDestinations(BaseProperty baseProperty)
    {
        DestinationListBaseProperty destinationListBaseProperty = (DestinationListBaseProperty) baseProperty;
        Destination[] destinations = destinationListBaseProperty.getDestinationListValue();

        if(destinations == null || destinations.length == 0 && !baseProperty.isOptional())
        {
            destinations = new Destination[1];
            destinations[0] = new DestinationImpl("");
        }

        return destinations;
    }

    @Override
    protected String[] getDestinationNames(BaseProperty baseProperty)
    {
        Destination[] destinations = getDestinations(baseProperty);

        int destinationCount = destinations == null ? 0 : destinations.length;

        StringBuffer namePrefix = new StringBuffer(getFieldName());
        if(destinationCount > 0)
        {
            namePrefix.append(" #");
        }

        String[] dNames = new String[destinationCount];
        for(int i = 0; i < destinationCount; ++i)
        {
            dNames[i] = namePrefix.toString() + " " + i;
        }

        return dNames;
    }

    protected BasePropertyValidator createdDestinationValidator(BaseProperty bProperty, final int num)
    {
        String[]              destinationNames      = getDestinationNames(bProperty);
        BasePropertyValidator basePropertyValidator = new ValidateDestination(destinationNames[num])
        {
            @Override
            protected Destination getDestination(BaseProperty baseProperty)
            {
                Destination[] destinations = getDestinations(baseProperty);
                return destinations[num];
            }

            @Override
            protected boolean isOptional(BaseProperty baseProperty)
            {
                // individual values within a list are not optional, the whole list may be optional, which is handled by
                // the parent container class
                return false;
            }
        };

        return basePropertyValidator;
    }
}
