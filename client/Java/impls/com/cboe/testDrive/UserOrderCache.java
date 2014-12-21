package com.cboe.testDrive;

/**
 * This class is the Order Callback for use by the Performance Driver Tool (PDT) test scripts.
 *
 * @author Dean Grippo
 */

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.application.test.*;
import com.cboe.idl.consumers.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.*;

import java.util.Hashtable;

public class UserOrderCache
{
    private Hashtable orderCacheBuySide = new Hashtable(95);
    private Hashtable orderCacheSellSide = new Hashtable(95);
    private long low = 0;
    private long high = 0;

    public UserOrderCache(int loginNumber, long numOfTest)
    {
        low = loginNumber*numOfTest;
        high = low + numOfTest -1;
    }

    public synchronized boolean addOrder(OrderDetailStruct order)
    {
        if ( Long.parseLong(order.orderStruct.userAssignedId) > high || Long.parseLong(order.orderStruct.userAssignedId) < low) return false;

        try
        {
            SessionKeyContainer key = new SessionKeyContainer(order.orderStruct.sessionNames[0], order.orderStruct.productKey);

            if (order.orderStruct.side == 'B')
            {
                if (order.orderStruct.leavesQuantity == 0)
                {
                    orderCacheBuySide.remove(key);
                }
                else
                {
                    orderCacheBuySide.put(key, order);
                }
            }
            else
            {
                if (order.orderStruct.leavesQuantity == 0)
                {
                    orderCacheSellSide.remove(key);
                }
                else
                {
                    orderCacheSellSide.put(key, order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        return true;
    }

    public synchronized OrderDetailStruct getOrder(String sessionName, int productKey, char side)
    {
        SessionKeyContainer key = new SessionKeyContainer(sessionName, productKey);
        try
        {
            if (side == 'B')
            {
                return (OrderDetailStruct)orderCacheBuySide.get(key);
            }
            else
            {
                return (OrderDetailStruct)orderCacheSellSide.get(key);
            }
        } catch (Exception e)
        {
            return null;
        }
    }
}
