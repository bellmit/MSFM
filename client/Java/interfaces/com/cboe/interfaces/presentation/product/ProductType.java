//
// -----------------------------------------------------------------------------------
// Source file: ProductType.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import java.util.Date;

import com.cboe.idl.cmiProduct.ProductTypeStruct;

/**
 * ProductType provides a wrapper to the ProductTypeStruct class for
 * use in the CBOE gui environment
 */
public interface ProductType extends Comparable
{
    /**
     * Sets the underlying ProductTypeStruct
     * @param ProductTypeStruct struct
     */
    public void setProductTypeStruct(ProductTypeStruct struct);

    /**
     * Gets the type of this product type.
     * @see com.cboe.idl.cmiConstants.ProductTypes
     * @return short
     */
    public short getType();

    /**
     * Gets the name of this product type.
     * @return String
     */
    public String getName();

    /**
     * Gets the description of this product type.
     * @return String
     */
    public String getDescription();

    /**
     * Gets the creation time of this product type.
     * @return Date
     */
    public Date getCreatedTime();

    /**
     * Gets the last modified time of this product type
     * @return Date
     */
    public Date getLastModifiedTime();

    /**
     * Acts a tag to remind implementors to override
     */
    public boolean equals(Object other);

    /**
     * Returns a string representation
     */
    public String toString();

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public ProductTypeStruct getProductTypeStruct();

}