//
// -----------------------------------------------------------------------------------
// Source file: PendingOrderFactory.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.PendingOrderStruct;

import com.cboe.interfaces.presentation.order.PendingOrder;

public class PendingOrderFactory
{
    public static PendingOrder create(PendingOrderStruct struct)
    {
        return new PendingOrderImpl(struct);
    }
}