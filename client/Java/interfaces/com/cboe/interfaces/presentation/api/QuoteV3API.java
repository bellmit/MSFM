//
// ------------------------------------------------------------------------
// FILE: QuoteV3API.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.QuoteEntryStructV3;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;

public interface QuoteV3API extends QuoteV2API
{
    ClassQuoteResultStructV3[] acceptQuotesForClassV3(int classKey, QuoteEntryStructV3[] quoteEntryStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;
    
    public void addQuoteFilledReport(QuoteFilledReportStruct quoteFilledReportStruct);
    public void removeQuoteFilledReport(QuoteFilledReportStruct quoteFilledReportStruct);
}
