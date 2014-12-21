package com.cboe.interfaces.domain.routingProperty;

// -----------------------------------------------------------------------------------
// Source file: BasePropertyValidationFactory
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Mar 20, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.List;


public interface BasePropertyValidationFactory
{
    void setValidationRequested(boolean requestValiation);

    boolean isValidationRequested();

    List<Validator> createBaseGroupValidators();

    List<Validator> createDestinationValidators(String displayName);

    List<Validator> createDestinationListValidators(String displayName);

    List<Validator> createDropCopyDestinationValidators();

    List<Validator> createPMMValidators(String displayName);

    List<Validator> createPartnershipFirmsValidators(String displayName);

    List<Validator> createAffiliatedFirmPropertyKeyValidators();
    
    List<Validator> createAcronymValidators(String displayName);

    void displayValidationReport(StringBuffer validationReport, Object parent);
}
