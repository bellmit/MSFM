package com.cboe.presentation.api;

import java.util.*;

import com.cboe.idl.cmiMarketData.*;


/**
 * This hides the hashtable implementation of the market data collection.
 * @author William Wei
 */
public class BookDepthCache
{
    private HashMap bookDepthCache = null;
    private String sessionName;

    /**
     * Creates the internal hashtable for best book struct storage.
     */
    public BookDepthCache(int hashSize, String sessionName)
    {
        super();

        this.sessionName = sessionName;

        bookDepthCache = new HashMap(hashSize);
    }

//    public boolean containsBookDepth(Integer key)
//    {
//        return bookDepthCache.containsKey( key );
//    }

    /**
     * @return boolean
     * @param productKey com.cboe.application.shared.ProductKeyStructContainer
     */
    public BookDepthStruct getBookDepth(Integer key)
    {
        synchronized(bookDepthCache)
        {
            return (BookDepthStruct) bookDepthCache.get( key );
        }
    }

    /**
     * @return boolean
     * @param productKey com.cboe.application.shared.ProductKeyStructContainer
     */
    public void removeBookDepth(Integer key)
    {
        synchronized(bookDepthCache)
        {
            bookDepthCache.remove( key );
        }
    }

    /**
     * @return void
     * @param BookDepthStruct
     */
    public void addBookDepth(BookDepthStruct bookDepth)
    {
        synchronized(bookDepthCache)
        {
            Integer key = new Integer(bookDepth.productKeys.productKey);
            BookDepthStruct oldBookDepth = (BookDepthStruct)bookDepthCache.put(key, bookDepth);

            // If book depth was cached already, check that new transactionSequenceNumber is greater or
            // equal than old one.  If this is not the case put the old bookDepth back.
            if ( oldBookDepth != null &&
                 bookDepth.transactionSequenceNumber < oldBookDepth.transactionSequenceNumber )
            {
                bookDepthCache.put(key, oldBookDepth);
            }
        }
    }

    public void cacheCleanUp()
    {
        synchronized(bookDepthCache)
        {
            bookDepthCache.clear();
        }
    }

}
