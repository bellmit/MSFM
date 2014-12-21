package com.cboe.presentation.api;

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
import com.cboe.idl.cmiMarketData.*;

import com.cboe.application.test.*;

import com.cboe.domain.util.*;
import com.cboe.interfaces.domain.CurrentMarketProductContainer;

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
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order status change :: " + getStatusReason(orders[i].statusChange) + " :: " + orders[i].statusChange + " Order date = " + orders[i].orderStruct.orderId.orderDate);
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order number=" + orders[i].orderStruct.orderId.branchSequenceNumber + " :: Orginal=" + orders[i].orderStruct.originalQuantity + " :: Leaves=" + orders[i].orderStruct.leavesQuantity + " ::Traded=" + orders[i].orderStruct.tradedQuantity + " ::Canceled=" + orders[i].orderStruct.cancelledQuantity);
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order number=" + orders[i].orderStruct.orderId.branchSequenceNumber + " :: Average price=" + PriceFactory.create(orders[i].orderStruct.averagePrice).toDouble() + " :: Session average price=" + PriceFactory.create(orders[i].orderStruct.sessionAveragePrice).toDouble());
            }
        }

        else if (key.channelType == ChannelType.CB_QUOTE_FILLED_REPORT)
        {
            QuoteFilledReportStruct quoteFilledReportStruct = (QuoteFilledReportStruct)event.getEventData();
            for (int i = 0; i < quoteFilledReportStruct.filledReport.length; i++) {
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Quote FILL :: FillReportType=" + quoteFilledReportStruct.filledReport[i].fillReportType + 
                        " :: quoteKey=" + quoteFilledReportStruct.quoteKey + 
                        " :: price=" + PriceFactory.create(quoteFilledReportStruct.filledReport[i].price).toDouble() +
                        " :: tradedQuantity=" + quoteFilledReportStruct.filledReport[i].tradedQuantity +
                        " :: leavesQuantity=" + quoteFilledReportStruct.filledReport[i].leavesQuantity);
            }
        }

        else if (key.channelType == ChannelType.CB_QUOTE_CANCEL_REPORT_V2)
        {
            QuoteDeleteReportStruct[] quoteDeleteStructs = (QuoteDeleteReportStruct[]) event.getEventData();
            for (int i = 0; i < quoteDeleteStructs.length; i++) {
                QuoteDetailStruct quote = quoteDeleteStructs[i].quote;
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Quote status change :: " + getStatusReason(quote.statusChange) + " :: " + quote.statusChange);
            }
        }

        else if (key.channelType == ChannelType.CB_QUOTE_BUST_REPORT)
        {
            QuoteBustReportStruct quoteBust = (QuoteBustReportStruct)event.getEventData();
            for (int i = 0; i < quoteBust.bustedReport.length; i++)
            {
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Quote status change :: " + getStatusReason(quoteBust.statusChange) + 
                        " :: quoteKey=" + quoteBust.quoteKey +
                        " :: bustReportType=" + quoteBust.bustedReport[i].bustReportType + 
                        " :: price=" + PriceFactory.create(quoteBust.bustedReport[i].price).toDouble() +
                        " :: bustedQuantity=" + quoteBust.bustedReport[i].bustedQuantity);
            }
        }

        else if (key.channelType == ChannelType.CB_FILLED_REPORT) {
            OrderFilledReportStruct filledReport = (OrderFilledReportStruct) event.getEventData();
            for (int i =0; i<filledReport.filledReport.length; i++) {
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Order filled report :: Price: " + PriceFactory.create(filledReport.filledReport[i].price).toDouble() );
            }
        }

        else if (key.channelType == ChannelType.CB_ALL_QUOTES) {
            QuoteDetailStruct[] quotes = (QuoteDetailStruct[]) event.getEventData();
            for (int i = 0; i < quotes.length; i++) {
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ Quote status change :: " + getStatusReason(quotes[i].statusChange) + " :: " + quotes[i].statusChange);
            }
        }

        else if ((key.channelType == ChannelType.CB_RECAP_BY_CLASS) ||
                (key.channelType == ChannelType.CB_RECAP_BY_PRODUCT)) {
            RecapStruct recap = (RecapStruct) event.getEventData();
            System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ RECAP :: class = " + recap.productKeys.classKey + " product = " + recap.productKeys.productKey + " session = " + recap.sessionName);
        }

        else if ((key.channelType == ChannelType.CB_CURRENT_MARKET_BY_CLASS) ||
                (key.channelType == ChannelType.CB_CURRENT_MARKET_BY_PRODUCT)) {
            CurrentMarketStruct currentMarket = (CurrentMarketStruct) event.getEventData();
            System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ CURRENT MARKET :: class = " + currentMarket.productKeys.classKey + " product = " + currentMarket.productKeys.productKey + " session = " + currentMarket.sessionName);
        }

        else if ((key.channelType == ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3)) {
            CurrentMarketContainerImpl currentMarketContainer = (CurrentMarketContainerImpl) event.getEventData();
            CurrentMarketStruct[] currentMarkets =  currentMarketContainer.getBestMarkets();
            if (currentMarkets != null && currentMarkets.length > 0)
            {
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ CURRENT MARKET V3 :: Class = " + currentMarkets[0].productKeys.classKey + " product = " + currentMarkets[0].productKeys.productKey + " session = " + currentMarkets[0].sessionName);
            }
        }

        else if ((key.channelType == ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3)) {
            CurrentMarketProductContainer  currentMarketProdContainer = (CurrentMarketProductContainer) event.getEventData();
            CurrentMarketStruct currentMarket = currentMarketProdContainer.getBestMarket();
            CurrentMarketStruct currentMarketPub = currentMarketProdContainer.getBestPublicMarketAtTop();
            if (currentMarket != null)
            {
                System.out.println("$$$$ ~~~~ EVENT ~~~~ $$$$ CURRENT MARKET V3 :: Class = " + currentMarket.productKeys.classKey + " product = " + currentMarket.productKeys.productKey + " session = " + currentMarket.sessionName);
                if (currentMarketPub != null && currentMarketPub.askSizeSequence != null && currentMarketPub.askSizeSequence.length > 0)
                {
                    for (int i = 0; i < currentMarketPub.askSizeSequence.length; ++i)
                        System.out.println("---- ~~~~ ---- ~~~~ $$$$ CURRENT MARKET V3 :: " + (currentMarketPub.askSizeSequence[i].volumeType == VolumeTypes.CUSTOMER_ORDER ? "public" : (currentMarketPub.askSizeSequence[i].volumeType == VolumeTypes.PROFESSIONAL_ORDER ? "professional" : "?")) + " quantity at top =  " + currentMarketPub.askSizeSequence[i].quantity);
                }
                if (currentMarketPub != null && currentMarketPub.bidSizeSequence != null && currentMarketPub.bidSizeSequence.length > 0)
                {
                    for (int i = 0; i < currentMarketPub.bidSizeSequence.length; ++i)
                        System.out.println("---- ~~~~ ---- ~~~~ $$$$ CURRENT MARKET V3 :: " + (currentMarketPub.bidSizeSequence[i].volumeType == VolumeTypes.CUSTOMER_ORDER ? "public" : (currentMarketPub.bidSizeSequence[i].volumeType == VolumeTypes.PROFESSIONAL_ORDER ? "professional" : "?")) + " quantity at top =  " + currentMarketPub.bidSizeSequence[i].quantity);
                }
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


