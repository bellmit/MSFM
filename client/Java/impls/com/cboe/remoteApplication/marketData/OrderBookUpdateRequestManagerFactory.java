package com.cboe.remoteApplication.marketData;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */
public class OrderBookUpdateRequestManagerFactory {

    private static Hashtable orderBookUpdateRequestManagersByGroupKey;

    public OrderBookUpdateRequestManagerFactory()
    {
    }

    private static Hashtable getRequestManagers()
    {
        if(orderBookUpdateRequestManagersByGroupKey == null)
        {
            orderBookUpdateRequestManagersByGroupKey = new Hashtable(11);
        }
        return orderBookUpdateRequestManagersByGroupKey;
    }

    public static synchronized OrderBookUpdateRequestManager find(Integer groupKey)
    {
        return create(groupKey);
    }

    public static OrderBookUpdateRequestManager create(Integer key)
    {
        OrderBookUpdateRequestManager orderBookUpdateRequestManager = (OrderBookUpdateRequestManager)(getRequestManagers().get(key));
        if (orderBookUpdateRequestManager == null)
        {
            orderBookUpdateRequestManager = new OrderBookUpdateRequestManager(key);
            getRequestManagers().put(key, orderBookUpdateRequestManager);
        }
        return orderBookUpdateRequestManager;
    }

    public static void remove(Integer groupKey)
    {
        OrderBookUpdateRequestManager orderBookUpdateRequestManager = (OrderBookUpdateRequestManager)(getRequestManagers().get(groupKey));
        if (orderBookUpdateRequestManager != null)
        {
            orderBookUpdateRequestManager.cleanUp();
            orderBookUpdateRequestManagersByGroupKey.remove(groupKey);
        }
    }
}
