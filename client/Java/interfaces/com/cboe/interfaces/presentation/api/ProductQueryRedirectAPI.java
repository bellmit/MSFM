//
// -----------------------------------------------------------------------------------
// Source file: ProductQueryRedirectAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.idl.cmi.ProductQuery;

public interface ProductQueryRedirectAPI extends ProductQueryAPI
{
    public void setProductQuery(ProductQuery query);
}