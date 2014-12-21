//
// -----------------------------------------------------------------------------------
// Source file: ProductNameImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.presentation.product.ProductName;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.DateImpl;

class ProductNameImpl implements ProductName
{
    private ProductNameStruct   productNameStruct;
    private String              reportingClass;
    private Price               exercisePrice;
    private Date                expirationDate;
    private char                optionType;
    private String              productSymbol;
    public ProductNameImpl(ProductNameStruct productNameStruct)
    {
        this.productNameStruct = productNameStruct;
        initialize();
    }

    private void initialize()
    {
        if(productNameStruct.reportingClass != null)
        {
            reportingClass  = new String(productNameStruct.reportingClass);
        }
        else
        {
            reportingClass = "";
        }
        exercisePrice   = DisplayPriceFactory.create(productNameStruct.exercisePrice);
        expirationDate  = new DateImpl(productNameStruct.expirationDate);
        optionType      = productNameStruct.optionType;
        if(productNameStruct.productSymbol != null)
        {
            productSymbol   = new String(productNameStruct.productSymbol);
        }
        else
        {
            productSymbol = "";
        }
    }

    public String getReportingClass()
    {
        return reportingClass;
    }

    public Price getExercisePrice()
    {
        return exercisePrice;
    }

    public Date getExpirationDate()
    {
        return expirationDate;
    }

    public char getOptionType()
    {
        return optionType;
    }

    public String getProductSymbol()
    {
        return productSymbol;
    }

    /**
     * Gets the underlying struct
     * @return ProductNameStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ProductNameStruct getStruct()
    {
        return productNameStruct;
    }
}
