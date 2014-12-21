//
// -----------------------------------------------------------------------------------
// Source file: NBBO.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.product.ProductKeys;

public interface NBBO extends BusinessModel
{
    /**
     * Gets the underlying struct
     * @return NBBOStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public NBBOStruct getStruct();

    public ProductKeys getProductKeys();

    public String getSessionName();

    public Price getBidPrice();

    public ExchangeVolume[] getBidExchangeVolume();

    public Price getAskPrice();

    public ExchangeVolume[] getAskExchangeVolume();

    public Time getSentTime();
}