package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.util.*;
import com.cboe.util.channel.*;
import com.cboe.util.event.*;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * This is the implementation of the CMIQuoteStatusConsumer callback object which
 * receives quote status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class QuoteStatusConsumerImpl implements QuoteStatusConsumer
{
    private EventChannelAdapter eventChannel = null;

    /**
     * QuoteStatusConsumerImpl constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public QuoteStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }

    /**
     * The callback method used by the CAS to publish quote status data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param statusChange the quote status change to publish.
     * @param quotes the quote data to publish to all subscribed listeners
     */
    public void acceptQuoteStatus(QuoteDetailStruct[] quotes)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

         // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_ALL_QUOTES, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, quotes);
        eventChannel.dispatch(event);

        for ( int i = 0; i < quotes.length; i++)
        {
            QuoteDetailStruct[] eventData = new QuoteDetailStruct[1];
            eventData[0] = quotes[i];

            // keyed by classKey.
            key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS, new Integer(quotes[i].productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key,eventData);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS, new Integer(quotes[i].productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key,eventData);
            eventChannel.dispatch(event);
        }
     }

    public void acceptQuoteBustReport(QuoteBustReportStruct bustedQuote)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

         // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustedQuote);
        eventChannel.dispatch(event);

        // keyed by class key.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS, new Integer(bustedQuote.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustedQuote);
        eventChannel.dispatch(event);

        // keyed by product key.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS, new Integer(bustedQuote.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustedQuote);
        eventChannel.dispatch(event);

        // keyed by firm key.
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustedQuote);
        eventChannel.dispatch(event);
    }

    public void acceptQuoteFilledReport(QuoteFilledReportStruct filledQuote)
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(this.getClass()+" acceptQuoteFilledReport", GUILoggerBusinessProperty.QUOTE, filledQuote);
        }

         // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledQuote);
        eventChannel.dispatch(event);

        // keyed by class key.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS, new Integer(filledQuote.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledQuote);
        eventChannel.dispatch(event);

        // keyed by product key.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS, new Integer(filledQuote.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledQuote);
        eventChannel.dispatch(event);

        // keyed by firm key.
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledQuote);
        eventChannel.dispatch(event);
    }

    public void acceptQuoteCancelReport(QuoteCancelReportStruct struct)
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(this.getClass()+" acceptQuoteCancelReport", GUILoggerBusinessProperty.QUOTE, struct);
        }

         // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, struct);
        eventChannel.dispatch(event);

        // keyed by class key.
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS, new Integer(struct.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, struct);
        eventChannel.dispatch(event);

        // keyed by product key.
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS, new Integer(struct.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, struct);
        eventChannel.dispatch(event);

        // keyed by firm key.
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, struct);
        eventChannel.dispatch(event);
    }
}
