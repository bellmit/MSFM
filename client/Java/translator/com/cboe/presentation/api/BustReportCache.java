package com.cboe.presentation.api;

import java.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
/**
 * This class is a helper that provides order and quote bust reports for
 * the current login session
 * @version 2/24/2000
 * @author Connie Liang
 */
public class BustReportCache implements EventChannelListener
{
    protected ArrayList quoteBustReportList = null;
    protected ArrayList orderBustReportList = null;

/**
 * BustReportCache constructor comment.
 */
public BustReportCache()
{
    super();
    quoteBustReportList = new ArrayList(101);    // ordered by quote key
    orderBustReportList = new ArrayList(101);    // ordered by orderIdContainer
}

public ArrayList getQuoteBustReportMap()
{
    return quoteBustReportList;
}

public ArrayList getOrderBustReportMap()
{
    return orderBustReportList;
}

/**
 * Adds or updates the passed quote to the cache.
 * @param quote QuoteDetailStruct
 */
public void addQuoteBustReport(QuoteBustReportStruct quoteBust)
{
    synchronized(getQuoteBustReportMap())
    {
        getQuoteBustReportMap().add(quoteBust);
    }
}

/**
 * Adds or updates the passed quote to the cache.
 * @param quote QuoteDetailStruct
 */
public void addOrderBustReport(OrderBustReportStruct orderBust)
{
    synchronized(getOrderBustReportMap())
    {
        getOrderBustReportMap().add(orderBust);
    }
}

/**
 * Receives updates of quote events in order to update the internal cache.
 * @param event
 */
public void channelUpdate(ChannelEvent event)
{
    int channelType = ((ChannelKey)event.getChannel()).channelType;
    Object eventData = event.getEventData();

    if(channelType == ChannelType.CB_ORDER_BUST_REPORT)
    {
        addOrderBustReport((OrderBustReportStruct)eventData);
    }
    else if (channelType == ChannelType.CB_QUOTE_BUST_REPORT)
    {
        addQuoteBustReport((QuoteBustReportStruct)eventData);
    }
}

/**
 * Returns all of the quotes that are cached.
 * @return An array of <code>QuoteDetailStruct</code>'s
 */
public QuoteBustReportStruct[] getBustReportsForQuotes()
{
    synchronized(getQuoteBustReportMap())
    {
        return(QuoteBustReportStruct[])( getQuoteBustReportMap().toArray(new QuoteBustReportStruct[0] ));
    }
}

/**
 * Returns all of the quotes that are cached.
 * @return An array of <code>QuoteDetailStruct</code>'s
 */
public OrderBustReportStruct[] getBustReportsForOrders()
{
    synchronized(getOrderBustReportMap())
    {
        return(OrderBustReportStruct[])( getOrderBustReportMap().toArray(new OrderBustReportStruct[0]));
    }
}
}
