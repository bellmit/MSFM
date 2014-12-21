//
// -----------------------------------------------------------------------------------
// Source file: OrderEntryFactory.java
//
// PACKAGE: com.cboe.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.OrderEntryStruct;

import com.cboe.interfaces.presentation.order.OrderEntry;
import com.cboe.interfaces.presentation.order.MutableOrderEntry;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderContingency;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;

import com.cboe.util.FormatNotFoundException;

import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;

import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.DateWrapper;

public class OrderEntryFactory
{
    public static OrderEntry createOrderEntry(OrderEntryStruct orderEntryStruct)
    {
        return new OrderEntryImpl(orderEntryStruct);
    }

    public static MutableOrderEntry createMutableOrderEntry(OrderEntryStruct orderEntryStruct)
    {
        return (MutableOrderEntry)createOrderEntry(orderEntryStruct);
    }

    public static OrderEntry createOrderEntry(Order order)
    {
        OrderEntryImpl newImpl = new OrderEntryImpl();
        newImpl.setAccount(order.getAccount());
        newImpl.setBranch(order.getOrderId().getBranch());
        newImpl.setBranchSequenceNumber(order.getOrderId().getBranchSequenceNumber());
        try
        {
            newImpl.setCmta((ExchangeFirm)order.getCmta().clone());
        }
        catch(CloneNotSupportedException e)
        {
            newImpl.setCmta(ExchangeFirmFactory.createExchangeFirm(order.getCmta().getExchangeFirmStruct()));
        }
        try
        {
            newImpl.setContingency((OrderContingency)order.getContingency().clone());
        }
        catch(CloneNotSupportedException e)
        {
            newImpl.setContingency(OrderContingencyFactory.createOrderContingency(order.getContingency().getStruct()));
        }
        newImpl.setCorrespondentFirm(order.getOrderId().getCorrespondentFirm());
        newImpl.setCoverage(order.getCoverage());
        newImpl.setCross(order.isCross());
        try
        {
            newImpl.setExecutingOrGiveUpFirm((ExchangeFirm)order.getOrderId().getExecutingOrGiveUpFirm().clone());
        }
        catch(CloneNotSupportedException e)
        {
            newImpl.setExecutingOrGiveUpFirm(ExchangeFirmFactory.createExchangeFirm(order.getOrderId().
                                                                                    getExecutingOrGiveUpFirm().
                                                                                    getExchangeFirmStruct()));
        }
        newImpl.setExpireTime(new DateTimeImpl(order.getExpireTime().getDateTimeStruct()));
        newImpl.setExtensions(order.getExtensions());
        newImpl.setOptionalData(order.getOptionalData());
        newImpl.setOrderDate(order.getOrderId().getOrderDate());
        newImpl.setOrderNBBOProtectionType(order.getOrderNBBOProtectionType());
        newImpl.setOrderOriginType(order.getOrderOriginType());
        newImpl.setOriginalQuantity(order.getOriginalQuantity());
        try
        {
            newImpl.setOriginator((ExchangeAcronym)order.getOriginator().clone());
        }
        catch(CloneNotSupportedException e)
        {
            newImpl.setOriginator(ExchangeAcronymFactory.createExchangeAcronym(order.getOriginator().
                                                                               getExchangeAcronymStruct()));
        }
        newImpl.setPositionEffect(order.getPositionEffect());
        newImpl.setPrice(DisplayPriceFactory.create(order.getPrice().toStruct()));
        newImpl.setProductKey(order.getProductKey());
        newImpl.setSide(order.getSide());
        newImpl.setSubaccount(order.getSubaccount());
        newImpl.setTimeInForce(order.getTimeInForce());
        newImpl.setUserAssignedId(order.getUserAssignedId());
        newImpl.sessionNames = order.getSessionNames();
        
        return newImpl;
    }

    public static MutableOrderEntry createMutableOrderEntry(Order order)
    {
        return (MutableOrderEntry)createOrderEntry(order);
    }

    static OrderEntryStruct createDefaultStruct()
    {
        OrderEntryStruct newStruct = new OrderEntryStruct();
        newStruct.account = "";
        newStruct.branch = "";
        newStruct.branchSequenceNumber = 0;
        newStruct.correspondentFirm = "";
        newStruct.cmta = StructBuilder.buildExchangeFirmStruct("", "");
        newStruct.contingency = OrderContingencyFactory.createDefaultStruct();
        newStruct.coverage = ' ';
        newStruct.cross = false;
        newStruct.expireTime = StructBuilder.buildDateTimeStruct();
        newStruct.extensions = "";
        newStruct.executingOrGiveUpFirm = StructBuilder.buildExchangeFirmStruct("", "");
        newStruct.optionalData = "";
        newStruct.originalQuantity = 0;
        newStruct.orderOriginType = ' ';
        newStruct.positionEffect = ' ';
        newStruct.price = StructBuilder.buildPriceStruct();
        newStruct.productKey = 0;
        newStruct.side = 0;
        newStruct.subaccount = "";
        newStruct.timeInForce = ' ';
        newStruct.userAssignedId = "";
        newStruct.originator = StructBuilder.buildExchangeAcronymStruct("", "");
        newStruct.sessionNames = new String[0];

        newStruct.orderDate = "";
        try
        {
            DateWrapper dateWrapper = new DateWrapper();

            dateWrapper.addDateFormatter(OrderIdFactory.ORDER_DATE_FORMAT, OrderIdFactory.ORDER_DATE_FORMAT_PATTERN);

            newStruct.orderDate = dateWrapper.format(OrderIdFactory.ORDER_DATE_FORMAT);
        }
        catch(FormatNotFoundException e)
        {
            // should not happen, but...
            throw new NullPointerException("Could not find date formatter: " + OrderIdFactory.ORDER_DATE_FORMAT);
        }

        return newStruct;
    }
}