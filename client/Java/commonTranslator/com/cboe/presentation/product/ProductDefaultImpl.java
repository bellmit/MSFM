//
// -----------------------------------------------------------------------------------
// Source file: ProductDefaultImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;

import com.cboe.presentation.common.formatters.CommonFormatFactory;

import com.cboe.domain.util.StructBuilder;

/**
 * Product implementation for a Default Product for the OPTION type.
 */
class ProductDefaultImpl extends AbstractProduct implements CustomKeys
{
    public static final String DEFAULT_SYMBOL = "Default";
    public static final String DEFAULT_REPORTING_CLASS = DEFAULT_SYMBOL;

    protected ProductFormatStrategy formatter = null;

    /**
     *  Default constructor.
     */
    protected ProductDefaultImpl()
    {
        super();
        formatter = CommonFormatFactory.getProductFormatStrategy();
//        formatter.setCurrentStyle(formatter.FULL_PRODUCT_NAME);
    }

    public boolean isDefaultProduct()
    {
        return true;
    }

    public boolean isAllSelectedProduct()
    {
        return false;
    }

    /**
     * Get the ProductStruct that this Product represents.
     * @return Null is returned
     * @deprecated
     */
    public ProductStruct getProductStruct()
    {
        return null;
    }

    /**
     * Get the product key for this Product.
     * @return Default product key for the ProductDefaultImpl is returned
     */
    public int getProductKey()
    {
        return DEFAULT_PRODUCT_KEY;
    }
    /**
     * Get the product type of this product
     * @return short
     * @see com.cboe.idl.cmiConstants.ProductTypes
     */
    public short getProductType()
    {
        return DEFAULT_PRODUCT_TYPE;
    }

    /**
     * Get the ProductKeysStruct for this Product.
     * @return null is returned
     */
    public ProductKeysStruct getProductKeysStruct()
    {
        return new ProductKeysStruct(getProductKey(), DEFAULT_CLASS_KEY, getProductType(), DEFAULT_CLASS_KEY);
    }

    /**
     * Get the ProductNameStruct for this Product.
     * @return Null is returned
     */
    public ProductNameStruct getProductNameStruct()
    {
        ProductNameStruct newStruct = new ProductNameStruct();
        newStruct.exercisePrice = new PriceStruct(PriceTypes.VALUED, 0, 0);
        newStruct.expirationDate = StructBuilder.buildDateStruct();
        newStruct.optionType = OptionTypes.CALL;
        newStruct.productSymbol = DEFAULT_SYMBOL;
        newStruct.reportingClass = DEFAULT_REPORTING_CLASS;
        return newStruct;
    }

    /**
     * Get the listing state for this Product.
     * @return fixed to return ListingStates.UNLISTED
     */
    public short getListingState()
    {
        return ListingStates.UNLISTED;
    }

    /**
     * Get the description for this Product.
     * @return Null is returned
     */
    public String getDescription()
    {
        return null;
    }

    /**
     * Get the company name for this Product.
     * @return Null is returned
     */
    public String getCompanyName()
    {
        return null;
    }

    /**
     * Get the unit of measure for this Product.
     * @return Null is returned
     */
    public String getUnitMeasure()
    {
        return null;
    }

    /**
     * Get the standard qty for this Product.
     * @return Zero is returned
     */
    public double getStandardQuantity()
    {
        return 0;
    }

    /**
     * Get the maturity date for this Product.
     * @return Null is returned
     */
    public DateStruct getMaturityDate()
    {
        return null;
    }

    /**
     * Get the activation date for this Product.
     * @return Null is returned
     */
    public DateStruct getActivationDate()
    {
        return null;
    }

    /**
     * Get the inactivation date for this Product.
     * @return Null is returned
     */
    public DateStruct getInactivationDate()
    {
        return null;
    }

    /**
     * Get the created time for this Product.
     * @return Null is returned
     */
    public DateTimeStruct getCreatedTime()
    {
        return null;
    }

    /**
     * Get the last modified time for this Product.
     * @return Null is returned
     */
    public DateTimeStruct getLastModifiedTime()
    {
        return null;
    }

    /**
     * Get the opra month code for this Product.
     * @return the space character is returned?
     */
    public char getOpraMonthCode()
    {
        return ' ';
    }

    /**
     * Get the opra price code for this Product.
     * @return the space character is returned?
     */
    public char getOpraPriceCode()
    {
        return ' ';
    }

    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new ProductDefaultImpl();
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

        if( !isEqual )
        {
            if( obj instanceof ProductDefaultImpl )
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
     //   return formatter.format(this, formatter.FULL_PRODUCT_NAME);
        return formatter.format(this, formatter.FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE);
    }
}
