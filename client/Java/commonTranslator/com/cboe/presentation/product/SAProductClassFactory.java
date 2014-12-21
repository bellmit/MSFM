//
// -----------------------------------------------------------------------------------
// Source file: SAProductClassFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 *  Factory for creating instances of ProductClass
 */
public class SAProductClassFactory extends AbstractProductClassFactory
{
    /**
     * Creates an instance of a ProductClass from a ClassStruct.
     * @param classStruct to wrap in instance of ProductClass
     * @return ProductClass to represent the ClassStruct
     */
    public ProductClass create(ClassStruct classStruct)
    {
        if (classStruct == null)
        {
            throw new IllegalArgumentException();
        }
        ProductClass productClass;
        productClass = new SAProductClassImpl(classStruct);

        return productClass;
    }
}
