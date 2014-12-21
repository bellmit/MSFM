package com.cboe.presentation.api.orderBook;

//======================================================================
//      | Copyright, 1999, CBOE - All Rights Reserved. |
//+================================================================+
//
//      MODULE:     TradersOrderBookManager.java
//======================================================================

//======================================================================
//                      Package Definition
//======================================================================
//======================================================================
//                          Imports
//======================================================================
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.presentation.api.*;
import java.util.*;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

//======================================================================
//          Class Definition / Implementation
//======================================================================

//======================================================================
//      CLASS:      TradersOrderBookManager
/**
 * Manages the Traders Order Book for each product added to the manager.
 *
 * @author Derek T. Chambers-Boucher and Michael Pyatetsky
 * @version 11/10/1999
 */
//======================================================================
public class TradersOrderBookManager implements EventChannelListener
{
    private Hashtable bookCollection;
    private final String Category = this.getClass().getName();

    public TradersOrderBookManager()
    {
        bookCollection = new Hashtable();
    }
    /**
     * This method updates the BestBookManager with the given ChannelEvent.
     * @param event ChannelEvent
     * @see com.cboe.util.event.EventChannelListener
     */
    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;
        Object eventData = event.getEventData();
        switch (channelType)
        {
            case ChannelType.CB_ALL_ORDERS:
            case ChannelType.CB_ALL_ORDERS_V2:
                {
                    OrderDetailStruct[] orders = (OrderDetailStruct[]) eventData;

                    GUILoggerHome.find().debug("TradersOrderBookManager.channelUpdate:-----> "+channelType, GUILoggerBusinessProperty.ORDER_BOOK, orders);

                    for (int i = 0; i < orders.length; i++)
                    {
                        updateBook(orders[i].orderStruct.activeSession,
                                   orders[i].orderStruct.classKey,
                                   orders[i].orderStruct.productKey,
                                   orders[i].orderStruct,
                                   orders[i].statusChange);
                    }
                }
                break;
            case ChannelType.CB_ALL_QUOTES:
                {
                    QuoteDetailStruct[] quoteDetail = (QuoteDetailStruct[]) eventData;
                    for (int j = 0; j < quoteDetail.length; j++)
                    {
                        updateBook(quoteDetail[j].quote.sessionName,
                                   quoteDetail[j].productKeys.classKey,
                                   quoteDetail[j].productKeys.productKey,
                                   quoteDetail[j].quote,
                                   quoteDetail[j].statusChange);

                    }
                }
                break;
            case ChannelType.CB_QUOTE_CANCEL_REPORT_V2:
                {
                    QuoteDeleteReportStruct[] quoteDeleteReport = (QuoteDeleteReportStruct[]) eventData;
                    for (int i = 0; i < quoteDeleteReport.length; i++)
                    {
                        QuoteDetailStruct quoteDetailStruct = quoteDeleteReport[i].quote;
                        updateBook(quoteDetailStruct.quote.sessionName,
                                   quoteDetailStruct.productKeys.classKey,
                                   quoteDetailStruct.productKeys.productKey,
                                   quoteDetailStruct.quote,
                                   quoteDetailStruct.statusChange
                        );
                    }
                }
                break;
            case ChannelType.CB_QUOTE_FILLED_REPORT:
                {
                    QuoteFilledReportStruct quoteFilledReportStruct = (QuoteFilledReportStruct) eventData;
                    FilledReportStruct[] filledReportStructs= quoteFilledReportStruct.filledReport;
                    for (int i = 0; i < filledReportStructs.length; i++)
                    {
                        FilledReportStruct filledReportStruct = filledReportStructs[i];
                        if (filledReportStruct.fillReportType != ReportTypes.STRATEGY_LEG_REPORT)
                        {
                            updateBook(filledReportStruct.sessionName,
                                       quoteFilledReportStruct.productKeys.classKey,
                                       filledReportStruct.productKey,
                                       filledReportStruct);
                        }
                    }

                }
                break;

// TODO: cleanup
            default :
                GUILoggerHome.find().debug(Category + ".channelUpdate()", GUILoggerBusinessProperty.ORDER_BOOK, "Traders Order Book Manager - Unexpected ChannelType = " + channelType);
        }
    }
    protected void updateBook(String sessionName, int classKey, int productKey, BustReportStruct quoteBustReportStruct)
    {
        TradersOrderBook orderBook = null;
        try
        {
            orderBook = find(sessionName, productKey, classKey);
            synchronized (orderBook)
            {
                orderBook.updateBook(quoteBustReportStruct);
            }
        }
        catch (TradersOrderBookInitializationException e)
        {
            GUILoggerHome.find().exception("TradersOrderBookManager.updateBook(QuoteStruct)", "Unable to update OrderBook", e);
        }

    }
    protected void updateBook(String sessionName, int classKey, int productKey, FilledReportStruct quoteFillReportStruct)
    {
        TradersOrderBook orderBook = null;
        try
        {
            orderBook = find(sessionName, productKey, classKey);
            synchronized (orderBook)
            {
                orderBook.updateBook(quoteFillReportStruct);
            }
        }
        catch (TradersOrderBookInitializationException e)
        {
            GUILoggerHome.find().exception("TradersOrderBookManager.updateBook(QuoteStruct)", "Unable to update OrderBook", e);
        }

    }
    protected void updateBook(String sessionName, int classKey, int productKey, QuoteStruct quote, short statusChange)
    {
        TradersOrderBook orderBook = null;
        try
        {
            orderBook = find(sessionName, productKey, classKey);
            synchronized (orderBook)
            {
                if (statusChange == StatusUpdateReasons.CANCEL)
                {
                    orderBook.deleteQuote();
                }
                else
                {
                    orderBook.updateBook(quote);
                }
            }
        }
        catch (TradersOrderBookInitializationException e)
        {
            GUILoggerHome.find().exception("TradersOrderBookManager.updateBook(QuoteStruct)", "Unable to update OrderBook", e);
        }

    }
    protected void updateBook(String sessionName, int classKey, int productKey, OrderStruct order, short statusChange)
    {
        TradersOrderBook orderBook = null;
        try
        {
            orderBook = find(sessionName, productKey, classKey);
            synchronized (orderBook)
            {
                orderBook.updateBook(order);
            }
        }
        catch (TradersOrderBookInitializationException e)
        {
            GUILoggerHome.find().exception("TradersOrderBookManager.updateBook(QuoteStruct)", "Unable to update OrderBook", e);
        }

    }
    public TradersOrderBook find(String sessionName, int productKey, int classKey) throws TradersOrderBookInitializationException
    {
        TradersOrderBook orderBook = get(sessionName, productKey);

        if (orderBook == null)
        {
            orderBook = create(sessionName, productKey, classKey);
        }

        return orderBook;
    }
    private synchronized TradersOrderBook create(String sessionName, int productKey, int classKey)
        throws TradersOrderBookInitializationException
    {
        TradersOrderBook orderBook = get(sessionName, productKey);

        if (orderBook == null)
        {
            orderBook = new TradersOrderBook(sessionName, productKey, classKey);
            bookCollection.put(new SessionKeyContainer(sessionName, productKey), orderBook);
        }
        return orderBook;
    }
    private TradersOrderBook get(String sessionName, int productKey)
    {
        SessionKeyContainer product = new SessionKeyContainer(sessionName, productKey);
        TradersOrderBook orderBook = (TradersOrderBook) bookCollection.get(product);
        return  orderBook;
    }
}
