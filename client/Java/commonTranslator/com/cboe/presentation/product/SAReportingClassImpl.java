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

import com.cboe.presentation.common.dateTime.DateImpl;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.formatters.CommonFormatFactory;


/**
 * Defines a wrapper impl for a ReportingClassStruct
 */
public class SAReportingClassImpl extends AbstractReportingClassImpl
{
    private ReportingClassStruct reportingClassStruct;
    private Date activationDate;
    private Date inactivationDate;
    private DateTime createdTime;
    private DateTime lastModifiedTime;

    /**
     * Constructor
     * @param ReportingClassStruct to represent
     */
    protected SAReportingClassImpl(ReportingClassStruct reportingClassStruct)
    {
        super();

        if(reportingClassStruct == null)
        {
            throw new IllegalArgumentException("ReportingClassStruct can not be NULL.");
        }
        this.reportingClassStruct = reportingClassStruct;
        formatter = CommonFormatFactory.getReportingClassFormatStrategy();
    }

    /**
     * Gets the wrapped struct
     * @deprecated try not to use the actual struct
     */
    public ReportingClassStruct getClassStruct()
    {
        return reportingClassStruct;
    }

    /**
     * Gets the activation date for this reporting class
     * @return activation date for this reporting class
     */
    public Date getActivationDate()
    {
        if(activationDate == null)
        {
            if(getClassStruct().activationDate != null)
            {
                activationDate = new DateImpl(getClassStruct().activationDate);
            }
            else
            {
                throw new IllegalStateException("ActivationDate DateStruct in ReportingClassStruct can not be NULL");
            }
        }
        return activationDate;
    }

    /**
     * Gets the class key for the reporting class
     * @return class key contained within an Integer object.
     */
    public Integer getClassKey()
    {
        return new Integer(getClassStruct().classKey);
    }

    /**
     * Gets the contract size for this reporting class
     * @return contract size for this reporting class
     */
    public Integer getContractSize()
    {
        return new Integer(getClassStruct().contractSize);
    }

    /**
     * Gets the created date & time for this reporting class
     * @return created date & time for this reporting class
     */
    public DateTime getCreatedTime()
    {
        if(createdTime == null)
        {
            if(getClassStruct().createdTime != null)
            {
                createdTime = new DateTimeImpl(getClassStruct().createdTime);
            }
            else
            {
                throw new IllegalStateException("CreatedTime DateTimeStruct in ReportingClassStruct can not be NULL");
            }
        }
        return createdTime;
    }

    /**
     * Gets the inactivation date for this reporting class
     * @return inactivation date for this reporting class
     */
    public Date getInactivationDate()
    {
        if(inactivationDate == null)
        {
            if(getClassStruct().inactivationDate != null)
            {
                inactivationDate = new DateImpl(getClassStruct().inactivationDate);
            }
            else
            {
                throw new IllegalStateException("InactivationDate DateStruct in ReportingClassStruct can not be NULL");
            }
        }
        return inactivationDate;
    }

    /**
     * Gets the last modified date & time for this reporting class
     * @return last modified date & time for this reporting class
     */
    public DateTime getLastModifiedTime()
    {
        if(lastModifiedTime == null)
        {
            if(getClassStruct().lastModifiedTime != null)
            {
                lastModifiedTime = new DateTimeImpl(getClassStruct().lastModifiedTime);
            }
            else
            {
                throw new IllegalStateException("LastModifiedTime DateTimeStruct in ReportingClassStruct can not be NULL");
            }
        }
        return lastModifiedTime;
    }

    /**
     * Gets the listing state of the reporting class
     * @return listing state contained within a Short object
     */
    public Short getListingState()
    {
        return new Short(getClassStruct().listingState);
    }

    /**
     * Gets the class key of the product class for this reporting class
     * @return class key of the product class for this reporting class contained within an Integer object.
     */
    public Integer getProductClassKey()
    {
        return new Integer(getClassStruct().productClassKey);
    }

    /**
     * Gets the symbol of the product class for this reporting class
     * @return symbol of product class
     */
    public String getProductClassSymbol()
    {
        return getClassStruct().productClassSymbol;
    }

    /**
     * Gets the product type of the reporting class
     * @return product type contained within a Short object
     */
    public Short getProductType()
    {
        return new Short(getClassStruct().productType);
    }

    /**
     * Gets the symbol for this reporting class
     * @return symbol of reporting class
     */
    public String getReportingClassSymbol()
    {
        return getClassStruct().reportingClassSymbol;
    }

    /**
     * Gets the transaction fee code for this reporting class
     * @return transaction fee code for this reporting class
     */
    public String getTransactionFeeCode()
    {
        return getClassStruct().transactionFeeCode;
    }
}
