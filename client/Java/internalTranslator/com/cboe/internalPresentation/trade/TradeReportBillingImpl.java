//
// -----------------------------------------------------------------------------------
// Source file: TradeReportBillingImpl.java
//
// PACKAGE: com.cboe.internalPresentation.trade
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.trade;

import com.cboe.interfaces.internalPresentation.trade.TradeReportBilling;
import com.cboe.interfaces.internalPresentation.trade.AtomicTradeBilling;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.idl.trade.TradeReportBillingStruct;
import com.cboe.idl.trade.CmtaAllocationStruct;
import com.cboe.presentation.util.CBOEIdImpl;

public class TradeReportBillingImpl implements TradeReportBilling
{
    private TradeReportBillingStruct billingStruct;
    private AtomicTradeBilling[] atomicTradeBillings;
    private CBOEId tradeID;

    public TradeReportBillingImpl(TradeReportBillingStruct billingStruct)
    {
        this.billingStruct = billingStruct;
    }

    public AtomicTradeBilling[] getAtomicTradeBillings()
    {
        if(atomicTradeBillings == null)
        {
            atomicTradeBillings = new AtomicTradeBilling[getStruct().partiesBillingType.length];
            for(int i=0; i<atomicTradeBillings.length; i++)
            {
                atomicTradeBillings[i] = new AtomicTradeBillingImpl(getStruct().partiesBillingType[i]);
            }
        }
        return atomicTradeBillings;
    }

    public AtomicTradeBilling getAtomicTradeBilling(CBOEId atomicTradeID)
    {
        AtomicTradeBilling retVal = null;
        AtomicTradeBilling[] atomicBillings = getAtomicTradeBillings();
        for (int j = 0; j < atomicBillings.length; j++)
        {
            if (atomicTradeID.equals(atomicBillings[j].getAtomicTradeId()))
            {
                retVal = atomicBillings[j];
            }
        }
        return retVal;
    }

    public CmtaAllocationStruct getCMTAStruct()
    {
        return getStruct().cmtaStruct;
    }

    public String getExtensions()
    {
        return getStruct().extensions;
    }

    public TradeReportBillingStruct getStruct()
    {
        return billingStruct;
    }

    public CBOEId getTradeId()
    {
        if (tradeID == null)
        {
            tradeID = new CBOEIdImpl(getStruct().tradeId);
        }
        return tradeID;
    }
}
