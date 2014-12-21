//==============================================================================
// QuoteDeleteReportWrapperImpl.java
//==============================================================================

package com.cboe.application.quote;

import com.cboe.domain.util.QuoteDetailCancelReportContainer;
import com.cboe.interfaces.application.QuoteDeleteReportWrapper;
import com.cboe.idl.cmiQuote.*;


/**
 * Simply implements the {@link QuoteDeleteReportWrapper} interface, creating a
 * simple transport class for sending quote delete data through the CAS to the
 * callback proxies.
 */
public class QuoteDeleteReportWrapperImpl
    extends QuoteDetailCancelReportContainer
    implements QuoteDeleteReportWrapper
{
    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------
    /**
     * @param quoteDetail
     * @param quoteCancel
     */
    public QuoteDeleteReportWrapperImpl(QuoteDetailStruct quoteDetail,
                                        QuoteCancelReportStruct quoteCancel)
    {
        super(quoteDetail, quoteCancel);
    }


    /*--------------------------------------------------------------------------
     * NOTE
     *
     * The getQuoteDetailStruct() and getQuoteCancelReportStruct() methods that
     * are defined in the QuoteDeleteReportWrapper interface are implemented in
     * the QuoteDetailCancelReportContainer class.
     *--------------------------------------------------------------------------
     */
}


// End of file.
