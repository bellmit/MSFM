package com.cboe.presentation.api;

import java.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.presentation.order.OrderIdFactory;


/**
 * This class is a helper that provides order and quote filled reports for
 * the current login session
 * @version 11/02/1999
 * @author Connie Liang
 */
public class FilledReportCache implements EventChannelListener
{
    protected ArrayList quoteFilledReportList = null;
    protected ArrayList orderFilledReportList = null;
    private FilledReportStruct[] emptyFilledReports;
    protected HashMap reportsByIdMap = null;
    private OrderFilledReportStruct[] emptyOrderFilledReports ;
/**
 * FilledReportCache constructor comment.
 */
public FilledReportCache()
{
    super();
    emptyFilledReports = new FilledReportStruct[0];
    emptyOrderFilledReports = new OrderFilledReportStruct[0];
    quoteFilledReportList = new ArrayList(1001);    // ordered by quote key
    orderFilledReportList = new ArrayList(1001);    // ordered by orderIdContainer
    reportsByIdMap = new HashMap(500);
}

public ArrayList getQuoteFilledReportMap()
{
    return quoteFilledReportList;
}

public ArrayList getOrderFilledReportMap()
{
    return orderFilledReportList;
}

/**
 * Adds or updates the passed quote to the cache.
 * @param quote QuoteDetailStruct
 */
public void addQuoteFilledReport(QuoteFilledReportStruct quoteFilled)
{
    synchronized(getQuoteFilledReportMap())
    {
        getQuoteFilledReportMap().add(quoteFilled);
    }
}

public void removeQuoteFilledReport(QuoteFilledReportStruct quoteFilled)
{
    synchronized(getQuoteFilledReportMap())
    {
        getQuoteFilledReportMap().remove(quoteFilled);
    }
}

/**
 * Adds or updates the passed quote to the cache.
 * @param quote QuoteDetailStruct
 */
public void addOrderFilledReport(OrderFilledReportStruct orderFilled)
{
    synchronized(getOrderFilledReportMap())
    {
        getOrderFilledReportMap().add(orderFilled);
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

    if(channelType == ChannelType.CB_FILLED_REPORT)
    {
        OrderFilledReportStruct struct = (OrderFilledReportStruct)eventData;
        addOrderFilledReport(struct);
        OrderId orderId = OrderIdFactory.createOrderId(struct.filledOrder.orderStruct.orderId);
        addReportForOrderId(orderId, struct);
    }
    else if (channelType == ChannelType.CB_QUOTE_FILLED_REPORT)
    {
        addQuoteFilledReport((QuoteFilledReportStruct)eventData);
    }
}

/**
 * Returns all of the quotes that are cached.
 * @return An array of <code>QuoteDetailStruct</code>'s
 */
public QuoteFilledReportStruct[] getFilledReportsForQuotes()
{
    synchronized(getQuoteFilledReportMap())
    {
        return(QuoteFilledReportStruct[])( getQuoteFilledReportMap().toArray(new QuoteFilledReportStruct[0] ));
    }
}

/**
 * Returns all of the quotes that are cached.
 * @return An array of <code>QuoteDetailStruct</code>'s
 */
public OrderFilledReportStruct[] getFilledReportsForOrders()
{
    synchronized(getOrderFilledReportMap())
    {
        return(OrderFilledReportStruct[])( getOrderFilledReportMap().toArray(new OrderFilledReportStruct[0]));
    }
}
    public int getFilledReportsCountForOrder(OrderId orderId)
    {
        synchronized(getReportsByIdMap())
        {
            List reportList = (List) getReportsByIdMap().get(orderId.getCboeId().toString());
            if(reportList == null) // nothing added yet
            {
                return 0;
            }
            else
            {
                return reportList.size();
            }
        }
    }

    public OrderFilledReportStruct[] getFilledReportsForOrder(OrderId orderId)
    {
        synchronized(getReportsByIdMap())
        {
            List reportList = (List) getReportsByIdMap().get(orderId.getCboeId().toString());
            if(reportList == null) // nothing added yet
            {
                return emptyOrderFilledReports;
            }
            else
            {
                return (OrderFilledReportStruct[]) reportList.toArray(emptyOrderFilledReports);
            }
        }
    }
    private HashMap getReportsByIdMap()
    {
        return reportsByIdMap;
    }

    private void addReportForOrderId(OrderId orderId, OrderFilledReportStruct struct)
    {
        synchronized (getReportsByIdMap())
        {
            List reportList = (List) getReportsByIdMap().get(orderId.getCboeId().toString());
            if(reportList == null)
            {
                reportList = new ArrayList(10);
                getReportsByIdMap().put(orderId.getCboeId().toString(), reportList);
            }
            reportList.add(struct);
        }
    }
}
