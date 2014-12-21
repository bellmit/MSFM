/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Jan 2, 2003 10:01:16 AM
 */
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.FillRejectStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.FillReject;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.presentation.order.Order;

import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.presentation.order.OrderFactory;

class FillRejectImpl implements FillReject
{
    CBOEId tradeId;
    Order order;
    Integer transactionSequenceNumber;
    Short rejectReason;
    String extensions;

    FillRejectStruct fillRejectStruct;
    public FillRejectImpl(FillRejectStruct fillRejectStruct)
    {
        this.fillRejectStruct = fillRejectStruct;
        initialize();
    }

    private void initialize()
    {
        tradeId = new CBOEIdImpl(fillRejectStruct.tradeId);
        order = OrderFactory.createOrder(fillRejectStruct.order);

        transactionSequenceNumber = new Integer(fillRejectStruct.transactionSequenceNumber);
        rejectReason = new Short(fillRejectStruct.rejectReason);
        extensions = new String(fillRejectStruct.extensions);
    }

    public CBOEId getTradeId()
    {
        return tradeId;
    }

    public Order getOrder()
    {
        return order;
    }

    public Integer getTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    public Short getRejectReason()
    {
        return rejectReason;
    }

    public String getExtensions()
    {
        return extensions;
    }

    public FillRejectStruct toStruct()
    {
        return fillRejectStruct;
    }
}
