//
// ------------------------------------------------------------------------
// FILE: SatisfactionAlert.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.domain.dateTime.DateTime;

public interface SatisfactionAlert extends Cloneable
{
    // accessors for header information
    CBOEId                  getAlertId();
    DateTime                getAlertCreationTime();
    short                   getAlertType();
    String                  getSessionName();

    AlertHeader             getAlertHeader();
    int                     getTradedThroughQuantity();
    Price                   getTradedThroughPrice();
    char                    getSide();
    TickerStruct            getLastSale();
    OrderId[]               getTradedThroughOrders();
    String                  getExtensions();
    String                  getExtensionField(String fieldName);

    SatisfactionAlertStruct getStruct();
}
