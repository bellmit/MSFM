//
// -----------------------------------------------------------------------------------
// Source file: OrderCancelMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.interfaces.presentation.order.CancelRequest;
import com.cboe.interfaces.presentation.order.Order;

/**
 * Interface for a cancel order OMT message
 */
public interface OrderCancelMessageElement extends Order, MessageElement
{
    Order getOrder();
    long getIdentifier();
    CancelRequest getCancelRequest();
}
