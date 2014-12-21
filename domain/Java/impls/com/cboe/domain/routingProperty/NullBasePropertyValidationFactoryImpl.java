package com.cboe.domain.routingProperty;

// -----------------------------------------------------------------------------------
// Source file: AbstractBasePropertyValidationFactoryImpl
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Mar 20, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyValidationFactory;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.validation.ValidateDefaultKeyCombination;
import com.cboe.domain.routingProperty.validation.ValidateDestinationListProperty;
import com.cboe.domain.routingProperty.validation.ValidateDropCopyDestinations;
import com.cboe.domain.routingProperty.validation.ValidateDestination;
import com.cboe.domain.routingProperty.validation.ValidatePMM;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class NullBasePropertyValidationFactoryImpl implements BasePropertyValidationFactory
{
    private boolean isValidationRequested = false;
    

    public NullBasePropertyValidationFactoryImpl()
    {
        initialize();
    }

    protected void initialize()
    {
    }

    public void setValidationRequested(boolean requestValiation)
    {
        isValidationRequested = requestValiation;
    }

    public boolean isValidationRequested()
    {
        return isValidationRequested;
    }

    public List<Validator> createBaseGroupValidators()
    {
        return getEmptyValidatorList();
    }

    public List<Validator> createDestinationValidators(String displayName)
    {
        return getEmptyValidatorList();
    }

    public List<Validator> createDestinationListValidators(String displayName)
    {
        return getEmptyValidatorList();
    }

    public List<Validator> createDropCopyDestinationValidators()
    {
        return getEmptyValidatorList();
    }

    public List<Validator> createPMMValidators(String displayName)
    {
        return getEmptyValidatorList();
    }

    public List<Validator> createAcronymValidators(String displayName)
    {
        return getEmptyValidatorList();
    }
    public List<Validator> createPartnershipFirmsValidators(String displayName)
    {
        return getEmptyValidatorList();
    }

    public List<Validator> createAffiliatedFirmPropertyKeyValidators()
    {
        return getEmptyValidatorList();
    }

    private List<Validator> getEmptyValidatorList()
    {
        return new ArrayList<Validator>(1);
    }

    // parent is not used here, but used by gui override to center the error dialog
    public void displayValidationReport(StringBuffer validationReport, Object parent)
    {
        Log.alarm("NullBasePropertyValidationFactoryImpl.displayValidationReport SHOULD NOT BE CALLED ?!:\n\n" +
                  validationReport + '\n');
    }

}
