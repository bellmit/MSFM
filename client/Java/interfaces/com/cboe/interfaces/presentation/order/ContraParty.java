//
// -----------------------------------------------------------------------------------
// Source file: ContraParty.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;

public interface ContraParty
{
    /**
     * Gets the underlying struct
     * @return ContraPartyStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ContraPartyStruct getStruct();

    public ExchangeAcronym getUser();
    public ExchangeFirm getFirm();
    public Integer getQuantity();
}