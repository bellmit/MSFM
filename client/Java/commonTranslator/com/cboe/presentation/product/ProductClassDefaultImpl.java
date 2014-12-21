//
// -----------------------------------------------------------------------------------
// Source file: ProductClassDefaultImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.Product;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.ProductTypes;

/**
 * ProductClass implementation for a Default ProductClass for the OPTION type.
 */
class ProductClassDefaultImpl extends AbstractProductClass implements CustomKeys
{
    private static final String DEFAULT_STRING = "Default";

    /**
     *  Default constructor.
     */
    protected ProductClassDefaultImpl()
    {
        super();
    }

    public boolean isAllSelectedProductClass()
    {
        return false;
    }

    public boolean isDefaultProductClass()
    {
        return true;
    }

    /**
     * Get the product type for this ProductClass.
     * @return product type of OPTION is fixed.
     */
    public short getProductType()
    {
        return ProductTypes.OPTION;
    }

    /**
     * Get the ClassStruct that this ProductClass represents.
     * @return Null is returned
     * @deprecated
     */
    public ClassStruct getClassStruct()
    {
        return null;
    }

    /**
     * Get the class symbol for this ProductClass.
     * @return Default string for the ProductClassDefaultImpl is returned
     */
    public String getClassSymbol()
    {
        return DEFAULT_STRING;
    }

    /**
     * Get the class key for this ProductClass.
     * @return Default class key for the ProductClassDefaultImpl is returned
     */
    public int getClassKey()
    {
        return DEFAULT_CLASS_KEY;
    }

    /**
     * Gets the ListingState for this ProductClass.
     * @return ListingState of UNLISTED is fixed.
     */
    public short getListingState()
    {
        return ListingStates.UNLISTED;
    }

    /**
     * Get the underlying product for this ProductClass.
     * @return Null is returned
     */
    public Product getUnderlyingProduct()
    {
        return null;
    }

    /**
     * Get the primary exchange for this ProductClass.
     * @return Null is returned
     */
    public String getPrimaryExchange()
    {
        return null;
    }

    /**
     * Get the activation date for this ProductClass.
     * @return Null is returned
     */
    public DateStruct getActivationDate()
    {
        return null;
    }

    /**
     * Get the inactivation date for this ProductClass.
     * @return Null is returned
     */
    public DateStruct getInactivationDate()
    {
        return null;
    }

    /**
     * Get the created time for this ProductClass.
     * @return Null is returned
     */
    public DateTimeStruct getCreatedTime()
    {
        return null;
    }

    /**
     * Get the last modified time for this ProductClass.
     * @return Null is returned
     */
    public DateTimeStruct getLastModifiedTime()
    {
        return null;
    }

    /**
     * Get the EPW Struct values for this ProductClass.
     * @return an zero length array is returned
     */
    public EPWStruct[] getEPWValues()
    {
        return new EPWStruct[0];
    }

    /**
     * Get the EPW fast market multiplier for this ProductClass.
     * @return zero is returned
     */
    public double getEPWFastMarketMultiplier()
    {
        return 0;
    }

    /**
     * Get the product description struct for this ProductClass.
     * @return Null is returned
     */
    public ProductDescriptionStruct getProductDescription()
    {
        return null;
    }

    /**
     * Default to false
     */
    public boolean isTestClass()
    {
        return false;
    }


    /**
     * getPost() and getStation() default to null.
     * @return Null is returned
     */
    public String getPost()
    {
        return null;
    }
    public String getStation()
    {
        return null;
    }


    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new ProductClassDefaultImpl();
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

        if(!isEqual)
        {
            if (obj instanceof ProductClassDefaultImpl )
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
     * Returns a String representation of this ProductClass. Returns
     * the same as the <code>getClassSymbol</code> method.
     */
    public String toString()
    {
        return getClassSymbol();
    }

}
