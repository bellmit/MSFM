//
// -----------------------------------------------------------------------------------
// Source file: ExchangeModel.java
//
// PACKAGE: com.cboe.interfaces.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.user;

import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;

public interface ExchangeModel extends MutableBusinessModel, Exchange
{
    void setExchange(String anExchange);
    void setFullName(String name);
}