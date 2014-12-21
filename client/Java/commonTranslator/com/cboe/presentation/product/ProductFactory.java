//
// -----------------------------------------------------------------------------------
// Source file: ProductFactory.java
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
public class ProductFactory extends AbstractProductFactory
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
            product = new FutureProductImpl(productStruct);
        }
        else
        {
            product = new ProductImpl(productStruct);
        }

        return product;
    }

    /**
     * Creates an instance of a Strategy from a StrategyStruct.
     * @param strategyStruct
     * @return Strategy
     * @author Jing Chen
     * @version 08/06/01
     */
    public Strategy create(StrategyStruct strategyStruct)
    {
        if (strategyStruct == null)
        {
            throw new IllegalArgumentException();
        }
        Strategy strategy;
        strategy = new StrategyImpl(strategyStruct);

        return strategy;
    }
}
