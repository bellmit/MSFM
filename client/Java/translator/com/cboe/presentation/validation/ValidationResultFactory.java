// -----------------------------------------------------------------------------------
// Source file: ValidationResultFactory.java
//
// PACKAGE: com.cboe.presentation.validation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.validation;

import com.cboe.interfaces.presentation.validation.ValidationResult;
import com.cboe.interfaces.presentation.validation.ValidationErrorCodes;

/**
 *  Factory for creating instances of ValidationResult
 */
public class ValidationResultFactory
{
    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ValidationResultFactory()
    {}

    /**
     * Creates an instance of a ValidationResult with passed in error code and error message.
     * @param errorCode int
     * @param errorMessage String
     * @return ValidationResult
     */
    public static ValidationResult create(int errorCode, String errorMessage)
    {
        ValidationResult result = new ValidationResultImpl(errorCode, errorMessage);

        return result;
    }

    /**
     * Creates an instance of a ValidationResult with NO_ERROR error code and empty error message.
     * @return ValidationResult - no error validation result
     */
    public static ValidationResult createNoErrorValidationResult()
    {
        ValidationResult result = new ValidationResultImpl(ValidationErrorCodes.NO_ERROR, "");

        return result;
    }

}
