//
// -----------------------------------------------------------------------------------
// Source file: FilledReport.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.interfaces.presentation.product.ProductContainer;
import com.cboe.interfaces.presentation.product.ProductName;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface OrderDetail extends ProductContainer, BusinessModel
{
    /**
     * Gets the underlying struct
     * @return OrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderDetailStruct getStruct();

    public ProductName getProductName();
    public Short getStatusChange();
    public Order getOrder();
}