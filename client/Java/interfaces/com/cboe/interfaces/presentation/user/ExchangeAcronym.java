//
// -----------------------------------------------------------------------------------
// Source file: ExchangeAcronym.java
//
// PACKAGE: com.cboe.interfaces.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.user;

import com.cboe.idl.cmiUser.ExchangeAcronymStruct;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface ExchangeAcronym extends BusinessModel
{
    String getAcronym();
    String getExchange();
    ExchangeAcronymStruct getExchangeAcronymStruct();

    boolean isNeverBeenSaved();
}
