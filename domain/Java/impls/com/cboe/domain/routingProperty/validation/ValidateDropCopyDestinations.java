package com.cboe.domain.routingProperty.validation;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyValidator;
import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.common.DropCopyBaseProperty;

// -----------------------------------------------------------------------------------
// Source file: ValidateDropCopyDestinations
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty.validation
// 
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class ValidateDropCopyDestinations extends ValidateMultiDestination
{
    public ValidateDropCopyDestinations()
    {
        super("");
    }

    protected Destination[] getDestinations(BaseProperty baseProperty)
    {
        DropCopyBaseProperty dcbp = (DropCopyBaseProperty) baseProperty;

        return new Destination[] { dcbp.getDirectRoute(), dcbp.getFillDropCopy(), dcbp.getCancelDropCopy() };
    }

    protected String[] getDestinationNames(BaseProperty baseProperty)
    {
        return new String[] { "Direct Route", "Fill Drop Copy", "Cancel Drop Copy" };
    }

    protected boolean[] getIsOptionals(BaseProperty baseProperty)
    {
        DropCopyBaseProperty dcbp = (DropCopyBaseProperty) baseProperty;

        return new boolean[] { dcbp.isDirectRouteOptional(), dcbp.isFillDropCopyOptional(), dcbp.isCancelDropCopyOptional() };
    }

    protected BasePropertyValidator createdDestinationValidator(BaseProperty bProperty, final int num)
    {
        String[]             destinationNames       = getDestinationNames(bProperty);
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
                // this decides if an individual value within the list is optional. the parent container class decides
                // if the property as a whole is optional
                return getIsOptionals(baseProperty)[num];
            }
        };

        return basePropertyValidator;
    }
}
