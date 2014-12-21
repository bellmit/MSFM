package com.cboe.interfaces.domain.routingProperty;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;
// -----------------------------------------------------------------------------------
// Source file: BasePropertyKey
//
// PACKAGE: com.cboe.interfaces.domain.firmRoutingProperty.test.key
// 
// Created: Jul 21, 2006 9:24:00 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface BasePropertyKey extends Cloneable
{
    String getPropertyKey();

    String getPropertyName();

    String getFirmNumber();

    String getExchangeAcronym();

    ExchangeFirmStruct getExchangeFirmStruct();

    String getSessionName();

    Object clone() throws CloneNotSupportedException;
}
