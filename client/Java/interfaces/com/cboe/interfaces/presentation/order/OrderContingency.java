//
// -----------------------------------------------------------------------------------
// Source file: OrderContingency.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.OrderContingencyStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface OrderContingency extends BusinessModel
{
    /**
     * Gets the underlying struct
     * @return OrderContingencyStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderContingencyStruct getStruct();

    public short getType();

    public Price getPrice();

    public int getVolume();
}