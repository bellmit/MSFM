//
// -----------------------------------------------------------------------------------
// Source file: ExchangeVolume.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;

public interface ExchangeVolume
{
    /**
     * Gets the underlying struct
     * @return ExchangeVolumeStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ExchangeVolumeStruct getStruct();

    public String getExchange();

    public int getVolume();
}