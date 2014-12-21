package com.cboe.interfaces.domain.routingProperty;

// -----------------------------------------------------------------------------------
// Source file: BasePropertyValidator
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Apr 24, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface BasePropertyValidator extends Validator
{
    String  getFieldName();

    boolean isValid(BaseProperty baseProperty, StringBuffer validationReport);
}