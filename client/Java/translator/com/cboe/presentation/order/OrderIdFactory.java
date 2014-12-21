//
// -----------------------------------------------------------------------------------
// Source file: OrderIdFactory.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.OrderIdStruct;

import com.cboe.interfaces.presentation.order.MutableOrderId;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.order.OrderEntry;
import com.cboe.interfaces.presentation.user.ExchangeFirm;

import com.cboe.util.FormatNotFoundException;

import com.cboe.presentation.userSession.UserSessionFactory;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.StructBuilder;

public class OrderIdFactory
{
    public static String ORDER_DATE_FORMAT = "OrderDateFormat";
    public static String ORDER_DATE_FORMAT_PATTERN = "yyyyMMdd";

    private static int highCboeId = 0;
    private static int lowCboeId = 0;

    public static OrderId createOrderId(OrderIdStruct orderIdStruct)
    {
        return new OrderIdImpl(orderIdStruct);
    }

    public static MutableOrderId createMutableOrderId(OrderIdStruct orderIdStruct)
    {
        return (MutableOrderId)createOrderId(orderIdStruct);
    }

    public static MutableOrderId convertToMutable(OrderId orderId)
    {
        return createMutableOrderId(orderId.getStruct());
    }

    public static MutableOrderId createMutableOrderId(OrderEntry orderEntry)
    {
        return createMutableOrderId(createDefaultStruct(orderEntry));
    }
    static OrderIdStruct createDefaultStruct()
    {
        OrderIdStruct newStruct = new OrderIdStruct();
        newStruct.branch = "";
        newStruct.branchSequenceNumber = 0;
        newStruct.correspondentFirm = "";

        ExchangeFirm firm = UserSessionFactory.findUserSession().getUserModel().getDefaultProfile().
                getExecutingGiveupFirm();
        if(firm == null)
        {
            newStruct.executingOrGiveUpFirm = StructBuilder.buildExchangeFirmStruct("", "");
        }
        else
        {
            newStruct.executingOrGiveUpFirm = firm.getExchangeFirmStruct();
        }

        newStruct.orderDate = "";
        try
        {
            DateWrapper dateWrapper = new DateWrapper();

            dateWrapper.addDateFormatter(ORDER_DATE_FORMAT, ORDER_DATE_FORMAT_PATTERN);

            newStruct.orderDate = dateWrapper.format(ORDER_DATE_FORMAT);
        }
        catch(FormatNotFoundException e)
        {
            // should not happen, but...
            throw new NullPointerException("Could not find date formatter: " + ORDER_DATE_FORMAT);
        }

        lowCboeId++;
        if(lowCboeId > 100)
        {
            highCboeId++;
            lowCboeId = 0;
        }

        newStruct.highCboeId = highCboeId;
        newStruct.lowCboeId = lowCboeId;

        return newStruct;
    }

    static OrderIdStruct createDefaultStruct(OrderEntry orderEntry)
    {
        OrderIdStruct newStruct = new OrderIdStruct();
        newStruct.branch = orderEntry.getBranch();
        newStruct.branchSequenceNumber = orderEntry.getBranchSequenceNumber().intValue();
        newStruct.correspondentFirm = orderEntry.getCorrespondentFirm();
        newStruct.executingOrGiveUpFirm = orderEntry.getExecutingOrGiveUpFirm().getExchangeFirmStruct();
        newStruct.orderDate = orderEntry.getOrderDate();
        newStruct.highCboeId = 0;
        newStruct.lowCboeId = 0;
        return newStruct;
    }
}