package com.cboe.domain.routingProperty.validation;

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.BasePropertyValidator;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroupValidator;

import com.cboe.domain.routingProperty.BasePropertyValidationFactoryHome;

// -----------------------------------------------------------------------------------
// Source file: ValidationSupport
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Apr 15, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class ValidationSupport implements Cloneable
{
    private static boolean isValidationRequestedMasterFlag = false;
    private static boolean masterFlagIsInitialized         = false;

    private List<Validator> validators;


    public ValidationSupport()
    {
        setValidators(null);
    }

    public static boolean isValidationRequested()
    {
        // initialize here instead of static code so that we are sure factory has been initialized already
        if(!masterFlagIsInitialized)
        {
            isValidationRequestedMasterFlag = BasePropertyValidationFactoryHome.find().isValidationRequested();
            masterFlagIsInitialized = true;
        }
        return isValidationRequestedMasterFlag;
    }

    public List<Validator> getValidators()
    {
        return validators;
    }

    public void setValidators(List<Validator> newValidators)
    {
        if(newValidators == null)
        {
            validators = new ArrayList<Validator>(2);
        }
        else
        {
            validators = newValidators;
        }
    }

    public void addValidators(List<Validator> newValidators)
    {
        if(newValidators != null && newValidators.size() > 0)
        {
            validators.addAll(newValidators);
        }
    }

    public void addValidator(Validator newValidator)
    {
        if(newValidator != null)
        {
            validators.add(newValidator);
        }
    }

//    void setValidator(Validator validator);
//    void addValidators(List<Validator> validators);
//    void removeValidator(Validator validator);
//    void removeValidators(List<Validator> validators);
//    void clearValidators();

    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
        boolean valid = true;

        // bypass all validation if the master flag is false
        if(isValidationRequested())
        {
            for(BaseProperty baseProperty : basePropertyGroup.getAllProperties())
            {
                for (Validator validator : baseProperty.getValidators())
                {
                    if(validator instanceof BasePropertyValidator)
                    {
                        BasePropertyValidator bpValidator = (BasePropertyValidator) validator;
                        if(!bpValidator.isValid(baseProperty, validationReport))
                        {
                            valid = false;
                        }
                    }
                }
            }

            for(Validator validator : validators)
            {
                if(validator instanceof BasePropertyGroupValidator)
                {
                    BasePropertyGroupValidator bpgValidator = (BasePropertyGroupValidator) validator;
                    if(!bpgValidator.isValid(basePropertyGroup, validationReport))
                    {
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }

    public ValidationSupport clone() throws CloneNotSupportedException
    {
        // note that the Validators contained in the ValidationSupport are not cloned
        ValidationSupport newValidationSupport = (ValidationSupport) super.clone();
        return newValidationSupport;
    }
}