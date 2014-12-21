//
// -----------------------------------------------------------------------------------
// Source file: ProductTypeSupport.java
//
// PACKAGE: com.cboe.interfaces.presentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

public interface ProductTypeSupport
{
    /**
     * @return constant from com.cboe.idl.cmiConstants.ProductTypes if table is
     *         populated with products, otherwise returns -1
     */
    short getProductType();
}
