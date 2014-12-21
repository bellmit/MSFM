//
// ------------------------------------------------------------------------
// Source file: ProductKeysFactory.java
//
// PACKAGE: com.cboe.presentation.product
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.interfaces.presentation.product.ProductKeys;

public class ProductKeysFactory
{
    public static ProductKeys createProductKeys(ProductKeysStruct productKeysStruct)
    {
        return new ProductKeysImpl(productKeysStruct);
    }
}