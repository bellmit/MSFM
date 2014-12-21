// -----------------------------------------------------------------------------------
// Source file: ProductTypeFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.interfaces.presentation.product.ProductType;

/**
 *  Factory for creating instances of ProductType
 */
public class ProductTypeFactory
{
    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ProductTypeFactory()
    {}

    /**
     * Creates an instance of a ProductType from a ProductTypeStruct.
     * @param productTypeStruct to wrap in instance of ProductType
     * @return ProductType to represent the ProductTypeStruct
     */
    public static ProductType create(ProductTypeStruct productTypeStruct)
    {
        if (productTypeStruct == null)
        {
            throw new IllegalArgumentException();
        }
        ProductType productType = new ProductTypeImpl(productTypeStruct);

        return productType;
    }
}
