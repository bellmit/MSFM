//
// -----------------------------------------------------------------------------------
// Source file: ProductNameFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ProductNameStruct;

import com.cboe.interfaces.presentation.product.ProductName;

public class ProductNameFactory
{
    public static ProductName createProductName(ProductNameStruct productNameStruct)
    {
        return new ProductNameImpl(productNameStruct);
    }
}