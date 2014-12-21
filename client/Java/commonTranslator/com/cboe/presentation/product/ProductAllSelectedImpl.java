//
// -----------------------------------------------------------------------------------
// Source file: ProductAllSelectedImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;

import com.cboe.presentation.common.formatters.CommonFormatFactory;

/**
 * ProductClass implementation for an All Selected ProductClass for the OPTION type.
 */
class ProductAllSelectedImpl extends ProductDefaultImpl implements CustomKeys
{
    protected ProductFormatStrategy formatter = null;

    /**
     *  Default constructor.
     */
    protected ProductAllSelectedImpl()
    {
        super();
        formatter = CommonFormatFactory.getProductFormatStrategy();
//        formatter = FormatFactory.getProductFormatStrategy();
//        formatter.setCurrentStyle(formatter.FULL_PRODUCT_NAME);
    }

    public boolean isDefaultProduct()
    {
        return false;
    }

    public boolean isAllSelectedProduct()
    {
        return true;
    }

    /**
     * Get the product key for this Product.
     * @return Default product key for the ProductDefaultImpl is returned
     */
    public int getProductKey()
    {
        return ALL_SELECTED_PRODUCT_KEY;
    }
    /**
     * Get the product type of this product
     * @return short
     * @see com.cboe.idl.cmiConstants.ProductTypes
     */
    public short getProductType()
    {
        return ALL_SELECTED_PRODUCT_TYPE;
    }

    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new ProductAllSelectedImpl();
    }

    /**
     * If <code>obj</code> is an instance of this class true is return,
     * false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if( isEqual )
        {
            if( obj instanceof ProductAllSelectedImpl )
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }
        return isEqual;
    }

    /**
     * Returns a String representation of this Product.
     */
    public String toString()
    {
        return formatter.format(this, formatter.FULL_PRODUCT_NAME);
    }
}
