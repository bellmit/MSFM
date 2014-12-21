package com.cboe.interfaces.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
// -----------------------------------------------------------------------------------
// Source file: BooleanBaseProperty
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 27, 2006 10:15:22 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface BooleanBaseProperty extends BaseProperty
{
    boolean getBooleanValue();

    void setBooleanValue(boolean booleanValue);
}
