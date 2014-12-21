package com.cboe.interfaces.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
// -----------------------------------------------------------------------------------
// Source file: DoubleBaseProperty
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 27, 2006 9:24:08 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface DoubleBaseProperty extends BaseProperty
{
    double getDoubleValue();

    void setDoubleValue(double doubleValue);
}
