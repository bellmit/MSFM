// -----------------------------------------------------------------------------------
// Source file: ExchangeIndicatorImpl
//
// PACKAGE: com.cboe.presentation.marketData
// 
// Created: Jul 9, 2004 3:36:28 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.marketData.ExchangeIndicator;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

public class ExchangeIndicatorImpl extends AbstractBusinessModel implements ExchangeIndicator
{
    private ExchangeIndicatorStruct struct;

    private ExchangeIndicatorImpl() { super(); }

    public ExchangeIndicatorImpl(ExchangeIndicatorStruct struct)
    {
        this();
        if (struct == null)
        {
            throw new IllegalArgumentException("Exchange Indicator struct can not be null");
        }

        this.struct = struct;
    }

    public ExchangeIndicatorStruct getStruct()
    {
        return struct;
    }

    public String getExchange()
    {
        return struct.exchange;
    }

    public short getMarketCondition()
    {
        return struct.marketCondition;
    }

} // -- end of class ExchangeIndicatorImpl
