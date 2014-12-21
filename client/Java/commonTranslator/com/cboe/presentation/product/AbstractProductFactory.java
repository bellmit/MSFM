//
// -----------------------------------------------------------------------------------
// Source file: AbstractProductFactory.java
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
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.product.StrategyLeg;
import com.cboe.interfaces.presentation.product.SessionStrategyLeg;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 *  Factory for creating instances of Product
 */
public abstract class AbstractProductFactory implements ProductFactoryInterface
{
    private static Product allSelectedProduct = null;
    private static Product defaultProduct = null;
    private static Strategy defaultStrategy = null;

    /**
     * Creates an instance of a StrategyLeg from a StrategyLegStruct.
     * @param aStrategyLegStruct
     * @return strategyLeg
     * @author Nick DePasquale
     * @version 08/16/01
     */
    public StrategyLeg create(StrategyLegStruct aStrategyLegStruct)
    {
        if (aStrategyLegStruct == null)
        {
            throw new IllegalArgumentException();
        }
        StrategyLeg strategyLeg;
        strategyLeg = new StrategyLegImpl(aStrategyLegStruct);

        return strategyLeg;
    }

    /**
     * Creates an instance of a SessionStrategyLeg from a SessionStrategyLegStruct.
     * @param aSessionStrategyLegStruct
     * @return sessionStrategyLeg
     * @author Nick DePasquale
     * @version 08/16/01
     */
    public SessionStrategyLeg create(SessionStrategyLegStruct aSessionStrategyLegStruct)
    {
        if (aSessionStrategyLegStruct == null)
        {
            throw new IllegalArgumentException();
        }
        SessionStrategyLeg sessionStrategyLeg;
        sessionStrategyLeg = new SessionStrategyLegImpl(aSessionStrategyLegStruct);

        return sessionStrategyLeg;
    }

    /**
     * Returns the Product representing "All Selected"
     * @return Product to represent all Product's for a set
     * context.
     */
    public Product createAllSelected()
    {
        if(allSelectedProduct == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("ProductFactory.createAllSelected() caching ProductAllSelectedImpl", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
            allSelectedProduct = new ProductAllSelectedImpl();
        }

        return allSelectedProduct;
    }

    /**
     * Returns the Product representing "Default"
     * @return Product to represent the Default Product for a set
     * context.
     */
    public Product createDefault()
    {
        if(defaultProduct == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("ProductFactory.createDefault() caching ProductDefaultImpl", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
            defaultProduct = new ProductDefaultImpl();
        }

        return defaultProduct;
    }

    /**
     * Returns an "invalid" product that could not be found via lookup by product key.
     * @param productKey  the product key for which product lookup failed.
     *
     */
    public InvalidProductImpl createInvalidProduct(int productKey)
    {
        return new InvalidProductImpl(productKey);
    }

    /**
     * Returns the Strategy representing "Default"
     * @return Strategy to represent the Default Strategy for a set
     * context.
     */
    public Strategy createDefaultStrategy()
    {
        if(defaultStrategy == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("ProductFactory.createDefaultStrategy() caching StrategyDefaultImpl",
                        GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
            defaultStrategy = new StrategyDefaultImpl();
        }
        return defaultStrategy;
    }
}
