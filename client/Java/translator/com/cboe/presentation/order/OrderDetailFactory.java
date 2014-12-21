//
// -----------------------------------------------------------------------------------
// Source file: OrderDetailFactory.java
//
// PACKAGE: com.cboe.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;

import com.cboe.interfaces.presentation.order.OrderDetail;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public class OrderDetailFactory
{
    public static OrderDetail createOrderDetail(OrderDetailStruct orderDetailStruct)
    {
        return new OrderDetailImpl(orderDetailStruct);
    }

    public static OrderDetail createOrderDetail()
    {
        OrderDetailStruct newStruct = new OrderDetailStruct();
        newStruct.statusChange = StatusUpdateReasons.QUERY;
        OrderStruct newOrderStruct = OrderFactory.createDefaultOrderStruct();
        newStruct.orderStruct = newOrderStruct;

        SessionProduct product;

        try
        {
            product = APIHome.findProductQueryAPI().getProductByKeyForSession(
                    newOrderStruct.activeSession, newOrderStruct.productKey);
            newStruct.productInformation = product.getProductNameStruct();
        }
        catch(Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain product for new OrderDetail.");
        }

        return createOrderDetail(newStruct);
    }
}
