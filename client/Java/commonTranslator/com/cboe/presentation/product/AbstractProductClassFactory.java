//
// -----------------------------------------------------------------------------------
// Source file: AbstractProductClassFactory.java
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
public abstract class AbstractProductClassFactory implements ProductClassFactoryInterface
{
    private static ProductClass allSelectedProductClass = null;
    private static ProductClass defaultProductClass = null;

    /**
     * Returns the ProductClass representing "All Selected"
     * @return ProductClass to represent all ProductClass'es for a set
     * context.
     */
    public ProductClass createAllSelected()
    {
        if(allSelectedProductClass == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("ProductClassFactory.createAllSelected() caching ProductClassAllSelectedImpl", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
            allSelectedProductClass = new ProductClassAllSelectedImpl();
        }

        return allSelectedProductClass;
    }

    /**
     * Returns the ProductClass representing "Default"
     * @return ProductClass to represent the Default ProductClass for a set
     * context.
     */
    public ProductClass createDefault()
    {
        if(defaultProductClass == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("ProductClassFactory.createDefault() caching ProductClassDefaultImpl", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
            defaultProductClass = new ProductClassDefaultImpl();
        }

        return defaultProductClass;
    }

    public ProductClass createInvalid(int classKey)
    {
        return new InvalidProductClassImpl(classKey);
    }
}
