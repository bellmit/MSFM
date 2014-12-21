//
// -----------------------------------------------------------------------------------
// Source file: AtomicTradeBillingImpl.java
//
// PACKAGE: com.cboe.internalPresentation.trade
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.trade;

import com.cboe.interfaces.internalPresentation.trade.AtomicTradeBilling;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.idl.trade.AtomicTradeBillingStruct;
import com.cboe.presentation.util.CBOEIdImpl;

public class AtomicTradeBillingImpl implements AtomicTradeBilling
{
    private AtomicTradeBillingStruct struct;
    private CBOEId atomicID;

    public AtomicTradeBillingImpl(AtomicTradeBillingStruct struct)
    {
        this.struct = struct;
    }

    public CBOEId getAtomicTradeId()
    {
        if(atomicID == null)
        {
            atomicID = new CBOEIdImpl(getStruct().atomicTradeId);
        }
        return atomicID;
    }

    public String getBuyAwayExchanges()
    {
        return getStruct().buyAwayExchanges;
    }

    public char getBuyBillingType()
    {
        return getStruct().buyBillingType;
    }

    public char getBuyerClearingType()
    {
        return getStruct().buyerClearingType;
    }

    public String getExtensions()
    {
        return getStruct().extensions;
    }

    public int getRoundLotQuantity()
    {
        return getStruct().roundLotQuantity;
    }

    public String getSellAwayExchanges()
    {
        return getStruct().sellAwayExchanges;
    }

    public char getSellBillingType()
    {
        return getStruct().sellBillingType;
    }

    public char getSellerClearingType()
    {
        return getStruct().sellerClearingType;
    }

    public AtomicTradeBillingStruct getStruct()
    {
        return struct;
    }
}
