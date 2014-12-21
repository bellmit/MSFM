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
import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.common.DestinationBaseProperty;


public class ValidateDestination extends AbstracBasePropertytValidator
{
    public static final String DEFAULT_FIELD_NAME = "Destination";


    public ValidateDestination(String destinationName)
    {
        super(destinationName);
    }

    public boolean isValid(BaseProperty baseProperty, StringBuffer validationReport)
    {
        boolean     valid            = false;
        String      errorMsg         = null;
        Destination destination      = getDestination(baseProperty);
        String      destinationValue;

        destinationValue = destination == null ? "" : destination.getWorkstation();

        if(destinationValue.trim().length() < 1  &&  !isOptional(baseProperty))
        {
            errorMsg = "is empty or space(s)";     // TODO delegate to empty checker
        }
        else if (destinationValue.length() > 0  &&  destinationValue.trim().length() < 1)      // TODO push this up to abstract or blank validator
        {
            // empty is allowed, but not spaces
            errorMsg = "is space(s)";     // TODO delegate to empty checker
        }
        else if(destinationValue.trim().length() < destinationValue.length())   // TODO delegate to space checker
        {
            errorMsg = "leading or trailing spaces are not allowed";   // TODO embedded spaces ?
        }
        else if (isPossibleParClient(destinationValue))
        {
            if (Character.isLowerCase(destinationValue.charAt(0)))
            {
                errorMsg = "Destinations starting with W must be UPPER case W followed by 3 digits";
            }
            else if (destinationValue.length() != 4)
            {
                errorMsg = "Destinations starting with W must be followed by 3 digits";
            }
            else
            {
                String digits = destinationValue.substring(1);
                try
                {
                    int number = Integer.parseInt(digits);
                    if(digits.length() != 3  ||  number < 0)
                    {
                        errorMsg = "the number following W must be 3 digits long";
                    }
                    else
                    {
                        valid = true;
                    }
                }
                catch(NumberFormatException nfe)
                {
                    errorMsg = "starts with W but does not end in 3 digit number";
                }
            }
        }
        else // TODO what about long string > 4 chars
        {
            valid = true;
        }

        if (! valid)
        {
            addErrorPrefix(validationReport, destinationValue).append(errorMsg).append('\n');
        }
        return valid;
    }

    protected Destination getDestination(BaseProperty baseProperty)
    {
        DestinationBaseProperty destinationBaseProperty = (DestinationBaseProperty) baseProperty;
        return destinationBaseProperty.getDestination();
    }

    protected boolean isPossibleParClient(String dest)
    {
        return dest != null && dest.length() > 0 && dest.toUpperCase().charAt(0) == 'W';
    }

    protected StringBuffer addErrorPrefix(StringBuffer validationReport, String destinationValue)
    {
        return validationReport.append("destination [" + getFieldName() + "] has value [" + destinationValue + "] ");
    }
}
