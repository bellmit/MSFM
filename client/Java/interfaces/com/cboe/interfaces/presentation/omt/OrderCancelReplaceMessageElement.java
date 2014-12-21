//
// -----------------------------------------------------------------------------------
// Source file: OrderCancelReplaceMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.interfaces.presentation.order.Order;

public interface OrderCancelReplaceMessageElement extends OrderCancelMessageElement
{
    Order getOriginalOrder();
    Order getReplacementOrder();
}