/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Jan 2, 2003 10:01:41 AM
 */
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderFillReject;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.FillReject;
import com.cboe.interfaces.presentation.order.OrderDetail;

import com.cboe.idl.cmiIntermarketMessages.OrderFillRejectStruct;

import com.cboe.presentation.order.OrderDetailFactory;

class OrderFillRejectImpl implements OrderFillReject
{

    OrderDetail orderDetail;
    FillReject[] fillRejectReports;
    OrderFillRejectStruct orderFillRejectStruct;

    public OrderFillRejectImpl(OrderFillRejectStruct orderFillRejectStruct)
    {
        this.orderFillRejectStruct = orderFillRejectStruct;
        initialize();
    }

    private void initialize()
    {
        orderDetail = OrderDetailFactory.createOrderDetail(orderFillRejectStruct.rejectedFillOrder);
        fillRejectReports = new FillReject[orderFillRejectStruct.fillRejectReports.length];
        for (int i = 0; i < fillRejectReports.length; i++)
        {
            fillRejectReports[i] = FillRejectFactory.createFillReject(orderFillRejectStruct.fillRejectReports[i]);
        }
    }

    public OrderDetail getOrderDetail()
    {
        return orderDetail;
    }

    public FillReject[] getFillRejectReports()
    {
        return fillRejectReports;
    }

    public OrderFillRejectStruct toStruct()
    {
        return orderFillRejectStruct;
    }
}
