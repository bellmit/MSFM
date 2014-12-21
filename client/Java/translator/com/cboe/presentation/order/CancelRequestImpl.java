//
// -----------------------------------------------------------------------------------
// Source file: CancelRequestImpl.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.CancelRequestStruct;

import com.cboe.interfaces.presentation.order.CancelRequest;
import com.cboe.interfaces.presentation.order.OrderId;

class CancelRequestImpl implements CancelRequest
{
    protected OrderId orderId;
    protected String sessionName;
    protected String userAssignedCancelId;
    protected Short cancelType;
    protected Integer quantity;
    protected CancelRequestStruct cancelRequestStruct;
    public CancelRequestImpl(CancelRequestStruct cancelRequestStruct)
    {
        this.cancelRequestStruct = cancelRequestStruct;
        initialize();
    }

    private void initialize()
    {
        orderId = OrderIdFactory.createOrderId(cancelRequestStruct.orderId);
        sessionName = new String(cancelRequestStruct.sessionName);
        userAssignedCancelId = new String(cancelRequestStruct.userAssignedCancelId);
        cancelType = new Short(cancelRequestStruct.cancelType);
        quantity = new Integer(cancelRequestStruct.quantity);
    }

    public OrderId getOrderId()
    {
        return orderId;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public String getUserAssignedCancelId()
    {
        return userAssignedCancelId;
    }

    public Short getCancelType()
    {
        return cancelType;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    /**
     * Gets the underlying struct
     * @return CancelRequestStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public CancelRequestStruct getStruct()
    {
        return cancelRequestStruct;
    }
}
