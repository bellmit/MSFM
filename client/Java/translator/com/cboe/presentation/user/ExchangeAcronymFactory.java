//
// -----------------------------------------------------------------------------------
// Source file: ExchangeAcronymFactory.java
//
// PACKAGE: com.cboe.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.user;

import com.cboe.idl.cmiUser.ExchangeAcronymStruct;

import com.cboe.interfaces.presentation.user.ExchangeAcronym;

public class ExchangeAcronymFactory
{
    private ExchangeAcronymFactory(){}

    public static ExchangeAcronym createExchangeAcronym(ExchangeAcronymStruct exchangeAcronymStruct)
    {
        return createExchangeAcronym(exchangeAcronymStruct.exchange, exchangeAcronymStruct.acronym);
    }

    public static ExchangeAcronym createExchangeAcronym(String exchange, String acronym)
    {
        return new ExchangeAcronymModelImpl(exchange, acronym);
    }
}