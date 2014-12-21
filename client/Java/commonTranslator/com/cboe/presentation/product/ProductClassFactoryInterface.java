//
// -----------------------------------------------------------------------------------
// Source file: ProductClassFactoryInterface.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.idl.cmiProduct.ClassStruct;


/**
 *  Factory for creating instances of ProductClass
 */
public interface ProductClassFactoryInterface
{
    ProductClass create(ClassStruct classStruct);

    ProductClass createAllSelected();

    ProductClass createDefault();

    ProductClass createInvalid(int classKey);
}
