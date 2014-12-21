package com.cboe.presentation.api;

import java.util.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiConstants.ReportTypes;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.presentation.common.formatters.Sides;
import com.cboe.presentation.common.logging.GUILoggerHome;

//import com.sun.java.util.collections.*;

/**
 * This class is a helper that provides limited quote interfaces to facilitate
 * caching of quotes. It will keep a cached collection of quotes so that round
 * trips to the CAS are not always required.
 * @version 06/30/1999
 * @author Troy Wehrle
 */
public class QuoteCache implements EventChannelListener
{
    protected Map quoteProductKeyMap = null;
    protected Map quotesByClass = null;
    protected Map quotesByProduct = null;
    protected Map quotedClasses = null;
    private QuoteDetailStruct[] emptyQuoteSequence;

    /**
     * QuoteCache constructor comment.
     */
    public QuoteCache()
    {
        super();
    }

    /**
     * Adds or updates the passed quote to the cache.
     * @param quote QuoteDetailStruct
     */
    public synchronized void addQuote(QuoteDetailStruct quote)
    {
        SessionKeyContainer key = new SessionKeyContainer(quote.quote.sessionName, quote.productKeys.productKey);
        getQuoteProductKeyMap().put(key, quote);
        getQuoteMapForProduct(quote.productKeys.productKey).put(quote.quote.sessionName, quote);
        getQuoteMapForClass(quote.productKeys.classKey).put(key, quote);
        getQuotesByQuotedClasses(quote.quote.sessionName, quote.productKeys.classKey).put(key, quote);
    }

    public synchronized SessionKeyContainer[] getAllQuotedClasses()
    {
        Set sessionClasses = getQuotedClasses().keySet();
        SessionKeyContainer[] keys = new SessionKeyContainer[sessionClasses.size()];
        keys = (SessionKeyContainer[])sessionClasses.toArray(keys);
        return keys;
    }

    public synchronized SessionKeyContainer[] getAllQuotedClassesForSession(String sessionName)
    {
        Vector sessionKeys = new Vector();
        SessionKeyContainer[] keys = getAllQuotedClasses();
        for (int i=0; i< keys.length; i++) {
            if (keys[i].getSessionName().equals(sessionName)) {
                sessionKeys.add(keys[i]);
            }
        }
        return (SessionKeyContainer[])sessionKeys.toArray(keys);
    }

    /**
     * Adds or updates the passed quotes to the cache.
     * @param quote An array of QuoteDetailStruct
     */
    public void addQuotes(QuoteDetailStruct[] quotes)
    {
        for(int i = 0; i < quotes.length; i++)
        {
            addQuote(quotes[i]);
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
        switch(channelType)
        {
            case ChannelType.CB_ALL_QUOTES:
                {
                    // The V2 QuoteStatusConsumer will not publish fill, bust, or cancel events
                    // in the CB_ALL_QUOTES channel. Those events are published on separate channels.
                    QuoteDetailStruct[] quoteDetails = (QuoteDetailStruct[]) eventData;
                    for (int i = 0; i < quoteDetails.length; i++)
                    {
                        if (quoteDetails[i].statusChange == StatusUpdateReasons.UPDATE ||
                                quoteDetails[i].statusChange == StatusUpdateReasons.QUERY ||
                                quoteDetails[i].statusChange == StatusUpdateReasons.NEW)
                        {
                            //quote has been added or updated
                            addQuote(quoteDetails[i]);
                        }
                    }
                }
                break;
            case ChannelType.CB_QUOTE_FILLED_REPORT:
                {
                    // V2 does not double publish fill events on the CB_ALL_QUOTES channel, so need 
                    //    to handle quote fill separately.
                    QuoteFilledReportStruct quoteFilledReportStruct = (QuoteFilledReportStruct) eventData;
                    handleFillReport(quoteFilledReportStruct);
                }
                break;

// TODO: finish cleanup of quote cache
            case ChannelType.CB_QUOTE_CANCEL_REPORT_V2:
                {
                    // V2 does not double publish cancel events on the CB_ALL_QUOTES channel, so need 
                    //    to handle quote cancel separately.
                    QuoteDeleteReportStruct[] quoteDeleteReports = (QuoteDeleteReportStruct[]) eventData;
                    for (int i = 0; i < quoteDeleteReports.length; i++)
                    {
                        removeQuote(quoteDeleteReports[i].quote);
                    }
                }
                break;
            default:
                //don't know what happened to quote, should be one of the other cases.
        }

    }
    protected void handleFillReport(QuoteFilledReportStruct quoteFilledReportStruct)
    {
        for (int i = 0; i < quoteFilledReportStruct.filledReport.length; i++)
        {
            FilledReportStruct filledReportStruct = quoteFilledReportStruct.filledReport[i];
            if (filledReportStruct.fillReportType != ReportTypes.STRATEGY_LEG_REPORT)
            {
                QuoteDetailStruct quote = getQuote(filledReportStruct.sessionName,
                                                   filledReportStruct.productKey);
                if(quote != null)
                {
                    quote.statusChange = quoteFilledReportStruct.statusChange;
                    if(Sides.isBuyEquivalent(filledReportStruct.side))
                    {
                        quote.quote.bidQuantity = filledReportStruct.leavesQuantity;
                    }
                    else
                    {
                        quote.quote.askQuantity = filledReportStruct.leavesQuantity;
                    }
                }
                else
                {
                     GUILoggerHome.find().alarm("Got fill report and cannot find quote ", quoteFilledReportStruct);
                }
            }
        }
    }
    /**
     * Returns all of the quotes that are cached.
     * @return An array of <code>QuoteDetailStruct</code>'s
     */
    public synchronized QuoteDetailStruct[] getAllQuotes()
    {
        if(!getQuoteProductKeyMap().isEmpty())
        {
            int length = getQuoteProductKeyMap().values().size();
            return (QuoteDetailStruct[])getQuoteProductKeyMap().values().toArray(new QuoteDetailStruct[length]);
        }
        else
        {
            return new QuoteDetailStruct[0];
        }
    }
    /**
     * Returns the quote from the cache with the product key passed.
     * @param productKey integer product key of quote to get.
     * @return A <code>QuoteDetailStruct</code>
     */
    public synchronized QuoteDetailStruct getQuote(String sessionName, int productKey)
    {
        return (QuoteDetailStruct)getQuoteMapForProduct(productKey).get(sessionName);
    }

    /**
     * Returns the quote from the cache with the product key passed.
     * @param productKey integer product key of quote to get.
     * @return A <code>QuoteDetailStruct</code>
     */
    public synchronized QuoteDetailStruct[] getQuotesForProduct(int productKey)
    {
        int length = getQuoteMapForProduct(productKey).values().size();
        return (QuoteDetailStruct[])getQuoteMapForProduct(productKey).values().toArray(new QuoteDetailStruct[length]);
    }

    /**
     * Returns the quote from the cache with the product key passed.
     * @param productKey integer product key of quote to get.
     * @return A <code>QuoteDetailStruct</code>
     */
    public synchronized QuoteDetailStruct[] getQuotesForClass(int classKey)
    {
        int length = getQuoteMapForClass(classKey).values().size();
        return (QuoteDetailStruct[])getQuoteMapForClass(classKey).values().toArray(new QuoteDetailStruct[length]);
    }

    /**
     * Returns a zero length QuoteDetailStruct array
     * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
     */
    private QuoteDetailStruct[] getEmptyQuoteSequence()
    {
        if ( emptyQuoteSequence == null )
        {
            emptyQuoteSequence = new QuoteDetailStruct[0];
        }
        return emptyQuoteSequence;
    }

    /**
     * Gets the Map that correlates product keys to quotes.
     * @return Map
     */
    private Map getQuoteProductKeyMap()
    {
        if(quoteProductKeyMap == null)
        {
            quoteProductKeyMap = new HashMap(101);
        }
        return quoteProductKeyMap;
    }

    private Map getQuoteMapForClass(int classKey)
    {
        Integer key = new Integer(classKey);
        Map quotes = (Map)getQuotesByClass().get(key);
        if (quotes == null)
        {
            quotes = new HashMap(101);
            getQuotesByClass().put(key,quotes);
        }
        return quotes;
    }

    private Map getQuoteMapForProduct(int productKey)
    {
        Integer key = new Integer(productKey);
        Map quotes = (Map)getQuotesByProduct().get(key);
        if (quotes == null)
        {
            quotes = new HashMap(101);
            getQuotesByProduct().put(key, quotes);
        }
        return quotes;
    }

    private Map getQuotesByQuotedClasses(String sessionName, int classKey)
    {
        SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
        Map quotes = (Map)getQuotedClasses().get(key);
        if (quotes == null)
        {
            quotes = new HashMap(101);
            getQuotedClasses().put(key, quotes);
        }
        return quotes;
    }

    private Map getQuotedClasses()
    {
        if(quotedClasses == null)
        {
            quotedClasses = new HashMap(101);
        }
        return quotedClasses;
    }

    /**
     * Gets the Map that correlates class keys to quotes.
     * @return Map
     */
    private Map getQuotesByClass()
    {
        if(quotesByClass == null)
        {
            quotesByClass = new HashMap(101);
        }
        return quotesByClass;
    }

    /**
     * Gets the Map that correlates class keys to quotes.
     * @return Map
     */
    private Map getQuotesByProduct()
    {
        if(quotesByProduct == null)
        {
            quotesByProduct = new HashMap(101);
        }
        return quotesByProduct;
    }

    /**
     * Removes the passed quote
     * @param quote Quote to remove.
     */
    private void removeQuote(QuoteDetailStruct quote)
    {
        SessionKeyContainer key = new SessionKeyContainer(quote.quote.sessionName, quote.productKeys.productKey);
        getQuoteProductKeyMap().remove(key);
        getQuoteMapForProduct(quote.productKeys.productKey).remove(quote.quote.sessionName);
        getQuoteMapForClass(quote.productKeys.classKey).remove(key);
        getQuotesByQuotedClasses(quote.quote.sessionName, quote.productKeys.classKey).remove(key);

        // If there aren't any more quotes for the class, remove it from the list
        key = new SessionKeyContainer(quote.quote.sessionName, quote.productKeys.classKey);
        Map quotes = (Map)getQuotedClasses().get(key);
        if (quotes.size() == 0) {
            getQuotedClasses().remove(key);
        }
    }
    /**
     * Removes the passed quotes.
     * @param quotes An array of Quotes to remove.
     */
    private void removeQuotes(QuoteDetailStruct[] quotes)
    {
        for(int i = 0; i < quotes.length; i++)
        {
            removeQuote(quotes[i]);
        }
    }
    /**
     * Returns a String that represents the value of this cache.
     * @return A string representation of this cache.
     */
    public synchronized String toString()
    {
        QuoteDetailStruct[] detailedQuotes = getAllQuotes();

        StringBuffer buffer = new StringBuffer(detailedQuotes.length * 120);

        for(int i = 0; i < detailedQuotes.length; i++)
        {
            buffer.append('[');
            buffer.append("quoteKey=").append(detailedQuotes[i].quote.quoteKey).append(';');
            buffer.append("productKey=").append(detailedQuotes[i].productKeys.productKey).append(';');
            buffer.append("productName=").append(detailedQuotes[i].productName.productSymbol).append(';');
            buffer.append(']');
        }
        return buffer.toString();
    }
}
