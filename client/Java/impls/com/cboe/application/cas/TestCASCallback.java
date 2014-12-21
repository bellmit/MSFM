package com.cboe.application.cas;

/**
 * This class is the CAS Callback simulator
 *
 * @author Keith A. Korecky
 */
/**
 * This class is the CAS event consumer simulator which
 * implements a single listener callback
 * @author Keith A. Korecky
 */

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.*;

import com.cboe.application.test.*;

import com.cboe.idl.consumers.*;

import com.cboe.domain.util.*;

import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;

public class TestCASCallback
   implements  EventChannelListener
{
   private String          debugTag = "";
   private boolean          usePrintStruct = true;

   public TestCASCallback()
   {
      this( "DefaultDebugTag" );
   }

   public TestCASCallback( String   logTag )
   {
      super();
      this.debugTag = logTag;
      System.out.println( "TestCASCallback::" + debugTag + " constructor" );
   }

   public void channelUpdate( ChannelEvent event )
   {
//      System.out.println( "\n"
//                           + "**************************************************************************\n"
//                           + "**\n"
//                           + "**             TestCASCallback::" + debugTag + " listener::channelUpdate::" + event.getEventData().toString() + "\n"
//                           + "**\n"
//                           + "**************************************************************************\n"
//                           );

//  System.out.print("|" + event.getEventData().getClass().toString() + "|");

//        System.out.println();
//        System.out.print("TestCASCallback::" + debugTag + " listener::channelUpdate::" );
//        ReflectiveStructTester.printStruct(event.getEventData(), event.getEventData().getClass().toString() );
//        System.out.print("TestCASCallback::" + debugTag + " - listener::channelUpdate" + " - Object::" + event.getEventData().getClass().toString() );
//        if ( usePrintStruct )
//        {
//            ReflectiveStructTester.printStruct(event.getEventData(), event.getEventData().getClass().toString() );
//        }

        ChannelKey key = (ChannelKey)event.getChannel();
        if (key.channelType == ChannelType.CB_LOGOUT) {
            System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ CAS Generated Logout received " + event.getEventData());
            System.exit(1);
        }

        else if (key.channelType == ChannelType.CB_ALL_ORDERS) {
            OrderDetailStruct[] orders = (OrderDetailStruct[]) event.getEventData();
            for (int i = 0; i < orders.length; i++) {
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order status change :: " + getStatusReason(orders[i].statusChange) + " :: " + orders[i].statusChange);
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order number=" + orders[i].orderStruct.orderId.branchSequenceNumber + " :: Orginal=" + orders[i].orderStruct.originalQuantity + " :: Leaves=" + orders[i].orderStruct.leavesQuantity + " ::Traded=" + orders[i].orderStruct.tradedQuantity + " ::Canceled=" + orders[i].orderStruct.cancelledQuantity);
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order number=" + orders[i].orderStruct.orderId.branchSequenceNumber + " :: Average price=" + PriceFactory.create(orders[i].orderStruct.averagePrice).toDouble() + " :: Session average price=" + PriceFactory.create(orders[i].orderStruct.sessionAveragePrice).toDouble());
            }
        }

        else if (key.channelType == ChannelType.CB_FILLED_REPORT) {
            OrderFilledReportStruct filledReport = (OrderFilledReportStruct) event.getEventData();
//            System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order filled report :: Price: " + PriceFactory.create(filledReport.filledReport.price).toDouble() );
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order filled report :: Order status change :: " + getStatusReason(filledReport.filledOrder.statusChange) + " :: " + filledReport.filledOrder.statusChange);
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order number=" + filledReport.filledOrder.orderStruct.orderId.branchSequenceNumber + " :: Orginal=" + filledReport.filledOrder.orderStruct.originalQuantity + " :: Leaves=" + filledReport.filledOrder.orderStruct.leavesQuantity + " ::Traded=" + filledReport.filledOrder.orderStruct.tradedQuantity + " ::Canceled=" + filledReport.filledOrder.orderStruct.cancelledQuantity);
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order number=" + filledReport.filledOrder.orderStruct.orderId.branchSequenceNumber + " :: Average price=" + PriceFactory.create(filledReport.filledOrder.orderStruct.averagePrice).toDouble() + " :: Session average price=" + PriceFactory.create(filledReport.filledOrder.orderStruct.sessionAveragePrice).toDouble());
        }

        else if (key.channelType == ChannelType.CB_ALL_QUOTES) {
            QuoteDetailStruct[] quotes = (QuoteDetailStruct[]) event.getEventData();
            for (int i = 0; i < quotes.length; i++) {
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Quote status change :: " + getStatusReason(quotes[i].statusChange) + " :: " + quotes[i].statusChange);
            }
        }

        else
        {
            System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ received " + key.channelType + "::" + event.getEventData());
        }
   }

   private String getStatusReason(short status)
   {
        switch (status) {
            case StatusUpdateReasons.BOOKED:
                return "BOOKED";
            case StatusUpdateReasons.CANCEL:
                return "CANCEL";
            case StatusUpdateReasons.FILL:
                return "FILL";
            case StatusUpdateReasons.QUERY:
                return "QUERY";
            case StatusUpdateReasons.UPDATE:
                return "UPDATE";
            case StatusUpdateReasons.OPEN_OUTCRY:
                return "OPEN_OUTCRY";
            case StatusUpdateReasons.NEW:
                return "NEW";
            case StatusUpdateReasons.BUST:
                return "BUST";
            case StatusUpdateReasons.REINSTATE:
                return "REINSTATE";
            default:
                return "UNKNOWN";
        }
    }

    public boolean setPrintStructOn( boolean    turnOn )
    {
        boolean     lastVal = usePrintStruct;

        usePrintStruct = turnOn;
        return lastVal;
    }

}


