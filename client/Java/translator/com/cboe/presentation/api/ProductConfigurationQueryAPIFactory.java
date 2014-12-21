//
// -----------------------------------------------------------------------------------
// Source file: ProductConfigurationQueryAPIFactory.java
//
// PACKAGE: com.cboe.pcqsPresentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.idl.internalBusinessServices.ProductConfigurationQueryService;

import com.cboe.interfaces.presentation.productConfiguration.ProductConfigurationQueryAPI;

public class ProductConfigurationQueryAPIFactory
{
    private static ProductConfigurationQueryAPI pcqsAPI;

    private ProductConfigurationQueryAPIFactory()
    {
    }

    public static ProductConfigurationQueryAPI create(ProductConfigurationQueryService pcqs)
    {
        if(pcqs == null)
        {
            throw new IllegalArgumentException("ProductConfigurationQueryService cannot be null");
        }
        pcqsAPI = new ProductConfigurationQueryAPIImpl(pcqs);
        return pcqsAPI;
    }

    public static ProductConfigurationQueryAPI find()
    {
        if(pcqsAPI == null)
        {
            throw new IllegalStateException("ProductConfigurationQueryAPIFactory: Create has not been called yet.");
        }
        return pcqsAPI;
    }
}
