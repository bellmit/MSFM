//
// -----------------------------------------------------------------------------------
// Source file: SAProductFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.Strategy;

/**
 *  Factory for creating instances of Product
 */
public class SAProductFactory extends AbstractProductFactory
{
    /**
     * Creates an instance of a Product from a ProductStruct.
     * @param productStruct to wrap in instance of Product
     * @return Product to represent the ProductStruct
     */
    public Product create(ProductStruct productStruct)
    {
        if (productStruct == null)
        {
            throw new IllegalArgumentException();
        }

        Product product;

        if ( productStruct.productKeys.productType == ProductTypes.FUTURE )
        {
            product = new SAFutureProductImpl(productStruct);
        }
        else
        {
            product = new SAProductImpl(productStruct);
        }

        return product;
    }

    public Strategy create(StrategyStruct strategyStruct)
    {
        if (strategyStruct == null)
        {
            throw new IllegalArgumentException();
        }
        Strategy strategy;
        strategy = new SAStrategyImpl(strategyStruct);

        return strategy;
    }
}
