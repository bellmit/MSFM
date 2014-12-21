//
// -----------------------------------------------------------------------------------
// Source file: ExchangeVolumeFactory.java
//
// PACKAGE: com.cboe.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;

import com.cboe.interfaces.presentation.marketData.ExchangeVolume;

public class ExchangeVolumeFactory
{
    public static ExchangeVolume createExchangeVolume(ExchangeVolumeStruct exchangeVolumeStruct)
    {
        return new ExchangeVolumeImpl(exchangeVolumeStruct);
    }
}
