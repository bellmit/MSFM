package com.cboe.remoteApplication.marketData;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */
public class BookDepthRequestManagerFactory {

    private static Hashtable bookDepthRequestManagersByGroupKey;

    public BookDepthRequestManagerFactory()
    {
    }

    private static Hashtable getRequestManagers()
    {
        if(bookDepthRequestManagersByGroupKey == null)
        {
            bookDepthRequestManagersByGroupKey = new Hashtable(11);
        }
        return bookDepthRequestManagersByGroupKey;
    }

    public static synchronized BookDepthRequestManager find(Integer groupKey)
    {
        return create(groupKey);
    }

    public static BookDepthRequestManager create(Integer key)
    {
        BookDepthRequestManager bookDepthRequestManager = (BookDepthRequestManager)(getRequestManagers().get(key));
        if (bookDepthRequestManager == null)
        {
            bookDepthRequestManager = new BookDepthRequestManager(key);
            getRequestManagers().put(key, bookDepthRequestManager);
        }
        return bookDepthRequestManager;
    }

    public static void remove(Integer groupKey)
    {
        BookDepthRequestManager bookDepthRequestManager = (BookDepthRequestManager)(getRequestManagers().get(groupKey));
        if (bookDepthRequestManager != null)
        {
            bookDepthRequestManager.cleanUp();
            bookDepthRequestManagersByGroupKey.remove(groupKey);
        }
    }
}
