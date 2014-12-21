//
// -----------------------------------------------------------------------------------
// Source file: PendingOrder.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.PendingOrderStruct;

import com.cboe.interfaces.presentation.product.PendingNameContainer;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Defines a contract that a PendingOrder that represents a PendingOrderStruct should provide.
 */
public interface PendingOrder extends BusinessModel
{
    /**
     * Gets the PendingName
     * @return PendingNameContainer
     */
    public PendingNameContainer getPendingName();

    /**
     * Gets the current order
     * @return current order
     */
    public Order getCurrentOrder();

    /**
     * Gets the pending order
     * @return pending order
     */
    public Order getPendingOrder();

    /**
     * Get the underlying struct
     * @deprecated here for backwards compatibility only
     */
    public PendingOrderStruct getStruct();
}