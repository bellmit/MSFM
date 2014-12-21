package com.cboe.interfaces.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: IntegerBaseProperty
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 26, 2006 4:01:10 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.routingProperty.BaseProperty;

public interface IntegerBaseProperty extends BaseProperty
{
    int getIntegerValue();

    void setIntegerValue(int integerValue);
}
