package com.cboe.remoteApplication.marketData;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */
public class TickerRequestManagerFactory {

    protected static Hashtable tickerRequestManagersByGroupKey;

    public TickerRequestManagerFactory()
    {
    }

    protected static Hashtable getRequestManagers()
    {
        if(tickerRequestManagersByGroupKey == null)
        {
            tickerRequestManagersByGroupKey = new Hashtable(11);
        }
        return tickerRequestManagersByGroupKey;
    }

    public static synchronized TickerRequestManager find(Integer groupKey)
    {
        return create(groupKey);
    }

    public static TickerRequestManager create(Integer key)
    {
        TickerRequestManager tickerRequestManager = (TickerRequestManager)(getRequestManagers().get(key));
        if (tickerRequestManager == null)
        {
            tickerRequestManager = new TickerRequestManager(key);
            getRequestManagers().put(key, tickerRequestManager);
        }
        return tickerRequestManager;
    }

    public static void remove(Integer groupKey)
    {
        TickerRequestManager tickerRequestManager = (TickerRequestManager)(getRequestManagers().get(groupKey));
        if (tickerRequestManager != null)
        {
            tickerRequestManager.cleanUp();
            tickerRequestManagersByGroupKey.remove(groupKey);
        }
    }
}
