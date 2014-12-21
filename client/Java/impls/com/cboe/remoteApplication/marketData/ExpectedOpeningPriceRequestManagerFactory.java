package com.cboe.remoteApplication.marketData;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */
public class ExpectedOpeningPriceRequestManagerFactory {

    private static Hashtable expectedOpeningPriceRequestManagersByGroupKey;

    public ExpectedOpeningPriceRequestManagerFactory()
    {
    }

    private static Hashtable getRequestManagers()
    {
        if(expectedOpeningPriceRequestManagersByGroupKey == null)
        {
            expectedOpeningPriceRequestManagersByGroupKey = new Hashtable(11);
        }
        return expectedOpeningPriceRequestManagersByGroupKey;
    }

    public static synchronized ExpectedOpeningPriceRequestManager find(Integer groupKey)
    {
        return create(groupKey);
    }

    public static ExpectedOpeningPriceRequestManager create(Integer key)
    {
        ExpectedOpeningPriceRequestManager expectedOpeningPriceRequestManager = (ExpectedOpeningPriceRequestManager)(getRequestManagers().get(key));
        if (expectedOpeningPriceRequestManager == null)
        {
            expectedOpeningPriceRequestManager = new ExpectedOpeningPriceRequestManager(key);
            getRequestManagers().put(key, expectedOpeningPriceRequestManager);
        }
        return expectedOpeningPriceRequestManager;
    }

    public static void remove(Integer groupKey)
    {
        ExpectedOpeningPriceRequestManager expectedOpeningPriceRequestManager = (ExpectedOpeningPriceRequestManager)(getRequestManagers().get(groupKey));
        if (expectedOpeningPriceRequestManager != null)
        {
            expectedOpeningPriceRequestManager.cleanUp();
            expectedOpeningPriceRequestManagersByGroupKey.remove(groupKey);
        }
    }
}
