//
// -----------------------------------------------------------------------------------
// Source file: ProductName.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiProduct.ProductNameStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Date;

public interface ProductName
{
    /**
     * Gets the underlying struct
     * @return ProductNameStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ProductNameStruct getStruct();

    public String getReportingClass();

    public Price getExercisePrice();

    public Date getExpirationDate();

    public char getOptionType();

    public String getProductSymbol();
}