package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.FillRejectStruct;

import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.presentation.order.Order;

/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.interfaces.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Jan 2, 2003 9:22:17 AM
 */
public interface FillReject
{
    CBOEId getTradeId();
    Order getOrder();
    Integer getTransactionSequenceNumber();
    Short getRejectReason();
    String getExtensions();

    /**
     * @deprecated
     */
    FillRejectStruct toStruct();

}
