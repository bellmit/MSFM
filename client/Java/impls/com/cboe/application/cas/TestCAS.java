package com.cboe.application.cas;

/**
 * This class is a CAS simulator to test the OSS/QSS.  It
 * also simulates the CBOE event channel publisher
 * to publish events.
 * @author Keith A. Korecky
 */

import java.util.*;

import com.cboe.application.shared.*;

import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.quote.QuoteInfoStruct;

import org.omg.CORBA.*;

import com.cboe.idl.events.*;
import com.cboe.idl.consumers.*;

import com.cboe.interfaces.events.*;
// import com.cboe.interfaces.businessServices.OrderStatusSubscriptionService;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;

import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.domain.util.QuoteStructBuilder;

public class TestCAS {

   private static IECOrderStatusConsumerHome    orderStatusConsumerHome = null;
   private static IECQuoteStatusConsumerHome    quoteStatusConsumerHome = null;
   private static String                     memberKey = "sbtUser";
   private static int[]                   groups = {1};


    public TestCAS()
    {
        super();
    }

    public static void main(String[] args)
    {
         TestCASCallback            newOrderListener = null;
         TestCASCallback            orderUpdateListener = null;
         TestCASCallback            orderAcceptedByBookListener = null;
         TestCASCallback            orderFillReportListener = null;
         TestCASCallback            cancelReportListener = null;
         TestCASCallback            orderQueryListener = null;
         TestCASCallback            acceptOrdersListener = null;

         TestCASCallback            quoteFillReportListener = null;
         TestCASCallback            quoteDeleteReportListener = null;


         DateStruct                     ndate = new DateStruct( (byte)1, (byte)1, (byte)1999 );
         ExchangeFirmStruct              memberFirm = com.cboe.domain.util.StructBuilder.buildExchangeFirmStruct("CBOE","memberFirm");
         OrderIdStruct                  orderId = UnitTestHelper.createOrderIdStruct(  memberFirm, "branch", 1, "cFirm", "19990909" );
         OrderStruct                    newOrder = UnitTestHelper.createNewOrderStruct( "W_AM1",0, 0, ProductTypes.OPTION, memberKey, orderId );
         OrderStruct[]                  newOrders = new OrderStruct[ 1 ];
         FilledReportStruct             filledReportStruct = UnitTestHelper.createFilledReportStruct(newOrder, 1);
         FilledReportStruct[]           filledReport = {filledReportStruct};
//         OrderFilledReportStruct        filledOrder = UnitTestHelper.createOrderFilledReportStruct( newOrder, 1 );
         CancelReportStruct             cancelReportStruct = UnitTestHelper.createCancelReportStruct( newOrder, 1 );
         CancelReportStruct[]           cancelReport = {cancelReportStruct};

         int[]                          quoteKeys = { 1,2,3,4,5 };
         ProductKeysStruct              productKeys = new ProductKeysStruct( (int)0, (int)1, (short)1, (int)1 );
         PriceStruct                    priceStruct = new PriceStruct( (short)PriceTypes.VALUED, (int)1, (int)1 );
         ProductNameStruct              productName = ProductStructBuilder.buildProductNameStruct();
                                        productName.productSymbol = "productSymbol";
                                        productName.exercisePrice = priceStruct;
         QuoteEntryStruct               quoteEntryStruct = QuoteStructBuilder.buildQuoteEntryStruct();
         QuoteStruct                    quoteStruct = QuoteStructBuilder.buildQuoteStruct(quoteEntryStruct);
                                        quoteStruct.userId = memberKey;
                                        quoteStruct.productKey = 1;
         ExchangeFirmStruct             memberfirm = new ExchangeFirmStruct("cboe", "memberFirm");
         QuoteInfoStruct                quoteInfo =  new QuoteInfoStruct(memberKey, memberfirm, (int)0, (int)1, (int)1);  //modify this as per need
         QuoteDetailStruct              quoteDetail = new QuoteDetailStruct(
                                            productKeys, productName, StatusUpdateReasons.NEW, quoteStruct );
         QuoteFilledReportStruct        filledQuote = UnitTestHelper.createNewQuoteFilledReportStruct( quoteDetail );

         newOrders[ 0 ]                 = newOrder;
         newOrderListener               = new TestCASCallback( "newOrderListener" );
         orderUpdateListener            = new TestCASCallback( "orderUpdateListener" );
         orderAcceptedByBookListener    = new TestCASCallback( "orderAcceptedByBookListener" );
         orderFillReportListener        = new TestCASCallback( "orderFillReportListener" );
         cancelReportListener           = new TestCASCallback( "cancelReportListener" );
         orderQueryListener             = new TestCASCallback( "orderQueryListener" );
         acceptOrdersListener           = new TestCASCallback( "acceptOrdersListener" );

         quoteFillReportListener        = new TestCASCallback( "quoteFillReportListener" );
         quoteDeleteReportListener      = new TestCASCallback( "quoteDeleteReportListener" );

        EventChannelAdapterFactory.find().setDynamicChannels(true);


        try
        {
            ////////// MUST BE CALLED /////////
            String [] fileName = { "TestCAS.properties" } ;
            UnitTestHelper.initFFEnv( fileName, 0 );

           System.out.println("---------------------------------> Testing add OrderStatusConsumer listeners...");
            try
            {
/*
               orderStatusConsumerHome = ServicesHelper.getOrderStatusConsumerHome();

               orderStatusConsumerHome.addListener( newOrderListener, new ChannelKey( ChannelType.NEW_ORDER, memberKey ) );
               orderStatusConsumerHome.addListener( orderUpdateListener, new ChannelKey( ChannelType.ORDER_UPDATE, memberKey ) );
               orderStatusConsumerHome.addListener( orderAcceptedByBookListener, new ChannelKey( ChannelType.ORDER_ACCEPTED_BY_BOOK, memberKey ) );
               orderStatusConsumerHome.addListener( orderFillReportListener, new ChannelKey( ChannelType.ORDER_FILL_REPORT, memberKey ) );
               orderStatusConsumerHome.addListener( cancelReportListener, new ChannelKey( ChannelType.CANCEL_REPORT, memberKey ) );
               orderStatusConsumerHome.addListener( orderQueryListener, new ChannelKey( ChannelType.ORDER_QUERY_EXCEPTION, memberKey ) );
               orderStatusConsumerHome.addListener( acceptOrdersListener, new ChannelKey( ChannelType.ACCEPT_ORDERS, memberKey ) );

               System.out.println("--------------------------------> Testing add OrderStatusConsumer listeners...  -  success!");
*/

            }
            catch (Throwable e)
            {
               System.out.println("--------------------------------> Testing add OrderStatusConsumer listeners... - failed.");
               e.printStackTrace();
            }

           System.out.println("--------------------------------> Testing event publisher on order status...");
           try
           {

               OrderStatusEventConsumer orderPublisher = TestPublisherHelper.getOrderStatusChannel();

               orderPublisher.acceptNewOrder( new int[0], StatusUpdateReasons.NEW, newOrder,new String() );
               orderPublisher.acceptOrderUpdate( new int[0], newOrder );
               orderPublisher.acceptOrderAcceptedByBook( groups, newOrder );
               orderPublisher.acceptOrders( new int[0], memberKey, newOrder.orderId.executingOrGiveUpFirm, newOrders );
//               orderPublisher.acceptOrderFillReport(groups, StatusUpdateReasons.NEW, newOrder, filledReport );
               orderPublisher.acceptOrderFillReport(groups, StatusUpdateReasons.FILL, newOrder, filledReport,new String() );
//               orderPublisher.acceptCancelReport( memberKey, groups, StatusUpdateReasons.NEW, newOrder, cancelReport );
               orderPublisher.acceptCancelReport( groups, StatusUpdateReasons.CANCEL, newOrder, cancelReport,new String() );

               System.out.println("--------------------------------> Testing event publisher on order status...  -  success!");
           }
           catch (Throwable e)
           {
             System.out.println("--------------------------------> Testing event publisher on order status... - failed.");
               e.printStackTrace();
           }



           System.out.println("--------------------------------> Testing add QuoteStatusConsumer listeners...");
            try
            {
/*
               quoteStatusConsumerHome = ServicesHelper.getQuoteStatusConsumerHome();

               quoteStatusConsumerHome.addListener( quoteFillReportListener, new ChannelKey( ChannelType.QUOTE_FILL_REPORT, memberKey ) );
               quoteStatusConsumerHome.addListener( quoteDeleteReportListener, new ChannelKey( ChannelType.QUOTE_DELETE_REPORT, memberKey ) );

               System.out.println("--------------------------------> Testing add QuoteStatusConsumer listeners...  -  success!");
*/

            }
            catch (Throwable e)
            {
               System.out.println("--------------------------------> Testing add QuoteStatusConsumer listeners... - failed.");
               e.printStackTrace();
            }

//           System.out.println("--------------------------------> Test direct consumer calls...");
//               orderStatusConsumerHome.find().acceptNewOrder( newOrder );
//               orderStatusConsumerHome.find().acceptOrderUpdate( newOrder );
//               orderStatusConsumerHome.find().acceptOrderAcceptedByBook( newOrder );
//           System.out.println("--------------------------------> Test direct consumer calls...");



           System.out.println("--------------------------------> Testing event publisher on quote status...");
           try
           {

               QuoteStatusEventConsumer quotePublisher = TestPublisherHelper.getQuoteStatusChannel();

//               quotePublisher.acceptQuoteFillReport( new int[0], 5, StatusUpdateReasons.NEW, filledReport );
               quotePublisher.acceptQuoteFillReport( new int[0], quoteInfo, StatusUpdateReasons.NEW, filledReport,new String() );
               quotePublisher.acceptQuoteDeleteReport( new int[0], memberKey, quoteKeys, ActivityReasons.USER ,new String());

               System.out.println("--------------------------------> Testing event publisher on quote status...  -  success!");
           }
           catch (Throwable e)
           {
             System.out.println("--------------------------------> Testing event publisher on quote status... - failed.");
               e.printStackTrace();
           }

            java.lang.Object    waiter = new java.lang.Object();

            synchronized(waiter)
            {
                waiter.wait();
            }

         } catch (Throwable e)
         {
            e.printStackTrace();
         }
    }

}
