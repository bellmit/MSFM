//
// -----------------------------------------------------------------------------------
// Source file: ReportingClassImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ReportingClassStruct;

import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;

import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.presentation.common.dateTime.DateTimeFactory;
import com.cboe.presentation.util.StringCache;

/**
 * Defines a wrapper impl for a ReportingClassStruct
 */
public class ReportingClassImpl extends AbstractReportingClassImpl
{
    private Date activationDate;
    private Date inactivationDate;
    private DateTime createdTime;
    private DateTime lastModifiedTime;

    // ReportingClassStruct fields
    private int classKey;
    private short productType;
    private String reportingClassSymbol;
    private String productClassSymbol;
    private int productClassKey;
    private short listingState;
    private int contractSize;
    private String transactionFeeCode;

    /**
     * Constructor
     * @param ReportingClassStruct to represent
     */
    protected ReportingClassImpl(ReportingClassStruct reportingClassStruct)
    {
        super();

        if(reportingClassStruct == null)
        {
            throw new IllegalArgumentException("ReportingClassStruct can not be NULL.");
        }
        updateFromStruct(reportingClassStruct);
        formatter = CommonFormatFactory.getReportingClassFormatStrategy();
    }

    protected void updateFromStruct(ReportingClassStruct struct)
    {
        classKey = struct.classKey;
        productType = struct.productType;
        reportingClassSymbol = StringCache.get(struct.reportingClassSymbol);
        productClassSymbol = StringCache.get(struct.productClassSymbol);

        productClassKey = struct.productClassKey;
        listingState = struct.listingState;
        contractSize = struct.contractSize;
        transactionFeeCode = StringCache.get(struct.transactionFeeCode);

        activationDate = DateTimeFactory.getDate(struct.activationDate);
        inactivationDate = DateTimeFactory.getDate(struct.inactivationDate);
        createdTime = DateTimeFactory.getDateTime(struct.createdTime);
        lastModifiedTime = DateTimeFactory.getDateTime(struct.lastModifiedTime);
    }

    /**
     * Gets the wrapped struct
     * @deprecated try not to use the actual struct
     */
    public ReportingClassStruct getClassStruct()
    {
        ReportingClassStruct retVal = new ReportingClassStruct();
        retVal.classKey = getClassKey();
        retVal.productType = getProductType();
        retVal.reportingClassSymbol = getReportingClassSymbol();
        retVal.productClassSymbol = getProductClassSymbol();
        retVal.productClassKey = getProductClassKey();
        retVal.listingState = getListingState();
        retVal.contractSize = getContractSize();
        retVal.transactionFeeCode = getTransactionFeeCode();
        retVal.activationDate = getActivationDate().getDateStruct();
        retVal.inactivationDate = getInactivationDate().getDateStruct();
        retVal.createdTime = getCreatedTime().getDateTimeStruct();
        retVal.lastModifiedTime = getLastModifiedTime().getDateTimeStruct();

        return retVal;
    }

    /**
     * Gets the activation date for this reporting class
     * @return activation date for this reporting class
     */
    public Date getActivationDate()
    {
        return activationDate;
    }

    /**
     * Gets the class key for the reporting class
     * @return class key contained within an Integer object.
     */
    public Integer getClassKey()
    {
        return classKey;
    }

    /**
     * Gets the contract size for this reporting class
     * @return contract size for this reporting class
     */
    public Integer getContractSize()
    {
        return contractSize;
    }

    /**
     * Gets the created date & time for this reporting class
     * @return created date & time for this reporting class
     */
    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    /**
     * Gets the inactivation date for this reporting class
     * @return inactivation date for this reporting class
     */
    public Date getInactivationDate()
    {
        return inactivationDate;
    }

    /**
     * Gets the last modified date & time for this reporting class
     * @return last modified date & time for this reporting class
     */
    public DateTime getLastModifiedTime()
    {
        return lastModifiedTime;
    }

    /**
     * Gets the listing state of the reporting class
     * @return listing state contained within a Short object
     */
    public Short getListingState()
    {
        return listingState;
    }

    /**
     * Gets the class key of the product class for this reporting class
     * @return class key of the product class for this reporting class contained within an Integer object.
     */
    public Integer getProductClassKey()
    {
        return productClassKey;
    }

    /**
     * Gets the symbol of the product class for this reporting class
     * @return symbol of product class
     */
    public String getProductClassSymbol()
    {
        return productClassSymbol;
    }

    /**
     * Gets the product type of the reporting class
     * @return product type contained within a Short object
     */
    public Short getProductType()
    {
        return productType;
    }

    /**
     * Gets the symbol for this reporting class
     * @return symbol of reporting class
     */
    public String getReportingClassSymbol()
    {
        return reportingClassSymbol;
    }

    /**
     * Gets the transaction fee code for this reporting class
     * @return transaction fee code for this reporting class
     */
    public String getTransactionFeeCode()
    {
        return transactionFeeCode;
    }
}
