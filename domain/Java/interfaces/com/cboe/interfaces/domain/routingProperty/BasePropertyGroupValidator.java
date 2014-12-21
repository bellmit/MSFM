package com.cboe.interfaces.domain.routingProperty;

// -----------------------------------------------------------------------------------
// Source file: BasePropertyGroupValidator
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Apr 24, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

@SuppressWarnings({"MarkerInterface"})
public interface BasePropertyGroupValidator extends Validator
{
    boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport);
}