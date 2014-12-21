package com.cboe.remoteApplication.marketData;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */
public class CurrentMarketRequestManagerFactory {

    private static Hashtable currentMarketRequestManagersByGroupKey;

    public CurrentMarketRequestManagerFactory()
    {
    }

    private static Hashtable getRequestManagers()
    {
        if(currentMarketRequestManagersByGroupKey == null)
        {
            currentMarketRequestManagersByGroupKey = new Hashtable(11);
        }
        return currentMarketRequestManagersByGroupKey;
    }

    public static synchronized CurrentMarketRequestManager find(Integer groupKey)
    {
        return create(groupKey);
    }

    public static CurrentMarketRequestManager create(Integer key)
    {
        CurrentMarketRequestManager currentMarketRequestManager = (CurrentMarketRequestManager)(getRequestManagers().get(key));
        if (currentMarketRequestManager == null)
        {
            currentMarketRequestManager = new CurrentMarketRequestManager(key);
            getRequestManagers().put(key, currentMarketRequestManager);
        }
        return currentMarketRequestManager;
    }

    public static void remove(Integer groupKey)
    {
        CurrentMarketRequestManager currentMarketRequestManager = (CurrentMarketRequestManager)(getRequestManagers().get(groupKey));
        if (currentMarketRequestManager != null)
        {
            currentMarketRequestManager.cleanUp();
            currentMarketRequestManagersByGroupKey.remove(groupKey);
        }
    }
}
