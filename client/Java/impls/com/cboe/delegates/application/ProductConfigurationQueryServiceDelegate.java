//
// -----------------------------------------------------------------------------------
// Source file: ProductConfigurationQueryServiceDelegate.java
//
// PACKAGE: com.cboe.delegates.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.delegates.application;

import com.cboe.interfaces.application.ProductConfigurationQueryService;

public class ProductConfigurationQueryServiceDelegate extends com.cboe.idl.internalBusinessServices.POA_ProductConfigurationQueryService_tie
{
    public ProductConfigurationQueryServiceDelegate(ProductConfigurationQueryService delegate)
    {
        super(delegate);
    }
}
