//
// -----------------------------------------------------------------------------------
// Source file: ProductKeys.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiProduct.ProductKeysStruct;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface ProductKeys extends BusinessModel
{
    /**
     * Gets the underlying struct
     * @return ProductKeysStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ProductKeysStruct getStruct();

    public int getClassKey();

    public int getReportingClassKey();

    public short getProductType();

    public int getProductKey();
}