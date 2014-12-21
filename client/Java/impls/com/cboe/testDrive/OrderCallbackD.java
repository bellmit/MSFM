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

import java.util.*;

public class OrderCallbackD extends com.cboe.idl.cmiCallback.CMIOrderStatusConsumerPOA
{
    private CASMeter casMeter = null;
    private int totalfills = 0;
    private Vector interests = new Vector();
    private UserOrderCache[] orderCaches = null;
    int numberOfInterests = 0;

    public OrderCallbackD(CASMeter meter)
    {
        casMeter = meter;
        System.out.println("Entering OrderCallback::constructor");
    }

    public void addInterest(UserOrderCache userOrderCache)
    {
        interests.add(userOrderCache);
        numberOfInterests++;
    }

    public void acceptNewOrder(com.cboe.idl.cmiOrder.OrderDetailStruct order)
    {
        try {
            casMeter.setEndTime(Integer.valueOf(order.orderStruct.userAssignedId).intValue());
            updateOrderCache(order);
        }
        catch (Exception e) {
            e.printStackTrace() ;
        }
    }

    public void acceptOrderBustReinstateReport(com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct orderBustReinstateReportStruct)
    {
//        try {
//            casMeter.setEndTime(Integer.valueOf(orderDetailStruct[0].orderStruct.userAssignedId).intValue());
//        }
//        catch (Exception e) {
//            e.printStackTrace() ;
//        }
   }

    public void acceptOrderBustReport(com.cboe.idl.cmiOrder.OrderBustReportStruct orderBustReportStruct)
    {
    //        try {
    //            casMeter.setEndTime(Integer.valueOf(orderDetailStruct[0].orderStruct.userAssignedId).intValue());
    //        }
    //        catch (Exception e) {
    //            e.printStackTrace() ;
    //        }
    }

    public void acceptOrderStatus( com.cboe.idl.cmiOrder.OrderDetailStruct[] orderDetailStruct )
    {
    //        try {
    //            casMeter.setEndTime(Integer.valueOf(orderDetailStruct[0].orderStruct.userAssignedId).intValue());
    //        }
    //        catch (Exception e) {
    //            e.printStackTrace() ;
    //        }
    }

    public void acceptOrderCanceledReport(com.cboe.idl.cmiOrder.OrderCancelReportStruct orderCancelReportStruct)
    {
        try {
//            casMeter.setEndTime(Integer.valueOf(orderDetailStruct[0].orderStruct.userAssignedId).intValue());
              casMeter.incrementCancelCount(Integer.valueOf(orderCancelReportStruct.cancelledOrder.orderStruct.userAssignedId).intValue());
        }
        catch (Exception e) {
            e.printStackTrace() ;
        }
    }

    public void acceptOrderFilledReport(com.cboe.idl.cmiOrder.OrderFilledReportStruct orderFilledReportStruct)
    {
        try {
            casMeter.incrementFillCount(Integer.valueOf(orderFilledReportStruct.filledOrder.orderStruct.userAssignedId).intValue());
            updateOrderCache(orderFilledReportStruct.filledOrder);
        }
        catch (Exception e) {
            e.printStackTrace() ;
        }
    }

    private void updateOrderCache (OrderDetailStruct order)
    {
        if (numberOfInterests == 0) return;

        if (orderCaches == null)
        {
            orderCaches = new UserOrderCache[numberOfInterests];
            interests.copyInto(orderCaches);
        }

        for (int i = 0; i < orderCaches.length; i++)
        {
            if (orderCaches[i].addOrder(order)) break;
        }
    }
}



