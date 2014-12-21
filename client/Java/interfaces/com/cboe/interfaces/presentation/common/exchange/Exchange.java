//
// -----------------------------------------------------------------------------------
// Source file: Exchange.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.exchange
//
// This Exchange interface is a consolidation of two, originally mutually
// exchusive, Exchange interfaces:
//
//        com.cboe.interfaces.presentation.user.Exchange
//        com.cboe.interfaces.internalPresentation.product.Exchange
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.exchange;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface Exchange extends BusinessModel
{
    // moved from com.cboe.interfaces.presentation.user.Exchange
    String getExchange();
    String getFullName();

    // moved from original com.cboe.interfaces.internalPresentation.product.Exchange
    public int getExchangeKey();
    public String getAcronym();
    public String getName();
    public int getMembershipKey();
}
