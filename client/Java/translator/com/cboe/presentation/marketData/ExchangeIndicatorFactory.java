// -----------------------------------------------------------------------------------
// Source file: ExchangeIndicatorFactory
//
// PACKAGE: com.cboe.presentation.marketData
// 
// Created: Jul 9, 2004 3:47:19 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.marketData.ExchangeIndicator;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;

public class ExchangeIndicatorFactory
{
    public static ExchangeIndicator create(ExchangeIndicatorStruct struct)
    {
        return new ExchangeIndicatorImpl(struct);
    }

} // -- end of class ExchangeIndicatorFactory
