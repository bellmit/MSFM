//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrder;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;

import com.cboe.presentation.order.AbstractOrder;

import com.cboe.intermarketPresentation.intermarketMessages.ExchangeMarketFactory;
import com.cboe.intermarketPresentation.common.formatters.FormatFactory;

class HeldOrderImpl extends AbstractOrder implements HeldOrder
{
    protected ExchangeMarket[] exchangeMarkets;

    protected HeldOrderStruct heldOrderStruct;

    protected String displayString = null;
    public HeldOrderImpl(HeldOrderStruct heldOrderStruct)
    {
        super(heldOrderStruct.order);
        this.heldOrderStruct = heldOrderStruct;
        initialize();
    }
    public HeldOrderImpl()
    {
        this(new HeldOrderStruct());
    }

    /**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        HeldOrderImpl newImpl = new HeldOrderImpl(getHeldOrderStruct());
        return newImpl;
    }

    private void initialize()
    {
        displayString = null;
        exchangeMarkets = new ExchangeMarket[heldOrderStruct.currentMarketBest.length];
        for (int i = 0; i < heldOrderStruct.currentMarketBest.length; i++)
        {
            exchangeMarkets[i] = ExchangeMarketFactory.createExchangeMarket(heldOrderStruct.currentMarketBest[i]);
        }
    }
    public ExchangeMarket[] getCurrentMarketBest()
    {
        return exchangeMarkets;
    }

    /**
     * Gets the underlying struct
     * @return HeldOrderStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public HeldOrderStruct getHeldOrderStruct()
    {
        return heldOrderStruct;
    }

    public String toString()
    {
        // this object does not allow any modifications, so it is safe to format it once only.
        if(displayString == null)
        {
            displayString = FormatFactory.getHeldOrderFormatStrategy().format(this);
        }
        return displayString;
    }
}
