//
// -----------------------------------------------------------------------------------
// Source file: HeldOrder.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;

import com.cboe.interfaces.presentation.order.Order;

public interface HeldOrder extends Order
{
    /**
     * Gets the underlying struct
     * @return HeldOrderStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public HeldOrderStruct getHeldOrderStruct();

    public ExchangeMarket[] getCurrentMarketBest();
}