package com.cboe.consumers.eventChannel;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.util.RoutingParameterStruct;

/**
 * QuoteStatusConsumer
 *
 * @author Jeff Illian
 */

public class QuoteStatusEventConsumerImpl extends com.cboe.idl.events.POA_QuoteStatusEventConsumer implements QuoteStatusConsumer{
    private QuoteStatusConsumer delegate;

    /**
     * QuoteStatusListener constructor comment.
     */
    public QuoteStatusEventConsumerImpl(QuoteStatusConsumer quoteStatusConsumer) {
        super();
        this.delegate = quoteStatusConsumer;
    }

    /**
      * acceptQuoteFillReport
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    filledQuote - QuoteFilledReportStruct structure to delegate
      *
      * @return void
      */
    public void acceptQuoteFillReport(int[] groups, QuoteInfoStruct quoteInfo, short statusChange, FilledReportStruct[] filledQuote,String eventInitiator)
    {
        delegate.acceptQuoteFillReport(groups, quoteInfo, statusChange, filledQuote,eventInitiator);
    }

    public void acceptQuoteFillReportV3(RoutingParameterStruct routing, QuoteInfoStruct quoteInfo, short statusChange, FilledReportStruct[] filledQuote,String eventInitiator)
    {
        delegate.acceptQuoteFillReportV3(routing, quoteInfo, statusChange, filledQuote,eventInitiator);
    }
    /**
      * acceptQuoteDeleteReport
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    userId      - user/channel identifier
      * @param    quoteKeys   - array of int to delegate
      *
      * @return void
      */
    public void acceptQuoteDeleteReport(int[] groups, String userId, int[] quoteKeys, short cancelReason, String eventInitiator)
    {
        delegate.acceptQuoteDeleteReport(groups, userId, quoteKeys, cancelReason,eventInitiator);
    }

    public void acceptQuoteDeleteReportV2(int[] groups, String userId, com.cboe.idl.cmiQuote.QuoteStruct[] quotes, short cancelReason,String eventInitiator)
    {
        delegate.acceptQuoteDeleteReportV2(groups, userId, quotes, cancelReason,eventInitiator);
    }

    public void acceptQuoteDeleteReportV3(RoutingParameterStruct routing, String userId, com.cboe.idl.cmiQuote.QuoteStruct[] quotes, short cancelReason, String eventInitiator)
    {
        delegate.acceptQuoteDeleteReportV3(routing, userId, quotes, cancelReason, eventInitiator);
    }
    /**
      * acceptQuoteBustReport
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Connie Feng
      *
      * @param    bustedQuote   - event data
       *
      * @return void
      */
    public void acceptQuoteBustReport(int[] groups, QuoteInfoStruct quoteInfo, short statusChange, BustReportStruct[] bustedQuote, String eventInitiator)
    {
        delegate.acceptQuoteBustReport(groups, quoteInfo, statusChange, bustedQuote, eventInitiator);
    }

    public void acceptQuoteBustReportV3(RoutingParameterStruct routing, QuoteInfoStruct quoteInfo, short statusChange, BustReportStruct[] bustedQuote,String eventInitiator)
    {
        delegate.acceptQuoteBustReportV3(routing, quoteInfo, statusChange, bustedQuote, eventInitiator);
    }

    public void acceptQuoteStatusUpdate(RoutingParameterStruct routingParameters,  QuoteStruct quote, short statusChange)
    {
       delegate.acceptQuoteStatusUpdate(routingParameters, quote, statusChange);
    }
    /**
     * @author Jeff Illian
     */

    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }

}
