//
// ------------------------------------------------------------------------
// FILE: QuoteStatusConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callbackV2
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.QuoteStatusV2Consumer;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteDeleteReportStruct;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * @author torresl@cboe.com
 */
public class QuoteStatusV2ConsumerImpl implements QuoteStatusV2Consumer
{
    private EventChannelAdapter eventChannel;
    public static final int LOG_COUNT = 100;
    protected int count;
    public QuoteStatusV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
    }

    public void acceptQuoteBustReport(QuoteBustReportStruct quoteBustReport, int queueDepth)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteBustReport);
        eventChannel.dispatch(event);

        // keyed by class key.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS, new Integer(quoteBustReport.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteBustReport);
        eventChannel.dispatch(event);

        // keyed by productKey.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS, new Integer(quoteBustReport.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteBustReport);
        eventChannel.dispatch(event);

        // keyed by firm
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteBustReport);
        eventChannel.dispatch(event);

        // keyed by firm by class key.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_BY_CLASS_V2, new Integer(quoteBustReport.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteBustReport);
        eventChannel.dispatch(event);

        // keyed by firm by product key.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_BY_CLASS_V2, new Integer(quoteBustReport.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteBustReport);
        eventChannel.dispatch(event);

        this.count++;
        if (GUILoggerHome.find().isDebugOn() )//&& this.count % LOG_COUNT == 0)
        {
            GUILoggerHome.find().debug(this.getClass() + ".acceptQuoteBustReport()", GUILoggerBusinessProperty.QUOTE, quoteBustReport);
        }

    }

    public void acceptQuoteDeleteReport(QuoteDeleteReportStruct[] quoteDeleteReport, int queueDepth)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_V2, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteDeleteReport);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_FIRM_V2, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteDeleteReport);
        eventChannel.dispatch(event);

        QuoteDeleteReportStruct[] eventData = new QuoteDeleteReportStruct[1];
        for (int i = 0; i < quoteDeleteReport.length; i++)
        {
            eventData[0] = quoteDeleteReport[i];
            // keyed by classKey.
            key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, new Integer(quoteDeleteReport[i].quote.productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, eventData);
            eventChannel.dispatch(event);
            // keyed by productKey
            key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, new Integer(quoteDeleteReport[i].quote.productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, eventData);
            eventChannel.dispatch(event);
        }
        this.count++;
        if (GUILoggerHome.find().isDebugOn()) // && this.count % LOG_COUNT == 0)
        {
            GUILoggerHome.find().debug(this.getClass() + ".acceptQuoteDeleteReport()", GUILoggerBusinessProperty.QUOTE, quoteDeleteReport);
        }

    }

    public void acceptQuoteFilledReport(QuoteFilledReportStruct quoteFilledReport, int queueDepth)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteFilledReport);
        eventChannel.dispatch(event);

        // keyed by class key.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS, new Integer(quoteFilledReport.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteFilledReport);
        eventChannel.dispatch(event);

        // keyed by product key.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS, new Integer(quoteFilledReport.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteFilledReport);
        eventChannel.dispatch(event);

        // keyed by firm key.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteFilledReport);
        eventChannel.dispatch(event);

        // keyed by firm by class key.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_BY_CLASS_V2, new Integer(quoteFilledReport.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteFilledReport);
        eventChannel.dispatch(event);

        // keyed by firm by product key.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_BY_CLASS_V2, new Integer(quoteFilledReport.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteFilledReport);
        eventChannel.dispatch(event);

        this.count++;
        if (GUILoggerHome.find().isDebugOn() )// && this.count % LOG_COUNT == 0)
        {
            GUILoggerHome.find().debug(this.getClass() + ".acceptQuoteFilledReport()", GUILoggerBusinessProperty.QUOTE, quoteFilledReport);
        }
    }

    public void acceptQuoteStatus(QuoteDetailStruct[] quoteDetail, int queueDepth)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_ALL_QUOTES, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quoteDetail);
        eventChannel.dispatch(event);

        for (int i = 0; i < quoteDetail.length; i++)
        {
            QuoteDetailStruct[] eventData = new QuoteDetailStruct[1];
            eventData[0] = quoteDetail[i];

            // keyed by classKey.
            key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS, new Integer(quoteDetail[i].productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, eventData);
            eventChannel.dispatch(event);
            // keyed by product key
            key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS, new Integer(quoteDetail[i].productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, eventData);
            eventChannel.dispatch(event);
        }
        this.count++;
        if (GUILoggerHome.find().isDebugOn() ) //&& this.count % LOG_COUNT == 0)
        {
            GUILoggerHome.find().debug(this.getClass()+".acceptQuoteSatus()", GUILoggerBusinessProperty.QUOTE, quoteDetail);
        }
    }
}
