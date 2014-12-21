package com.cboe.interfaces.application;

/**
 *
 * @author Jeff Illian
 *
 */
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.quote.QuoteInfoStruct;

public interface QuoteStatusCollector {
  public void acceptQuoteFillReport(QuoteInfoStruct quoteInfo, FilledReportStruct[] filledQuote, short statusChange);
  public void acceptQuoteDeleteReports(QuoteDeleteReportWrapper[] deletedQuotes);
  public void acceptQuoteBustReport(QuoteInfoStruct quoteInfo, BustReportStruct[] bustedQuote, short statusChange);
  public void acceptAddQuotes(QuoteDetailStruct[] quoteDetails);
  public void acceptQuoteUpdate(QuoteDetailStruct[] quoteDetails);
}
