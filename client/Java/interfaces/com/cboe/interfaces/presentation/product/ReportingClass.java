//
// -----------------------------------------------------------------------------------
// Source file: ReportingClass.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiProduct.ReportingClassStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Defines the contract a ReportingClass wrapper for a ReportingClassStruct should
 * provide
 */
public interface ReportingClass extends BusinessModel
{
    /**
     * Gets the class key for the reporting class
     * @return class key contained within an Integer object.
     */
    public Integer getClassKey();

    /**
     * Gets the class key of the product class for this reporting class
     * @return class key of the product class for this reporting class contained within an Integer object.
     */
    public Integer getProductClassKey();

    /**
     * Gets the ProductClass for this reporting class
     * @return ProductClass that represents this reporting class
     */
    public ProductClass getProductClass()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Gets the product type of the reporting class
     * @return product type contained within a Short object
     */
    public Short getProductType();

    /**
     * Gets the listing state of the reporting class
     * @return listing state contained within a Short object
     */
    public Short getListingState();

    /**
     * Gets the symbol of the product class for this reporting class
     * @return symbol of product class
     */
    public String getProductClassSymbol();

    /**
     * Gets the symbol for this reporting class
     * @return symbol of reporting class
     */
    public String getReportingClassSymbol();

    /**
     * Gets the contract size for this reporting class
     * @return contract size for this reporting class
     */
    public Integer getContractSize();

    /**
     * Gets the transaction fee code for this reporting class
     * @return transaction fee code for this reporting class
     */
    public String getTransactionFeeCode();

    /**
     * Gets the activation date for this reporting class
     * @return activation date for this reporting class
     */
    public Date getActivationDate();

    /**
     * Gets the inactivation date for this reporting class
     * @return inactivation date for this reporting class
     */
    public Date getInactivationDate();

    /**
     * Gets the created date & time for this reporting class
     * @return created date & time for this reporting class
     */
    public DateTime getCreatedTime();

    /**
     * Gets the last modified date & time for this reporting class
     * @return last modified date & time for this reporting class
     */
    public DateTime getLastModifiedTime();

    /**
     * Gets the wrapped struct
     * @deprecated try not to use the actual struct
     */
    public ReportingClassStruct getClassStruct();
}