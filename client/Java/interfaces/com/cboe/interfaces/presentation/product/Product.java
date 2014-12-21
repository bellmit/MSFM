//
// -----------------------------------------------------------------------------------
// Source file: Product.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Defines the contract a Product wrapper for a ProductStruct should
 * provide
 */
public interface Product extends BusinessModel, Cloneable
{
    // helper methods to struct attributes
    public ProductKeysStruct getProductKeysStruct();
    public ProductNameStruct getProductNameStruct();
    public short getListingState();
    public String getDescription();
    public String getCompanyName();
    public String getUnitMeasure();
    public double getStandardQuantity();
    public DateStruct getMaturityDate();
    public DateStruct getActivationDate();
    public DateStruct getInactivationDate();
    public DateTimeStruct getCreatedTime();
    public DateTimeStruct getLastModifiedTime();
    public char getOpraMonthCode();
    public char getOpraPriceCode();
    public int getProductKey();
    public short getProductType();
    public ExpirationType getExpirationType();
    public boolean getLeapIndicator();

    /**
     * Determines if this product is the default product.
     */
    public boolean isDefaultProduct();

    /**
     * Determines if this product is the All Selected product.
     */
    public boolean isAllSelectedProduct();

    /**
     * Gets the expiration date of the product
     */
    public Date getExpirationDate();

    /**
     * Gets the exercise price of the product
     */
    public Price getExercisePrice();

    public Object clone() throws CloneNotSupportedException;

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public ProductStruct getProductStruct();


}
