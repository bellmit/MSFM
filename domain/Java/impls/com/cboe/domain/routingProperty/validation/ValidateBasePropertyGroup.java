package com.cboe.domain.routingProperty.validation;

import java.util.List;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.BasePropertyValidator;

// -----------------------------------------------------------------------------------
// Source file: ValidateBasePropertyGroup
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

// not used anymore, but may be used again later. see AbstractBasePropertyValidationFactoryImpl
@Deprecated
public class ValidateBasePropertyGroup // extends AbstracBasePropertyGroupValidator
{
    public ValidateBasePropertyGroup()
    {
    }

//    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
//    {
//        boolean valid = true;
//
//        if (basePropertyGroup != null)
//        {
//            BaseProperty[] baseProperties = basePropertyGroup.getAllProperties();
//
//            for (BaseProperty baseProperty : baseProperties)
//            {
//                List<Validator> validators = baseProperty.getValidators();
//                for (Validator validator : validators)
//                {
//                    if (validator instanceof BasePropertyValidator)
//                    {
//                        if (! ((BasePropertyValidator) validator).isValid(baseProperty, validationReport))
//                        {
//                            valid = false;
//                        }
//                }
//            }
//        }
//
//        return valid;
//    }
}
