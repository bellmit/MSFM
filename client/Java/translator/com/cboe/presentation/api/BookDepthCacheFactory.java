package com.cboe.presentation.api;

import java.util.Hashtable;
import java.util.HashMap;
import com.cboe.exceptions.*;

public class BookDepthCacheFactory
{
    private static HashMap sessions = new HashMap();
    private final static int size = 100;

    public BookDepthCacheFactory()
    {
        super();
    }

    public static BookDepthCache find(String sessionName) {
        synchronized(sessions)
        {
            BookDepthCache bookDepthCache = (BookDepthCache)sessions.get(sessionName);
            if (bookDepthCache == null) {
                bookDepthCache = new BookDepthCache(size, sessionName);
                sessions.put(sessionName, bookDepthCache);
            }
            return bookDepthCache;
        }
    }

    public static BookDepthCache create(String sessionName)
    {
        return find( sessionName );
    }

    public static void remove ( String sessionName )
        throws SystemException
    {
        synchronized(sessions)
        {
            BookDepthCache bookDepthCache = (BookDepthCache)sessions.get(sessionName);
            if (bookDepthCache != null)
            {
                bookDepthCache.cacheCleanUp();
                sessions.remove(sessionName);
            }
        }
    }
}
