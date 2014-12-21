/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.presentation.order
 * User: torresl
 * Date: Dec 31, 2002 2:19:25 PM
 */
package com.cboe.presentation.order;

import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.order.ContraParty;

import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.presentation.user.ExchangeFirmFactory;

class ContraPartyImpl implements ContraParty
{
    ExchangeAcronym user ;
    ExchangeFirm    firm;
    Integer         quantity;
    ContraPartyStruct contraPartyStruct;
    public ContraPartyImpl(ContraPartyStruct contraPartyStruct)
    {
        this.contraPartyStruct = contraPartyStruct;
        initialize();
    }

    private void initialize()
    {
        user = ExchangeAcronymFactory.createExchangeAcronym(contraPartyStruct.user);
        firm = ExchangeFirmFactory.createExchangeFirm(contraPartyStruct.firm);
        quantity = new Integer(contraPartyStruct.quantity);
    }

    /**
     * Gets the underlying struct
     * @return ContraPartyStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ContraPartyStruct getStruct()
    {
        return contraPartyStruct;
    }

    public ExchangeAcronym getUser()
    {
        return user;
    }

    public ExchangeFirm getFirm()
    {
        return firm;
    }

    public Integer getQuantity()
    {
        return quantity;
    }
}
