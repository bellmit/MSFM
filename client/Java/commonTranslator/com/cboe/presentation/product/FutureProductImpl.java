// -----------------------------------------------------------------------------------
// Source file: FutureProductImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.FutureProduct;

import com.cboe.idl.cmiProduct.ProductStruct;


/**
 * FutureProduct implementation
 */
class FutureProductImpl extends ProductImpl implements FutureProduct
{
    /**
     * Constructor
     * @param productStruct to represent
     */
    protected FutureProductImpl(ProductStruct productStruct)
    {
        super(productStruct);
    }
    /**
     *  Default constructor.
     */
    protected FutureProductImpl()
    {
        super();
    }
}

