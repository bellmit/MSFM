//
// -----------------------------------------------------------------------------------
// Source file: CancelRequest.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.CancelRequestStruct;

public interface CancelRequest
{
    /**
     * Gets the underlying struct
     * @return CancelRequestStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public CancelRequestStruct getStruct();

    public OrderId getOrderId();

    public String getSessionName();

    public String getUserAssignedCancelId();

    public Short getCancelType();

    public Integer getQuantity();
}