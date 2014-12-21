//
// -----------------------------------------------------------------------------------
// Source file: ProductTypeImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import java.util.Date;
import java.util.Calendar;

import com.cboe.idl.cmiProduct.ProductTypeStruct;

import com.cboe.interfaces.presentation.product.ProductType;

/**
 * ProductType provides a wrapper to the ProductTypeStruct class for
 * use in the CBOE gui environment
 */
public class ProductTypeImpl implements ProductType
{
    protected ProductTypeStruct productTypeStruct;

    /**
     * ProductType constructor
     * @param struct ProductTypeStruct
     */
    public ProductTypeImpl(ProductTypeStruct struct)
    {
        setProductTypeStruct(struct);
    }

    /**
     * Provides Comparable implementation
     */
    public int compareTo(Object other)
    {
        int retVal = -1;
        //Compare the session names
        if(other instanceof ProductType)
        {
            ProductType otherType = (ProductType) other;
            retVal = getName().compareTo(otherType.getName());
        }
        return retVal;
    }

    /**
     * Sets the underlying ProductTypeStruct
     * @param ProductTypeStruct struct
     */
    public void setProductTypeStruct(ProductTypeStruct struct)
    {
        if (struct != null)
        {
            productTypeStruct = struct;
        }
    }

    /**
     * Gets the type of this product type.
     * @see com.cboe.idl.cmiConstants.ProductTypes
     * @return short
     */
    public short getType()
    {
        return productTypeStruct.type;
    }

    /**
     * Gets the name of this product type.
     * @return String
     */
    public String getName()
    {
        return productTypeStruct.name;
    }

    /**
     * Gets the description of this product type.
     * @return String
     */
    public String getDescription()
    {
        return productTypeStruct.description;
    }

    /**
     * Gets the creation time of this product type.
     * @return Date
     */
    public Date getCreatedTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, productTypeStruct.createdTime.date.year);
        calendar.set(Calendar.MONTH, productTypeStruct.createdTime.date.month);
        calendar.set(Calendar.DATE, productTypeStruct.createdTime.date.day);
        calendar.set(Calendar.HOUR, productTypeStruct.createdTime.time.hour);
        calendar.set(Calendar.MINUTE, productTypeStruct.createdTime.time.minute);
        calendar.set(Calendar.SECOND, productTypeStruct.createdTime.time.second);
        calendar.set(Calendar.MILLISECOND, productTypeStruct.createdTime.time.fraction);

        return calendar.getTime();
    }

    /**
     * Gets the last modified time of this product type
     * @return Date
     */
    public Date getLastModifiedTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, productTypeStruct.lastModifiedTime.date.year);
        calendar.set(Calendar.MONTH, productTypeStruct.lastModifiedTime.date.month);
        calendar.set(Calendar.DATE, productTypeStruct.lastModifiedTime.date.day);
        calendar.set(Calendar.HOUR, productTypeStruct.lastModifiedTime.time.hour);
        calendar.set(Calendar.MINUTE, productTypeStruct.lastModifiedTime.time.minute);
        calendar.set(Calendar.SECOND, productTypeStruct.lastModifiedTime.time.second);
        calendar.set(Calendar.MILLISECOND, productTypeStruct.lastModifiedTime.time.fraction);

        return calendar.getTime();
    }

    /**
     * Acts a tag to remind implementors to override
     */
    public boolean equals(Object other)
    {
        boolean retVal = super.equals(other);
        if ( (!retVal) && (other instanceof ProductType) )
        {
            //Compare the type of each ProductType
            ProductType otherType = (ProductType) other;
            retVal = getType() == otherType.getType();
        }
        return retVal;
    }

    /**
     * Returns a string representation
     */
    public String toString()
    {
        return getName();
    }

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public ProductTypeStruct getProductTypeStruct()
    {
        return this.productTypeStruct;
    }

}