//
// -----------------------------------------------------------------------------------
// Source file: DsmBidAskStruct.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiUtil.PriceStruct;


public class DsmBidAskStruct
{
    private PriceStruct bid;
    private PriceStruct ask;
    private boolean     isBidDebit;
    private boolean     isAskDebit;
    private boolean     isFlipped;

    public DsmBidAskStruct(DsmBidAskStruct dsmBidAskStruct)     // defaults to Debit bid & ask
    {
        this(dsmBidAskStruct.getBid(), dsmBidAskStruct.getAsk());
    }

    public DsmBidAskStruct(PriceStruct b, PriceStruct a)        // defaults to Debit bid & ask
    {
        this(b, a, true, true);
    }

    public DsmBidAskStruct(DsmBidAskStruct dsmBidAskStruct, boolean bidDebit, boolean askDebit)
    {
        this(dsmBidAskStruct.getBid(), dsmBidAskStruct.getAsk(), bidDebit, askDebit);
    }

    public DsmBidAskStruct(PriceStruct b, PriceStruct a, boolean bidDebit, boolean askDebit)
    {
        setBid(b);
        setAsk(a);
        setIsBidDebit(bidDebit);
        setIsAskDebit(askDebit);
    }

    public PriceStruct getBid()
    {
        return bid;
    }

    public void setBid(PriceStruct b)
    {
        bid = b;
    }

    public PriceStruct getAsk()
    {
        return ask;
    }

    public void setAsk(PriceStruct a)
    {
        ask = a;
    }

    public void setIsBidDebit(boolean d)
    {
        isBidDebit = d;
    }

    public boolean isBidDebit()
    {
        return isBidDebit;
    }

    public void setIsAskDebit(boolean a)
    {
        isAskDebit = a;
    }

    public boolean isAskDebit()
    {
        return isAskDebit;
    }

    public void setFlipped(boolean f)
    {
        isFlipped = f;
    }

    public boolean isFlipped()
    {
        return isFlipped;
    }
}
