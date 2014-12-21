//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderDetail.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;

import com.cboe.interfaces.presentation.product.ProductName;

/**
 * for order entry - order detail
 */
public interface HeldOrderDetail
{
    /**
     * Gets the underlying struct
     * @return HeldOrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public HeldOrderDetailStruct getStruct();

    public void setStruct(HeldOrderDetailStruct heldOrderDetailStruct);

    public ProductName getProductInformation();

    public Short getStatusChange();

    public HeldOrder getHeldOrder();
}