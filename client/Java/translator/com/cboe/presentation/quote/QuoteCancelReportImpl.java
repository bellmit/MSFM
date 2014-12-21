// -----------------------------------------------------------------------------------
// Source file: QuoteCancelReportImpl.java
//
// PACKAGE: com.cboe.presentation.quote;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.quote;

import com.cboe.domain.util.QuoteStructBuilder;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteCancelReportStruct;
import com.cboe.interfaces.presentation.quote.Quote;
import com.cboe.interfaces.presentation.quote.QuoteDetail;
import com.cboe.interfaces.presentation.quote.QuoteCancelReport;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

/**
 * QuoteCancelReport implementation for a QuoteCancelReportStruct from the API.
 */
class QuoteCancelReportImpl extends AbstractBusinessModel implements QuoteCancelReport
{
    private QuoteCancelReportStruct quoteCancelReportStruct;

    /**
     * Constructor
     * @param quoteCancelReportStruct to represent
     */
    protected QuoteCancelReportImpl(QuoteCancelReportStruct quoteCancelReportStruct)
    {
        this();
        this.quoteCancelReportStruct = quoteCancelReportStruct;
    }

    /**
     *  Default constructor.
     */
    protected QuoteCancelReportImpl()
    {
        super();
    }

    // helper methods to struct attributes
    /**
     * Get the quote key for this QuoteCancelReport.
     * @return quote key from represented struct
     */
    public int getQuoteKey()
    {
        return getQuoteCancelReportStruct().quoteKey;
    }

    /**
     * Get the ProductKeysStruct for this QuoteCancelReport.
     * @return ProductKeysStruct from represented struct
     */
    public ProductKeysStruct getProductKeysStruct()
    {
        return getQuoteCancelReportStruct().productKeys;
    }

    /**
     * Get the ProductNameStruct for this QuoteCancelReport.
     * @return ProductNameStruct from represented struct
     */
    public ProductNameStruct getProductNameStruct()
    {
        return getQuoteCancelReportStruct().productName;
    }

    /**
     * Get the cancel reson for this QuoteCancelReport.
     * @return cancelReason from represented struct
     */
    public short getCancelReason()
    {
        return getQuoteCancelReportStruct().cancelReason;
    }

    /**
     * Get the statusChange for this QuoteCancelReport.
     * @return statusChange from represented struct
     */
    public short getStatusChange()
    {
        return getQuoteCancelReportStruct().statusChange;
    }

    /**
     * Get the QuoteDetailStruct that this QuoteDetail represents.
     * @return QuoteDetailStruct
     * @deprecated
     */
    public QuoteCancelReportStruct getQuoteCancelReportStruct()
    {
        return this.quoteCancelReportStruct;
    }

    /**
     * Returns a hash code value for the object.
     * Overrides hashCode() implemented by the Object
     * @return int hash code
     */
    public int hashCode()
    {
        return getQuoteKey();
    }

    /**
     * Clones this quote detail by returning another instance that represents a
     * QuoteDetailStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        QuoteCancelReportImpl dest;
        dest = (QuoteCancelReportImpl) super.clone();
        if (getQuoteCancelReportStruct() != null)
        {
            dest.quoteCancelReportStruct = QuoteStructBuilder.cloneQuoteCancelReportStruct(getQuoteCancelReportStruct());
        }
        return dest;
    }

    /**
     * If <code>obj</code> is an instance of QuoteCancelReport and has the same
     * quote key true is returned, false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if(this == obj)
        {
            isEqual = true;
        }
        else if(obj instanceof QuoteCancelReport)
        {
            isEqual = getQuoteKey() == ((QuoteCancelReport)obj).getQuoteKey();
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    /**
     * Returns a String representation of this QuoteDetail.
     */
//    public String toString()
//    {
//        return getQuote().toString();
//    }
}
