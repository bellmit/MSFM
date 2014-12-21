package com.cboe.testDrive;

/**
 * This class is the Order Callback for use by the Performance Driver Tool (PDT) test scripts.
 *
 * @author Dean Grippo
 */

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class OrderCallback extends BObject implements com.cboe.interfaces.callback.OrderStatusConsumer
{

   public OrderCallback()
   {
     System.out.println("Entering OrderCallback::constructor");
   }

   public void acceptOrderBustReinstateReport(com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct orderBustReinstateReportStruct)
   {
     System.out.println("Entering OrderCallback::acceptOrderBustReinstateReportStruct");
   }

   public void acceptOrderBustReport(com.cboe.idl.cmiOrder.OrderBustReportStruct orderBustReportStruct)
   {
      System.out.println("Entering OrderCallback::acceptOrderBustReport");
   }

   public void acceptOrderStatus( com.cboe.idl.cmiOrder.OrderDetailStruct[] orderDetailStruct )
   {
      System.out.println("Entering OrderCallback::acceptOrder");
   }

   public void acceptOrderCanceledReport(com.cboe.idl.cmiOrder.OrderCancelReportStruct orderCancelReportStruct)
   {
     System.out.println("Entering OrderCallback::acceptOrderCanceledReport");
   }

   public void acceptOrderFilledReport(com.cboe.idl.cmiOrder.OrderFilledReportStruct orderFilledReportStruct)
   {
      System.out.println("Entering OrderCallback::acceptOrderFilledReport");
   }
    public void acceptNewOrder(OrderDetailStruct order)
    {
      System.out.println("Entering OrderCallback::acceptNewOrder");
    }



}



