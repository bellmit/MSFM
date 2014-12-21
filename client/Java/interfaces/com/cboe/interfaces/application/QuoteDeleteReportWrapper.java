//==============================================================================
// QuoteDeleteReportWrapper.java
//==============================================================================

package com.cboe.interfaces.application;

import com.cboe.idl.cmiQuote.*;


/**
 * An instance of a class that implements this interface can be used as a simple
 * wrapper object to hold all the data needed to service both V1 and V2 versions
 * of the cancel/delete quote functionality.
 */
public interface QuoteDeleteReportWrapper
{
    /**
     *
     */
    public QuoteDetailStruct getQuoteDetailStruct();

    /**
     *
     */
    public QuoteCancelReportStruct getQuoteCancelReportStruct();
}


// End of file.
