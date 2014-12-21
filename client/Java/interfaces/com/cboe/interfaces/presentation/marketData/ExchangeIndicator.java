// -----------------------------------------------------------------------------------
// Source file: ExchangeIndicator
//
// PACKAGE: com.cboe.interfaces.presentation.marketData
// 
// Created: Jul 9, 2004 3:31:57 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface ExchangeIndicator extends BusinessModel
{
    public ExchangeIndicatorStruct getStruct();

    public String getExchange();
    public short getMarketCondition();

} // -- end of interface ExchangeIndicator
