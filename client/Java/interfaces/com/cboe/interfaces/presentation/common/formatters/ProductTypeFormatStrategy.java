// -----------------------------------------------------------------------------------
// Source file: ProductTypeFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.product.ProductType;

/**
 * Defines a contract for a class that formats ProductTypes
 */
public interface ProductTypeFormatStrategy extends FormatStrategy
{
    public static final String FULL_PRODUCT_TYPE_NAME = "Full Product Type Name";
    public static final String FULL_PRODUCT_TYPE_NAME_AND_DESCRIPTION = "Full Product Name and Desription";

    public static final String FULL_PRODUCT_TYPE_NAME_DESCRIPTION = "Full Product Type Name";
    public static final String FULL_PRODUCT_TYPE_NAME_AND_DESCRIPTION_DESCRIPTION = "Full Product Name and Desription.";

    /**
     * Defines a method for formatting ProductType
     * @param productType to format
     * @return formatted string
     */
    public String format(ProductType productType);

    /**
     * Defines a method for formatting ProductType
     * @param productType to format
     * @param format style to use
     * @return formatted string
     */
    public String format(ProductType productType, String style);

}
