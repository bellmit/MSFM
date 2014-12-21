//
// -----------------------------------------------------------------------------------
// Source file: InvalidProductImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.StrategyTypes;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

import com.cboe.domain.util.StructBuilder;


/**
 * Product implementation for an Invalid Product not found by the product key.
 */
class InvalidProductImpl extends ProductDefaultImpl
{
     protected int productKey;

    /**
     *  Constructor takes product key for which no product was found during lookup by key.
     */
    protected InvalidProductImpl(int productKey)
    {
        this.productKey = productKey;
    }

    /**
     * Get the product key for this Product.
     * @return Not found product key for the ProductDefaultImpl is returned
     */
    public int getProductKey()
    {
        return productKey;
    }
    /**
     * Get the product type of this product
     * @return short
     * @see com.cboe.idl.cmiConstants.ProductTypes
     */
    public short getProductType()
    {
        return StrategyTypes.UNKNOWN;
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
        newStruct.productSymbol = toString();
        newStruct.reportingClass = toString();
        return newStruct;
    }




    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new InvalidProductImpl(productKey);
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
            if( obj instanceof InvalidProductImpl )
            {
                if( ((InvalidProductImpl)obj).getProductKey() == productKey)
                {
                    isEqual = true;
                }
            }
        }
        return isEqual;
    }

    /**
     * Returns a String representation of this Product.
     */
    public String toString()
    {
        return "Invalid Product Key " + productKey;
    }
}
