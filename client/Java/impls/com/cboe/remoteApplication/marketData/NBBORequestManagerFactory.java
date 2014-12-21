package com.cboe.remoteApplication.marketData;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */
public class NBBORequestManagerFactory {

    private static Hashtable NBBORequestManagersByGroupKey;

    public NBBORequestManagerFactory()
    {
    }

    private static Hashtable getRequestManagers()
    {
        if(NBBORequestManagersByGroupKey == null)
        {
            NBBORequestManagersByGroupKey = new Hashtable(11);
        }
        return NBBORequestManagersByGroupKey;
    }

    public static synchronized NBBORequestManager find(Integer groupKey)
    {
        return create(groupKey);
    }

    public static NBBORequestManager create(Integer key)
    {
        NBBORequestManager NBBORequestManager = (NBBORequestManager)(getRequestManagers().get(key));
        if (NBBORequestManager == null)
        {
            NBBORequestManager = new NBBORequestManager(key);
            getRequestManagers().put(key, NBBORequestManager);
        }
        return NBBORequestManager;
    }

    public static void remove(Integer groupKey)
    {
        NBBORequestManager NBBORequestManager = (NBBORequestManager)(getRequestManagers().get(groupKey));
        if (NBBORequestManager != null)
        {
            NBBORequestManager.cleanUp();
            NBBORequestManagersByGroupKey.remove(groupKey);
        }
    }
}
