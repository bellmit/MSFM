//
// -----------------------------------------------------------------------------------
// Source file: DropCopyMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.exceptions.*;
import com.cboe.interfaces.presentation.order.OrderDetail;

/**
 * Interface for non-order related (informational) OMT messages.
 */
@SuppressWarnings({"MarkerInterface"})
public interface DropCopyMessageElement extends InfoMessageElement
{
    public OrderStruct getOrderStruct();

    public OrderDetail getOrderDetail() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException;

    public OrderDetailStruct getOrderDetailStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException;
}
