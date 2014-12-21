//
// -----------------------------------------------------------------------------------
// Source file: ProductConfigurationQueryServiceHome.java
//
// PACKAGE: com.cboe.interfaces.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.application;

public interface ProductConfigurationQueryServiceHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ProductConfigurationQueryServiceHome";

    /**
     * Creates an instance of the ProductConfigurationQueryService.
     */
    public ProductConfigurationQueryService create(SessionManager sessionManager);
}