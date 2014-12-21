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

import com.cboe.domain.routingProperty.validation.ValidateDefaultFirm;
import com.cboe.domain.routingProperty.validation.ValidateDefaultKeyCombination;
import com.cboe.domain.routingProperty.validation.ValidateDestinationListProperty;
import com.cboe.domain.routingProperty.validation.ValidateDropCopyDestinations;
import com.cboe.domain.routingProperty.validation.ValidateDestination;
import com.cboe.domain.routingProperty.validation.ValidateNewBobContingency;
import com.cboe.domain.routingProperty.validation.ValidateNewBobOriginCode;
import com.cboe.domain.routingProperty.validation.ValidatePMM;
import com.cboe.domain.routingProperty.validation.ValidatePartnershipFirms;
import com.cboe.domain.routingProperty.validation.ValidateAffiliatedFirmPropertyKey;
import com.cboe.domain.routingProperty.validation.ValidateTradingSession;
import com.cboe.domain.routingProperty.validation.ValidateAcronym;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;



public class AbstractBasePropertyValidationFactoryImpl implements BasePropertyValidationFactory
{
    private boolean isValidationRequested = true;

    public AbstractBasePropertyValidationFactoryImpl()
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
        List<Validator> validators = new ArrayList<Validator>(3);

        validators.add(new ValidateDefaultKeyCombination());
        validators.add(new ValidateTradingSession());
        validators.add(new ValidateDefaultFirm());
        validators.add(new ValidateNewBobOriginCode());
        validators.add(new ValidateNewBobContingency());
        return validators;
    }

    public List<Validator> createDestinationValidators(String displayName)
    {
        List<Validator> validators = new ArrayList<Validator>(2);

        Validator validator = new ValidateDestination(displayName);
        validators.add(validator);

        return validators;
    }

    public List<Validator> createDestinationListValidators(String displayName)
    {
        List<Validator> validators = new ArrayList<Validator>(2);

        Validator validator = new ValidateDestinationListProperty(displayName);
        validators.add(validator);

        return validators;
    }

    public List<Validator> createDropCopyDestinationValidators()
    {
        List<Validator> validators = new ArrayList<Validator>(2);

        Validator validator = new ValidateDropCopyDestinations();
        validators.add(validator);

        return validators;
    }

    public List<Validator> createPMMValidators(String displayName)
    {
        List<Validator> validators = new ArrayList<Validator>(1);

        Validator validator = new ValidatePMM(displayName);
        validators.add(validator);

        return validators;

    }

    public List<Validator> createAcronymValidators(String displayName)
    {
        List<Validator> validators = new ArrayList<Validator>(1);

        Validator validator = new ValidateAcronym(displayName);
        validators.add(validator);

        return validators;
    }

    public List<Validator> createPartnershipFirmsValidators(String displayName)
    {
        List<Validator> validators = new ArrayList<Validator>(2);

        Validator validator = new ValidatePartnershipFirms(displayName);
        validators.add(validator);

        return validators;
    }

    public List<Validator> createAffiliatedFirmPropertyKeyValidators()
    {
        List<Validator> validators = new ArrayList<Validator>(2);

        validators.add(new ValidateAffiliatedFirmPropertyKey());

        return validators;
    }

    // parent is not used here, but used by gui override to center the error dialog
    public void displayValidationReport(StringBuffer validationReport, Object parent)
    {
        Log.debug("AbstractBasePropertyValidationFactoryImpl.displayValidationReport:\n\n" + validationReport + '\n');
    }

}
