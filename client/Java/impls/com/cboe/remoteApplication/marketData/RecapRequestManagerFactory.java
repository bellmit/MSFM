package com.cboe.remoteApplication.marketData;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */
public class RecapRequestManagerFactory {

    private static Hashtable recapRequestManagersByGroupKey;

    public RecapRequestManagerFactory()
    {
    }

    private static Hashtable getRequestManagers()
    {
        if(recapRequestManagersByGroupKey == null)
        {
            recapRequestManagersByGroupKey = new Hashtable(11);
        }
        return recapRequestManagersByGroupKey;
    }

    public static synchronized RecapRequestManager find(Integer groupKey)
    {
        return create(groupKey);
    }

    public static RecapRequestManager create(Integer key)
    {
        RecapRequestManager recapRequestManager = (RecapRequestManager)(getRequestManagers().get(key));
        if (recapRequestManager == null)
        {
            recapRequestManager = new RecapRequestManager(key);
            getRequestManagers().put(key, recapRequestManager);
        }
        return recapRequestManager;
    }

    public static void remove(Integer groupKey)
    {
        RecapRequestManager recapRequestManager = (RecapRequestManager)(getRequestManagers().get(groupKey));
        if (recapRequestManager != null)
        {
            recapRequestManager.cleanUp();
            recapRequestManagersByGroupKey.remove(groupKey);
        }
    }
}
