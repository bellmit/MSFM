/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.presentation.order
 * User: torresl
 * Date: Dec 31, 2002 2:23:46 PM
 */
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.ContraPartyStruct;

import com.cboe.interfaces.presentation.order.ContraParty;

public class ContraPartyFactory
{
    public static ContraParty createContraParty(ContraPartyStruct contraPartyStruct)
    {
        return new ContraPartyImpl(contraPartyStruct);
    }
}