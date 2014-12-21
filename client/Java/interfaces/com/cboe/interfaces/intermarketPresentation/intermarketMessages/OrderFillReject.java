package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.OrderFillRejectStruct;

import com.cboe.interfaces.presentation.order.OrderDetail;

/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.interfaces.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Jan 2, 2003 9:21:16 AM
 */
public interface OrderFillReject
{
    OrderDetail getOrderDetail();
    FillReject[] getFillRejectReports();

    /**
     * @deprecated
     */
    OrderFillRejectStruct toStruct();

}
