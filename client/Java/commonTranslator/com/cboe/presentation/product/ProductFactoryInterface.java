//
// -----------------------------------------------------------------------------------
// Source file: ProductFactoryInterface.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.product.StrategyLeg;
import com.cboe.interfaces.presentation.product.SessionStrategyLeg;

/**
 *  Factory for creating instances of Product
 */
public interface ProductFactoryInterface
{
    Product create(ProductStruct productStruct);

    Strategy create(StrategyStruct strategyStruct);

    StrategyLeg create(StrategyLegStruct aStrategyLegStruct);

    SessionStrategyLeg create(SessionStrategyLegStruct aSessionStrategyLegStruct);

    Product createAllSelected();

    Product createDefault();

    InvalidProductImpl createInvalidProduct(int productKey);

    Strategy createDefaultStrategy();
}
