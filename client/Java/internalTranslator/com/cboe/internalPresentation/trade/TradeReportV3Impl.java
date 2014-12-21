//
// -----------------------------------------------------------------------------------
// Source file: TradeReportV3Impl.java
//
// PACKAGE: com.cboe.internalPresentation.trade
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.trade;

import com.cboe.idl.trade.TradeReportStructV3;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.interfaces.internalPresentation.trade.TradeReportV3;
import com.cboe.interfaces.internalPresentation.trade.TradeReportBilling;

public class TradeReportV3Impl extends TradeReportImpl implements TradeReportV3
{
    private TradeReportStructV3 struct;
    private TradeReportBilling billingInfo;

    protected TradeReportV3Impl(TradeReportStructV3 struct)
    {
        super(new TradeReportStructV2(struct.tradeReport, struct.settlementTradeReport));
        this.struct = struct;
    }

    public TradeReportStructV3 getStruct()
    {
        return struct;
    }

    public TradeReportBilling getTradeReportBilling()
    {
        if(billingInfo == null)
        {
            billingInfo = new TradeReportBillingImpl(getStruct().billingInfo);
        }
        return billingInfo;
    }
}
