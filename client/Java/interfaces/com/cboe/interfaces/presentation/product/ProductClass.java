//
// -----------------------------------------------------------------------------------
// Source file: ProductClass.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Defines the contract a ProductClass wrapper for a ClassStruct should
 * provide
 */
public interface ProductClass extends BusinessModel, Cloneable
{
    // helper methods to struct attributes
    public int getClassKey();
    public short getProductType();
    public short getListingState();
    public String getClassSymbol();
    public Product getUnderlyingProduct();
    public String getPrimaryExchange();
    public DateStruct getActivationDate();
    public DateStruct getInactivationDate();
    public DateTimeStruct getCreatedTime();
    public DateTimeStruct getLastModifiedTime();
    public EPWStruct[] getEPWValues();
    public double getEPWFastMarketMultiplier();
    public ProductDescriptionStruct getProductDescription();
    public boolean isTestClass();

    /**
     * Determines if this product is the default product.
     */
    public boolean isDefaultProductClass();

    /**
     * Determines if this product is the All Selected product.
     */
    public boolean isAllSelectedProductClass();

    /**
     * Gets all the reporting classes for this product class
     * @return an array of reporting classes
     */
    public ReportingClass[] getReportingClasses();

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public ClassStruct getClassStruct();

    /**
     * Determines if this ProductClass is invalid, either it has been marked inactive or
     * has been removed from the system, but some data structures still reference the classkey 
     */
    public boolean isValid();

    /**
     * Gets the post location
     */
    public String getPost();

    /**
     * Gets the station location
     */
    public String getStation();
}