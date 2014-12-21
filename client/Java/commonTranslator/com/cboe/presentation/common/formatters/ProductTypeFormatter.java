// -----------------------------------------------------------------------------------
// Source file: ProductTypeFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.product.ProductType;

import com.cboe.interfaces.presentation.common.formatters.ProductTypeFormatStrategy;
import com.cboe.presentation.common.formatters.Formatter;

/**
 * Implements the ProductTypeFormatStrategy
 */
class ProductTypeFormatter extends Formatter implements ProductTypeFormatStrategy
{
    /**
     * Constructor, defines styles and sets initial default style
     */
    public ProductTypeFormatter()
    {
        super();

        addStyle(FULL_PRODUCT_TYPE_NAME, FULL_PRODUCT_TYPE_NAME_DESCRIPTION);
        addStyle(FULL_PRODUCT_TYPE_NAME_AND_DESCRIPTION, FULL_PRODUCT_TYPE_NAME_AND_DESCRIPTION_DESCRIPTION);

        setDefaultStyle(FULL_PRODUCT_TYPE_NAME);
    }

    /**
     * Formats a ProductType
     * @param product to format
     */
    public String format(ProductType productType)
    {
        return format(productType, getDefaultStyle());
    }


    /**
     * Formats a ProductType
     * @param productType to format
     */
    public String format(ProductType productType, String style) throws IllegalArgumentException
    {
        String productTypeText = null;

        if ( ! this.containsStyle(style) )
        {
            throw new IllegalArgumentException("ProductTypeFormatter - Unknown Style");
        }
        if(style.equals(FULL_PRODUCT_TYPE_NAME))
        {
            productTypeText = formatFullProductTypeName(productType);
        }
        else if(style.equals(FULL_PRODUCT_TYPE_NAME_AND_DESCRIPTION))
        {
            productTypeText = formatFullProductTypeNameAndDescription(productType);
        }
        return productTypeText;
    }

    /**
     * Formats a Product using FULL_PRODUCT_TYPE_NAME style
     * @param productType to format
     */
    private String formatFullProductTypeName(ProductType productType)
    {
        return productType.getName();
    }

    /**
     * Formats a Product using FULL_PRODUCT_TYPE_NAME style
     * @param productType to format
     */
    private String formatFullProductTypeNameAndDescription(ProductType productType)
    {
        StringBuffer text = new StringBuffer(productType.getName());
        text.append(", ").append(productType.getDescription());
        return text.toString();
    }
}
