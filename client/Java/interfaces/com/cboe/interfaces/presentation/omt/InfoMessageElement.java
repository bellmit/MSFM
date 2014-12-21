//
// -----------------------------------------------------------------------------------
// Source file: InfoMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderId;

/**
 * Interface for non-order related (informational) OMT messages.
 */
public interface InfoMessageElement extends MessageElement {

    String getBranchSeqNum();

    String getGiveUpFirm();

    /**
     * Get the associated order if immediately available.
     * 
     * <p>
     * This method may return null even if there is a value for {@link #getOrderId()}, as it will only be filled in if
     * it is available immediately.
     * </p>
     * 
     * @return The order if available, else null.
     */
    Order getOrder();

    /**
     * Fetch the relevant order for the info message.
     * 
     * @return An order id, or null if none could be found.
     */
    OrderId getOrderId();
}
