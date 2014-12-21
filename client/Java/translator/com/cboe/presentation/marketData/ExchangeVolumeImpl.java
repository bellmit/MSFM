//
// -----------------------------------------------------------------------------------
// Source file: ExchangeVolumeImpl.java
//
// PACKAGE: com.cboe.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;

import com.cboe.interfaces.presentation.marketData.ExchangeVolume;

class ExchangeVolumeImpl implements ExchangeVolume
{
    private ExchangeVolumeStruct    exchangeVolumeStruct;
    private String                  exchange;
    private int                     volume;
    public ExchangeVolumeImpl(ExchangeVolumeStruct exchangeVolumeStruct)
    {
        this.exchangeVolumeStruct = exchangeVolumeStruct;
        initialize();
    }
    private void initialize()
    {
        exchange    = new String(exchangeVolumeStruct.exchange);
        volume      = exchangeVolumeStruct.volume;
    }

    public String getExchange()
    {
        return exchange;
    }

    public int getVolume()
    {
        return volume;
    }

    /**
     * Gets the underlying struct
     * @return ExchangeVolumeStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ExchangeVolumeStruct getStruct()
    {
        return exchangeVolumeStruct;
    }
}
