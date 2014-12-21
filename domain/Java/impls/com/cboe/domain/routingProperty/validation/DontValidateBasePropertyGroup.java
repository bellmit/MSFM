package com.cboe.domain.routingProperty.validation;

import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;

// -----------------------------------------------------------------------------------
// Source file: DontValidateBasePropertyGroup
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class DontValidateBasePropertyGroup extends AbstracBasePropertyGroupValidator
{
    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
        return true;
    }
}
