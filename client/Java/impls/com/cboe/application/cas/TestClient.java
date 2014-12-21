package com.cboe.application.cas;

/**
 * This class is the CAS client simulator.  It
 * also simulates the CBOE event channel publisher
 * to publish events.
 * @author Jeff Illian
 * @author Connie Feng
 * @author Thomas Lynch
 */

import java.util.*;

import com.cboe.application.shared.*;

//import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiCallbackV3.POA_CMICurrentMarketConsumer_tie;
import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumerHelper;
import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmi.*;
import com.cboe.delegates.callback.*;
import com.cboe.application.test.*;

import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.ReflectiveStructBuilder;

import com.cboe.idl.events.*;
import com.cboe.idl.consumers.*;
import com.cboe.idl.cmiIntermarket.IntermarketUserAccess;
import com.cboe.idl.cmiIntermarket.IntermarketSessionManagerStruct;
import com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.interfaces.events.*;

import com.cboe.util.event.*;

import com.cboe.util.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

public class TestClient {
    // static final variables
    private static final boolean FOR_FIRM = true;
    private static final boolean FOR_USER = false;

    private static final boolean GMD = true;
    private static final boolean NOT_GMD = false;

    // class keys
    private static final int CLASS1 = 515;
    private static final int CLASS2 = 2752527;


    public static RemoteConnection connection;
    public static SessionManagerStructV2 sessionManagerV2;
    public static UserSessionManager session;
    public static UserSessionManagerV2 sessionV2;
    public static UserSessionManagerV3 sessionV3;
    
    public static IntermarketSessionManagerStruct imSessionStruct;
    public static ProductTypeStruct[] productTypes;
    public static SessionProductStruct[] products;
    public static SessionClassStruct[] classes;
    public static TradingSessionStruct[] sessions;
    public static TestCallback callback;
    public static int[]  groups = {1};
    private static OrderIdStruct lastOrderIdStruct = null;
    private static String userId;
    private final static String USER_ID = "CV1";
    //private final static String USER_ID = "HBB";
    
    private static int orderId;
    private static POA poaReference;

    protected static void initPOA () {
        try {
            ORB orb = com.cboe.ORBInfra.ORB.Orb.init();
            POA poa = POAHelper.narrow(orb.resolve_initial_references ("RootPOA"));
            poaReference = poa;
            poa.the_POAManager().activate();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected static POA getPOA(){
        return poaReference;
    }

    public TestClient() {
        super();
    }

    /** initializes the ORB connection object */
    public static void initORBConnection(String[] args)
    {
        if ( connection == null )
        {
            connection = RemoteConnectionFactory.create(args);
            EventChannelAdapterFactory.find().setDynamicChannels(true);
        }
    }

    private static int getRandomOrderId()
    {
        Random r     = new Random();
        int    max   = 9999;
        int    min   = 1000;
        int    range = max - min + 1;
        int    orderId;

        orderId = r.nextInt(range) + min;
        return orderId;
    }
    private static String getSystemProperty(String propertyName)
    {
        Properties props = System.getProperties();

        String theProperty = props.getProperty(propertyName);

        return theProperty;
    }

    /** initializes the user session object */

    public static TestCallback getCallback() {
        if (callback == null) {
            callback = new TestCallback();
        }
        return callback;
    }

    public static void initImUserSession()
    {   try {
            userId = getSystemProperty("USER_ID");
            if (userId == null) {
                userId = USER_ID;
            }

            if ( session == null )
            {
                IntermarketUserAccess userAccess =  TestImUserAccessFactory.find();
                UserLogonStruct logonStruct = new UserLogonStruct(userId, userId, "2.0", LoginSessionModes.NETWORK_TEST);
                //UserLogonStruct logonStruct = new UserLogonStruct(userId, userId, "2.0", LoginSessionModes.STAND_ALONE_TEST);

                initPOA();
                TestCallback callbackConsumer = getCallback();

                UserSessionAdminConsumerDelegate clientListener = new UserSessionAdminConsumerDelegate(callbackConsumer);
                org.omg.CORBA.Object orbObject = getPOA().servant_to_reference(clientListener);
                CMIUserSessionAdmin cmiCallback = CMIUserSessionAdminHelper.narrow(orbObject);


                imSessionStruct = userAccess.logon(logonStruct, LoginSessionTypes.SECONDARY, cmiCallback, true);


            }
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    public static void initUserSession()
    {   try {
            userId = getSystemProperty("USER_ID");
            if (userId == null) {
                userId = USER_ID;
            }

            if ( session == null )
            {
                //commented only for now RETURN AFTER
                UserAccessV3 userAccess = TestUserAccessV3Factory.find();
                //UserLogonStruct logonStruct = new UserLogonStruct(userId, userId, "2.1", LoginSessionModes.STAND_ALONE_TEST);
                //UserLogonStruct logonStruct = new UserLogonStruct(userId, userId, "2.1", LoginSessionModes.STAND_ALONE_TEST);
                UserLogonStruct logonStruct = new UserLogonStruct(userId, userId, "2.1", LoginSessionModes.NETWORK_TEST);
                
                /**
                UserAccessV2 userAccess =  TestUserAccessV2Factory.find();
                UserLogonStruct logonStruct = new UserLogonStruct("HBB", "HBB", "2.1", LoginSessionModes.PRODUCTION);
                ****/
                
                initPOA();
                TestCallback callbackConsumer = getCallback();

                UserSessionAdminConsumerDelegate clientListener = new UserSessionAdminConsumerDelegate(callbackConsumer);
                org.omg.CORBA.Object orbObject = getPOA().servant_to_reference(clientListener);
                CMIUserSessionAdmin cmiCallback = CMIUserSessionAdminHelper.narrow(orbObject);
                
                /***comment out these 3 lines to test V3
                sessionManagerV2 = userAccess.logon(logonStruct, LoginSessionTypes.SECONDARY, cmiCallback, false);
                session = sessionManagerV2.sessionManager;
                sessionV2 = sessionManagerV2.sessionManagerV2;
                ****/
                
                sessionV3 = userAccess.logon(logonStruct, LoginSessionTypes.PRIMARY, cmiCallback, false);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    /** gets product types  */
    public static void testGetSessions()
    {
        try {
            initPOA();
            TestCallback callbackConsumer = getCallback();

            TradingSessionStatusConsumerDelegate sessionListener = new TradingSessionStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object sessionObject = getPOA().servant_to_reference(sessionListener);
            CMITradingSessionStatusConsumer theSessionConsumer = CMITradingSessionStatusConsumerHelper.narrow(sessionObject);

            sessions = session.getTradingSession().getCurrentTradingSessions(theSessionConsumer);
            System.out.println("Got " + sessions.length + " sessions");
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    /** gets product types  */
    public static void testGetImSession()
    {
        try {
            initPOA();
            TestCallback callbackConsumer = getCallback();

            TradingSessionStatusConsumerDelegate sessionListener = new TradingSessionStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object sessionObject = getPOA().servant_to_reference(sessionListener);
            CMITradingSessionStatusConsumer theSessionConsumer = CMITradingSessionStatusConsumerHelper.narrow(sessionObject);

            sessions = session.getTradingSession().getCurrentTradingSessions(theSessionConsumer);
            System.out.println("Got " + sessions.length + " sessions");
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    /** gets product types  */
    public static void testGetProductTypes()
    {
        try {
            productTypes = session.getTradingSession().getProductTypesForSession(sessions[0].sessionName);
            System.out.println("Got " + productTypes.length + " productTypes for Session = " + sessions[0].sessionName);
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    /** gets products for all product types   */
    public static void testGetProducts() throws Exception
    {
        try{

            if ( productTypes == null ) {
                return;
            }

            initPOA();


            TestCallback callbackConsumer = getCallback();
            ClassStatusConsumerDelegate classListener = new ClassStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object classObject = getPOA().servant_to_reference(classListener);
            CMIClassStatusConsumer theClassConsumer = CMIClassStatusConsumerHelper.narrow(classObject);

            ProductStatusConsumerDelegate productListener = new ProductStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object productObject = getPOA().servant_to_reference(productListener);
            CMIProductStatusConsumer theProductConsumer = CMIProductStatusConsumerHelper.narrow(productObject);

            System.out.println("-> Sessions : " + sessions.length);
            for (int i = 0; i < sessions.length; i++ )
            {
                System.out.println("-> Session : " + sessions[i].sessionName);
                productTypes = session.getTradingSession().getProductTypesForSession(sessions[i].sessionName);
                for (int j=0; j < productTypes.length; j++) {
                    System.out.println("-->> Product type : " + productTypes[j].description + " :: " + productTypes[j].type);

                    classes = session.getTradingSession().getClassesForSession(sessions[i].sessionName, productTypes[j].type, theClassConsumer);

                    System.out.println("-->>-->> Classes: " + classes.length);

                    for (int k = 0; k < classes.length; k++)
                    {
                        System.out.println("-->>-->> Class : " + classes[k].classStruct.classSymbol + " :: " + classes[k].classStruct.classKey);
                        products = session.getTradingSession().getProductsForSession(sessions[i].sessionName, classes[k].classStruct.classKey, theProductConsumer);

                        for (int l = 0; l < products.length; l++)
                        {
                            System.out.println("-->>-->>-->> Product : " + products[l].productStruct.productName.productSymbol + " :: " + products[l].productStruct.productKeys.productKey);
                        }
                    }
                }
             }

            System.out.println("Testing getSessionClassDetails for session " + sessions[0].sessionName);
//            SessionClassDetailStruct[] details = session.getTradingSession().getClassDetailForTradingSession(sessions[0].sessionName);


//             classes = session.getTradingSession().getProductClassesForSession(sessions[0].sessionName, ProductTypes.OPTION, theClassConsumer);
//             System.out.println("Got " + classes.length + " classes for " + sessions[0].sessionName);
//             for (int i=0; i < classes.length; i++) {
//                 products = session.getTradingSession().getProductsForSession(sessions[0].sessionName, classes[i].classStruct.classKey, theProductConsumer);
//                 System.out.println("Got " + products.length + " products for " + sessions[0].sessionName + " and classKey " + classes[i].classStruct.classKey);
//             }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    private static void testAcceptOrder( int sequenceNumber, int quantity, double price, char side )
    {
        try {
            ExchangeFirmStruct firm = StructBuilder.buildExchangeFirmStruct("CBOE", "690");
            OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",sequenceNumber,"FRM","20031211");
            lastOrderIdStruct = inputOrderId;
            OrderEntryStruct order = UnitTestHelper.createOrderEntryStruct(
                "ONE_MAIN",
                100270087,
                userId,
                inputOrderId);
            order.side = side;
            order.price = PriceFactory.create( price ).toStruct();
            order.originalQuantity = quantity;
            order.contingency.price = PriceFactory.create( 0 ).toStruct();

            System.out.println("Testing Accept Order");
            System.out.println("-= Order.originalQuantity = " + order.originalQuantity);
            System.out.println("-= Order.side = " + order.side + " (Buy = " + Sides.BUY + "; Sell = " + Sides.SELL + ")");
            System.out.println("-= Order.price.type = " + order.price.type);
            System.out.println("-= Order.price.whole = " + order.price.whole);
            System.out.println("-= Order.price.fraction = " + order.price.fraction);
            System.out.println("-= Order.contingency.type = " + order.contingency.type);
            System.out.println("-= Order.orderOrginType = " + order.orderOriginType +
                    " ( Broker = " + OrderOrigins.BROKER_DEALER + ";Customer = " + OrderOrigins.CUSTOMER +
                    ";Customer Broker = " + OrderOrigins.CUSTOMER_BROKER_DEALER +
                    ";Firm = " + OrderOrigins.FIRM + ";Market Maker = " + OrderOrigins.MARKET_MAKER + ")");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            session.getOrderEntry().acceptOrder(order);
            System.out.println("Order accepted");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception adding order : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testAcceptInternalizationOrder()
    {
        try
        {
            ExchangeFirmStruct firm = StructBuilder.buildExchangeFirmStruct("CBOE", "690");
            int seq = (int)(System.currentTimeMillis() % (24 * 60 * 60 * 1000))/10000;
            //seq = seq < 0 ? seq * -1 : seq;
            OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct(firm, "BRA", seq, "FRM", "20050125");
            OrderEntryStruct order = UnitTestHelper.createOrderEntryStruct(
                "W_MAIN",
                163192390,
                userId,
                inputOrderId);

            order.side = Sides.BUY;
            //order.price = PriceFactory.create("10.00"  ).toStruct();
            order.price = PriceFactory.create("1.35"  ).toStruct();
            order.originalQuantity = 150;

            firm = StructBuilder.buildExchangeFirmStruct("CBOE", "690");
            seq += 1;
            inputOrderId = UnitTestHelper.createOrderIdStruct(firm, "BRA", seq, "FRM", "20050125");
            OrderEntryStruct match = UnitTestHelper.createOrderEntryStruct(
                "W_MAIN",
                163192390,
                userId,
                inputOrderId);

            match.side = Sides.SELL;
            match.price = PriceFactory.create("1.35"  ).toStruct();
            match.originalQuantity = 150;

            System.out.println("Testing Accept Internalization Order");
            System.out.println("-= PrimaryOrder.originalQuantity = " + order.originalQuantity);
            System.out.println("-= PrimaryOrder.side = " + order.side + " (Buy = " + Sides.BUY + "; Sell = " + Sides.SELL + ")");
            System.out.println("-= PrimaryOrder.price.type = " + order.price.type);
            System.out.println("-= PrimaryOrder.price.whole = " + order.price.whole);
            System.out.println("-= PrimaryOrder.price.fraction = " + order.price.fraction);
            System.out.println("-= PrimaryOrder.contingency.type = " + order.contingency.type);
            System.out.println("-= PrimaryOrder.orderOrginType = " + order.orderOriginType +
                    " ( Broker = " + OrderOrigins.BROKER_DEALER + ";Customer = " + OrderOrigins.CUSTOMER +
                    ";Customer Broker = " + OrderOrigins.CUSTOMER_BROKER_DEALER +
                    ";Firm = " + OrderOrigins.FIRM + ";Market Maker = " + OrderOrigins.MARKET_MAKER + ")");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            
            ReflectiveStructBuilder.printStruct(order, "primary");
            ReflectiveStructBuilder.printStruct(match, "match");
            
            InternalizationOrderResultStruct result = sessionV3.getOrderEntryV3().acceptInternalizationOrder(order, match, MatchTypes.LIMIT_PRICE);
            ReflectiveStructBuilder.printStruct(result, "result");
            System.out.println("Internalization Order accepted");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception adding order : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private static void testSubscribeOrders() {
        try {
            System.out.println("Testing subscribe orders");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

            OrderStatusConsumerDelegate delegate= new OrderStatusConsumerDelegate(getCallback());
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            CMIOrderStatusConsumer theOrderConsumer = CMIOrderStatusConsumerHelper.narrow (corbaObject);

            OrderStatusV2ConsumerDelegate delegateV2 = new OrderStatusV2ConsumerDelegate(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegateV2);
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer orderConsumerV2 =
                com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerHelper.narrow(corbaObject);

            sessionV2.getOrderQueryV2().subscribeOrders(theOrderConsumer, true);
//            sessionV2.getOrderQueryV2().subscribeOrderStatusV2(orderConsumerV2, true, true);

            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testSubscribeAuction()
    {
        try
        {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing subscribe auction...");
            com.cboe.delegates.callback.AuctionConsumerDelegate delegate = new AuctionConsumerDelegate(new TestAuctionCallback(sessionV3));
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegate);
            CMIAuctionConsumer theAuctionConsumer = CMIAuctionConsumerHelper.narrow(corbaObject);
            short[] auctionTypes = {AuctionTypes.AUCTION_UNSPECIFIED, AuctionTypes.AUCTION_INTERNALIZATION};
            AuctionSubscriptionResultStruct[] result = sessionV3.getOrderQueryV3().subscribeAuctionForClass("W_MAIN", 917647, auctionTypes, theAuctionConsumer);
            if(result[1].auctionType == AuctionTypes.AUCTION_INTERNALIZATION &&
                    result[1].subscriptionResult.errorCode == 0)
            {
                System.out.println("Subscribed for auction with AuctionTypes.AUCTION_INTERNALIZATION. --> GOOD!");
            }
            else
            {
                throw ExceptionBuilder.dataValidationException(result[0].subscriptionResult.errorMessage,  result[0].subscriptionResult.errorCode);
            }
                
            if(result[0].auctionType == AuctionTypes.AUCTION_UNSPECIFIED &&
                    result[0].subscriptionResult.errorCode == 0)
            {
                System.out.println("Subscribed for auction with AuctionTypes.AUCTION_UNSPECIFIED --> BAD!");
            }
            else
            {
                System.out.println("Not Subscribed for auction with AuctionTypes.AUCTION_UNSPECIFIED --> GOOD!");
            }

            result = sessionV3.getOrderQueryV3().unsubscribeAuctionForClass("W_MAIN", 917647, auctionTypes, theAuctionConsumer);

            if(result[1].auctionType == AuctionTypes.AUCTION_INTERNALIZATION &&
                    result[1].subscriptionResult.errorCode == 0)
            {
                System.out.println("Unubscribed for auction with AuctionTypes.AUCTION_INTERNALIZATION. --> GOOD!");
            }
            else
            {
                throw ExceptionBuilder.dataValidationException(result[0].subscriptionResult.errorMessage,  result[0].subscriptionResult.errorCode);
            }
                
            if(result[0].auctionType == AuctionTypes.AUCTION_UNSPECIFIED &&
                    result[0].subscriptionResult.errorCode == 0)
            {
                System.out.println("Unsubscribed for auction with AuctionTypes.AUCTION_UNSPECIFIED --> BAD!");
            }
            else
            {
                System.out.println("Not Unsubscribed for auction with AuctionTypes.AUCTION_UNSPECIFIED --> GOOD!");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private static void testSubscribeOrdersNonGMD() {
        try {
            System.out.println("Testing subscribe orders ");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

//            OrderStatusConsumerDelegate delegate;
            org.omg.CORBA.Object corbaObject;

//            delegate= new OrderStatusConsumerDelegate(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
//            CMIOrderStatusConsumer v1consumer1 = CMIOrderStatusConsumerHelper.narrow (corbaObject);

//            delegate= new OrderStatusConsumerDelegate(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
//            CMIOrderStatusConsumer v1consumer2 = CMIOrderStatusConsumerHelper.narrow (corbaObject);

            OrderStatusV2ConsumerDelegate delegateV2 = new OrderStatusV2ConsumerDelegate(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegateV2);
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer1 =
                com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerHelper.narrow(corbaObject);

            delegateV2 = new OrderStatusV2ConsumerDelegate(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegateV2);
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer2 =
                com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerHelper.narrow(corbaObject);

//            testSubscribeOrdersGMD_scenario1(v2consumer1, v2consumer2);
//            testSubscribeOrdersGMD_scenario2(v2consumer1, v2consumer2);
            testSubscribeOrders_1(v2consumer1, v2consumer2);

            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeQuotesNonGMD() {
        try {
            System.out.println("Testing subscribe quotes ");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

//            QuoteStatusConsumerDelegate delegate;
            org.omg.CORBA.Object corbaObject;

//            delegate= new QuoteStatusConsumerDelegate(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
//            CMIQuoteStatusConsumer v1consumer1 = CMIQuoteStatusConsumerHelper.narrow (corbaObject);

//            delegate= new QuoteStatusConsumerDelegate(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
//            CMIQuoteStatusConsumer v1consumer2 = CMIQuoteStatusConsumerHelper.narrow (corbaObject);

            QuoteStatusV2ConsumerDelegate delegateV2 = new QuoteStatusV2ConsumerDelegate(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegateV2);
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer v2consumer1 =
                com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerHelper.narrow(corbaObject);

            delegateV2 = new QuoteStatusV2ConsumerDelegate(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegateV2);
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer v2consumer2 =
                com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerHelper.narrow(corbaObject);

//            testSubscribeQuotesGMD_scenario1(v2consumer1, v2consumer2);
//            testSubscribeQuotesGMD_scenario2(v2consumer1, v2consumer2);
            testSubscribeQuotes_1(v2consumer1, v2consumer2);

            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeOrdersGMD()
    {
        OrderStatusV2ConsumerDelegate delegateV2 = new OrderStatusV2ConsumerDelegate(getCallback());
        org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegateV2);
        com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer1 =
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerHelper.narrow(corbaObject);
        subscGmdOrderV2(FOR_USER, v2consumer1,      0,     GMD);// okay
    }
    /**
     *
     */
    private static void testSubscribeOrdersGMD_scenario1(
        com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer1,
        com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer2)
    {
        subscGmdOrderV2(FOR_USER, v2consumer1,      0,     GMD);// okay
        subscGmdOrderV2(FOR_USER, v2consumer1,      0, NOT_GMD);// FAIL
        subscGmdOrderV2(FOR_USER, v2consumer1,      0,     GMD);// okay

/**/    unsubGmdOrderV2(FOR_USER, v2consumer1,      0);         // okay
        unsubGmdOrderV2(FOR_USER, v2consumer1,      0);         // okay

        subscGmdOrderV2(FOR_USER, v2consumer1,      0, NOT_GMD);// okay
        subscGmdOrderV2(FOR_USER, v2consumer1,      0, NOT_GMD);// okay

//        subscGmdOrderV2(FOR_USER, v2consumer1, CLASS1,     GMD);// okay
//        subscGmdOrderV2(FOR_USER, v2consumer1, CLASS2,     GMD);// okay
//        subscGmdOrderV2(FOR_USER, v2consumer1,      0,     GMD);// fail
//        subscGmdOrderV2(FOR_USER, v2consumer1, CLASS1,     GMD);// okay
//        subscGmdOrderV2(FOR_USER, v2consumer2, CLASS1,     GMD);// fail
//
///**/    unsubGmdOrderV2(FOR_USER, v2consumer1, CLASS1);         // okay
//        unsubGmdOrderV2(FOR_USER, v2consumer1, CLASS1);         // okay
//
//        subscGmdOrderV2(FOR_USER, v2consumer2, CLASS1,     GMD);// okay

///**/    unsubGmdOrderV2(FOR_USER, CLASS2, v2consumer1);         // okay
//        unsubGmdOrderV2(FOR_USER, CLASS1, v2consumer2);         // okay
    }

    /**
     *
     */
    private static void testSubscribeOrdersGMD_scenario2(
        com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer1,
        com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer2)
    {
        subscGmdOrderV2(FOR_FIRM, v2consumer1,      0,     GMD);// okay
        subscGmdOrderV2(FOR_USER, v2consumer1,      0,     GMD);// okay
        subscGmdOrderV2(FOR_FIRM, v2consumer1,      0, NOT_GMD);// okay
        subscGmdOrderV2(FOR_FIRM, v2consumer1, CLASS1,     GMD);// fail
        subscGmdOrderV2(FOR_FIRM, v2consumer2, CLASS1,     GMD);// fail

/**/    unsubGmdOrderV2(FOR_FIRM, v2consumer1, CLASS1);         // okay

        subscGmdOrderV2(FOR_FIRM, v2consumer2, CLASS1,     GMD);// fail

/**/    unsubGmdOrderV2(FOR_FIRM, v2consumer1,      0);         // okay

        subscGmdOrderV2(FOR_FIRM, v2consumer2, CLASS1,     GMD);// okay
        subscGmdOrderV2(FOR_FIRM, v2consumer1, CLASS1,     GMD);// fail

/**/    unsubGmdOrderV2(FOR_USER, v2consumer1,      0);         // okay

        subscGmdOrderV2(FOR_FIRM, v2consumer1, CLASS1,     GMD);// fail
    }

    private static void testSubscribeOrders_1(com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer1, 
                                                         com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer v2consumer2)
    {
        System.out.println("WARNING.  This process will subscribe for orders for user, " +
                           "wait for 5 minutes, and then unsubscribe (for CASMON testing -- to allow " + 
                           "observance of instrumentation data.  Then, it will repreat the same" +
                           "process for firm.");
        
        try
        {
            subscGmdOrderV2(FOR_USER, v2consumer1, CLASS1, NOT_GMD);
            Thread.sleep(300000);
            
            
            System.out.println("Unsubscribing now...");
            
            unsubGmdOrderV2(FOR_USER, v2consumer1, CLASS1);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

//        try
//        {
//            subscGmdOrderV2(FOR_FIRM, v2consumer2, CLASS1, NOT_GMD);
//            Thread.sleep(300000);
//            unsubGmdOrderV2(FOR_FIRM, v2consumer2, CLASS1);
//        }
//        catch(InterruptedException e)
//        {
//            e.printStackTrace();
//        }

    }
    
    private static void testSubscribeQuotes_1(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer v2consumer1, 
                                                         com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer v2consumer2)
    {
        System.out.println("WARNING.  This process will subscribe for quotes for user, " +
                           "wait for 5 minutes, and then unsubscribe (for CASMON testing -- to allow " + 
                           "observance of instrumentation data.  Then, it will repreat the same" +
                           "process for firm.");
        
        try
        {
            subscGmdQuoteV2(v2consumer1, FOR_USER, CLASS1, NOT_GMD);
            Thread.sleep(300000);
            unsubGmdQuoteV2(v2consumer1, FOR_USER, CLASS1);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

//        try
//        {
//            subscGmdQuoteV2(v2consumer2, FOR_FIRM, CLASS1, NOT_GMD);
//            Thread.sleep(300000);
//            unsubGmdQuoteV2(v2consumer2, FOR_FIRM, CLASS1);
//        }
//        catch(InterruptedException e)
//        {
//            e.printStackTrace();
//        }

    }
    /**
     *
     */
    private static void subscGmdOrderV1(
        com.cboe.idl.cmiCallback.CMIOrderStatusConsumer consumer,
        boolean forFirm,
        boolean gmd)
    {
        boolean okay = true;

        try
        {
            if (forFirm)
            {
                sessionV2.getOrderQueryV2().subscribeOrdersByFirmWithoutPublish(consumer, gmd);
            }
            else
            {
                sessionV2.getOrderQueryV2().subscribeOrdersWithoutPublish(consumer, gmd);
            }
        }
        catch (Exception e)
        {
            okay = false;
        }

        System.out.print("  subOrder");
        if (forFirm)
            System.out.print("ForFirm");
        System.out.print("V1(" + consumer.hashCode());
        System.out.print(", gmd:" + gmd);
        System.out.print(") ");
        System.out.println(okay ? "OK" : "Failed");
    }

    /**
     *
     */
    private static void subscGmdOrderV2(
        boolean forFirm,
        com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer consumer,
        int classKey,
        boolean gmd)
    {

        System.out.print("  subOrder");
        if (forFirm)
            System.out.print("ForFirm");
        if (classKey > 0)
            System.out.print("ForClass");
        System.out.print("V2(" + consumer.hashCode());
        if (classKey > 0)
            System.out.print(", " + Integer.toString(classKey));
        System.out.print(", gmd:" + gmd);
        System.out.print(") ");

        try
        {
            if (forFirm)
            {
                if (classKey > 0)
                {
                    sessionV2.getOrderQueryV2().subscribeOrderStatusForFirmForClassV2(
                        classKey, consumer, false, gmd);
                }
                else
                {
                    sessionV2.getOrderQueryV2().subscribeOrderStatusForFirmV2(
                        consumer, false, gmd);
                }
            }
            else
            {
                if (classKey > 0)
                {
                    sessionV2.getOrderQueryV2().subscribeOrderStatusForClassV2(
                        classKey, consumer, false, gmd);
                }
                else
                {
                    com.cboe.idl.cmiV3.OrderQuery orderQueryV3 = sessionV3.getOrderQueryV3();
                    orderQueryV3.subscribeOrderStatusV2(consumer, false, gmd);
                }
            }

            System.out.println("OK");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Failed -- " + e.getMessage());
        }
    }

    /**
     *
     */
    private static void unsubGmdOrderV2(
        boolean forFirm,
        com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer consumer,
        int classKey)
    {
        System.out.print("unsubOrder");
        if (forFirm)
            System.out.print("ForFirm");
        if (classKey > 0)
            System.out.print("ForClass");
        System.out.print("V2(" + consumer.hashCode());
        if (classKey > 0)
            System.out.print(", " + Integer.toString(classKey));
        System.out.print(") ");

        try
        {
            if (forFirm)
            {
                if (classKey > 0)
                {
                    sessionV2.getOrderQueryV2().unsubscribeOrderStatusForFirmForClassV2(
                        classKey, consumer);
                }
                else
                {
                    sessionV2.getOrderQueryV2().unsubscribeOrderStatusForFirmV2(
                        consumer);
                }
            }
            else
            {
                if (classKey > 0)
                {
                    sessionV2.getOrderQueryV2().unsubscribeOrderStatusForClassV2(
                        classKey, consumer);
                }
                else
                {
                    sessionV2.getOrderQueryV2().unsubscribeOrderStatusV2(
                        consumer);
                }
            }

            System.out.println("OK");
        }
        catch (Exception e)
        {
            System.out.println("Failed -- " + e.getMessage());
        }
    }

    /**
     *
     */
    private static void testSubscribeQuotesGMD() {
        try {
            System.out.println("Testing subscribe quotes GMD");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

            QuoteStatusConsumerDelegate delegate;
            org.omg.CORBA.Object corbaObject;

//            delegate= new QuoteStatusConsumerDelegate(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
//            CMIQuoteStatusConsumer v1consumer1 = CMIQuoteStatusConsumerHelper.narrow (corbaObject);

//            delegate= new QuoteStatusConsumerDelegate(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
//            CMIQuoteStatusConsumer v1consumer2 = CMIQuoteStatusConsumerHelper.narrow (corbaObject);

            QuoteStatusV2ConsumerDelegate delegateV2 = new QuoteStatusV2ConsumerDelegate(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegateV2);
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer v2consumer1 =
                com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerHelper.narrow(corbaObject);

            delegateV2 = new QuoteStatusV2ConsumerDelegate(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object(delegateV2);
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer v2consumer2 =
                com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerHelper.narrow(corbaObject);

            subscGmdQuoteV2(v2consumer1, FOR_USER,      0,     GMD); // okay
            subscGmdQuoteV2(v2consumer1, FOR_USER,      0,     GMD); // okay
            subscGmdQuoteV2(v2consumer2, FOR_USER,      0,     GMD); // fail

            unsubGmdQuoteV2(v2consumer1, FOR_USER,      0);
            unsubGmdQuoteV2(v2consumer1, FOR_USER,      0);

            subscGmdQuoteV2(v2consumer1, FOR_USER, CLASS1,     GMD); // okay
            subscGmdQuoteV2(v2consumer1, FOR_USER, CLASS2,     GMD); // okay
            subscGmdQuoteV2(v2consumer1, FOR_USER,      0,     GMD); // fail
            subscGmdQuoteV2(v2consumer1, FOR_USER, CLASS1,     GMD); // okay
            subscGmdQuoteV2(v2consumer2, FOR_USER, CLASS1,     GMD); // fail

            unsubGmdQuoteV2(v2consumer1, FOR_USER, CLASS1);

            subscGmdQuoteV2(v2consumer2, FOR_USER, CLASS1,     GMD); // okay

            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private static void subscGmdQuoteV1(
        com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer consumer,
        boolean forFirm,
        boolean gmd)
    {
        boolean okay = true;

        try
        {
            if (forFirm)
            {
                sessionV2.getQuoteV2().subscribeQuoteStatusForFirmWithoutPublish(consumer, gmd);
            }
            else
            {
                sessionV2.getQuoteV2().subscribeQuoteStatusWithoutPublish(consumer, gmd);
            }
        }
        catch (Exception e)
        {
            okay = false;
        }

        System.out.print("  subQuote");
        if (forFirm)
            System.out.print("ForFirm");
        System.out.print("V1(" + consumer.hashCode());
        System.out.print(", gmd:" + gmd);
        System.out.print(") ");
        System.out.println(okay ? "OK" : "Failed");
    }

    /**
     *
     */
    private static void subscGmdQuoteV2(
        com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer consumer,
        boolean forFirm,
        int classKey,
        boolean gmd)
    {
        boolean okay = true;

        try
        {
            if (forFirm)
            {
                if (classKey > 0)
                {
                    sessionV2.getQuoteV2().subscribeQuoteStatusForFirmForClassV2(
                        classKey, consumer, gmd);
                }
                else
                {
                    sessionV2.getQuoteV2().subscribeQuoteStatusForFirmV2(
                        consumer, gmd);
                }
            }
            else
            {
                if (classKey > 0)
                {
                    sessionV2.getQuoteV2().subscribeQuoteStatusForClassV2(
                        classKey, true, true, consumer, gmd);
                }
                else
                {
                    sessionV2.getQuoteV2().subscribeQuoteStatusV2(
                        consumer, true, true, gmd);
                }
            }
        }
        catch (Exception e)
        {
            okay = false;
        }

        System.out.print("  subQuote");
        if (forFirm)
            System.out.print("ForFirm");
        if (classKey > 0)
            System.out.print("ForClass");
        System.out.print("V2(" + consumer.hashCode());
        if (classKey > 0)
            System.out.print(", " + Integer.toString(classKey));
        System.out.print(", gmd:" + gmd);
        System.out.print(") ");
        System.out.println(okay ? "OK" : "Failed");
    }

    /**
     *
     */
    private static void unsubGmdQuoteV2(
        com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer consumer,
        boolean forFirm,
        int classKey)
    {
        boolean okay = true;

        try
        {
            if (forFirm)
            {
                if (classKey > 0)
                {
                    sessionV2.getQuoteV2().unsubscribeQuoteStatusForFirmForClassV2(
                        classKey, consumer);
                }
                else
                {
                    sessionV2.getQuoteV2().unsubscribeQuoteStatusForFirmV2(
                        consumer);
                }
            }
            else
            {
                if (classKey > 0)
                {
                    sessionV2.getQuoteV2().unsubscribeQuoteStatusForClassV2(
                        classKey, consumer);
                }
                else
                {
                    sessionV2.getQuoteV2().unsubscribeQuoteStatusV2(
                        consumer);
                }
            }
        }
        catch (Exception e)
        {
            okay = false;
        }

        System.out.print("unsubQuote");
        if (forFirm)
            System.out.print("ForFirm");
        if (classKey > 0)
            System.out.print("ForClass");
        System.out.print("V2(" + consumer.hashCode());
        if (classKey > 0)
            System.out.print(", " + Integer.toString(classKey));
        System.out.print(") ");
        System.out.println(okay ? "OK" : "Failed");
    }

    /**
     *
     */
    private static void testSubscribeMarketData() {
       try {
           System.out.println("Testing subscrbe Market Data");
           System.out.println("-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-");
           short action = QueueActions.OVERLAY_LAST;
           org.omg.CORBA.Object corbaObject;
           System.out.println("-=-=-=-=-=-=-=-=TESTING V3 CURRENTMARKETCONSUMER-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
           //com.cboe.idl.cmiCallbackV2.POA_CMICurrentMarketConsumer_tie delegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMICurrentMarketConsumer_tie(getCallback());
           com.cboe.idl.cmiCallbackV3.POA_CMICurrentMarketConsumer_tie delegateV3 = new com.cboe.idl.cmiCallbackV3.POA_CMICurrentMarketConsumer_tie(getCallback());
           corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateV3);
           com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer currentMarketV3Consumer = com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.narrow (corbaObject);
           //for simulator --sessionV3.getMarketQueryV3().subscribeCurrentMarketForClassV3("W_MAIN", 917647, currentMarketV3Consumer,action);
           //sessionV3.getMarketQueryV3().subscribeCurrentMarketForProductV3("W_MAIN",918805, currentMarketV3Consumer,action);

           //for real cas, use this.
           sessionV3.getMarketQueryV3().subscribeCurrentMarketForClassV3("W_MAIN", 196705, currentMarketV3Consumer,action);
           //product real cass--sessionV3.getMarketQueryV3().subscribeCurrentMarketForProductV3("W_MAIN",105317597, currentMarketV3Consumer,action);

            //sessionV3.getMarketQueryV3().subscribeCurrentMarketForProductV3("W_MAIN",918805, currentMarketV3Consumer,action);
           
           //sessionV3.getMarketQueryV3().subscribeCurrentMarketForClassV3("W_STOCK", 2359538, currentMarketV3Consumer,action);

/***          

         System.out.println("-=-=-=-=-=-=-=-=TESTING V1 CURRENTMARKETCONSUMER-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
         com.cboe.idl.cmiCallback.POA_CMICurrentMarketConsumer_tie delegate= new com.cboe.idl.cmiCallback.POA_CMICurrentMarketConsumer_tie(getCallback());
         corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
         CMICurrentMarketConsumer currentMarketConsumer = CMICurrentMarketConsumerHelper.narrow (corbaObject);
         session.getMarketQuery().subscribeCurrentMarketForClass("W_MAIN", 917647, currentMarketConsumer);
         //sessionV3.getMarketQueryV3().subscribeCurrentMarketForClassV3("W_MAIN", 918805, currentMarketV3Consumer,action);
         session.getMarketQuery().subscribeCurrentMarketForProduct("W_MAIN", 2359538, currentMarketConsumer);

//            sessionV2.getOrderQueryV2().subscribeOrders(theOrderConsumer, true);
            System.out.println("-=-=-=-=-TESTING V2 CURRENTMARKETCONSUMER=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            com.cboe.idl.cmiCallbackV2.POA_CMICurrentMarketConsumer_tie delegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMICurrentMarketConsumer_tie(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateV2);
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketV2Consumer = com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow (corbaObject);
            sessionV2.getMarketQueryV2().subscribeCurrentMarketForClassV2("W_MAIN", 917647, currentMarketV2Consumer, action);
           sessionV2.getMarketQueryV2().subscribeCurrentMarketForProductV2("W_MAIN", 918805, currentMarketV2Consumer, action);
****/           

//           com.cboe.idl.cmiCallback.POA_CMIRecapConsumer_tie recapDelegate= new com.cboe.idl.cmiCallback.POA_CMIRecapConsumer_tie(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (recapDelegate);
//            CMIRecapConsumer recapConsumer = CMIRecapConsumerHelper.narrow (corbaObject);
//            sessionV2.getMarketQueryV2().subscribeRecapForClass("W_AM1", 917647, recapConsumer);
//            sessionV2.getOrderQueryV2().subscribeOrders(theOrderConsumer, true);
//            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            com.cboe.idl.cmiCallbackV2.POA_CMIRecapConsumer_tie recapDelegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMIRecapConsumer_tie(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (recapDelegateV2);
//            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapV2Consumer = com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.narrow (corbaObject);
//            sessionV2.getMarketQueryV2().subscribeRecapForClassV2("W_AM1", 917647, recapV2Consumer, action);
//            sessionV2.getMarketQueryV2().subscribeRecapForProductV2("W_AM1", 918805, recapV2Consumer, action);

//           com.cboe.idl.cmiCallback.POA_CMINBBOConsumer_tie NBBODelegate= new com.cboe.idl.cmiCallback.POA_CMINBBOConsumer_tie(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (NBBODelegate);
//            CMINBBOConsumer NBBOConsumer = CMINBBOConsumerHelper.narrow (corbaObject);
//            sessionV2.getMarketQueryV2().subscribeNBBOForClass("W_AM1", 917647, NBBOConsumer);
//            sessionV2.getOrderQueryV2().subscribeOrders(theOrderConsumer, true);
//            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            com.cboe.idl.cmiCallbackV2.POA_CMINBBOConsumer_tie NBBODelegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMINBBOConsumer_tie(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (NBBODelegateV2);
//            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer NBBOV2Consumer = com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.narrow (corbaObject);
//            sessionV2.getMarketQueryV2().subscribeNBBOForClassV2("W_AM1", 917647, NBBOV2Consumer, action);
//           sessionV2.getMarketQueryV2().subscribeNBBOForProductV2("W_AM1", 918805, NBBOV2Consumer, action);
//
//            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            com.cboe.idl.cmiCallbackV2.POA_CMITickerConsumer_tie tickerDelegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMITickerConsumer_tie(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (tickerDelegateV2);
//            com.cboe.idl.cmiCallbackV2.CMITickerConsumer tickerV2Consumer = com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.narrow (corbaObject);
//            sessionV2.getMarketQueryV2().subscribeTickerForClassV2("W_AM1", 917647, tickerV2Consumer, action);
//           sessionV2.getMarketQueryV2().subscribeTickerForProductV2("W_AM1", 918805, tickerV2Consumer, action);

//           com.cboe.idl.cmiCallback.POA_CMIOrderBookConsumer_tie BookDepthDelegate= new com.cboe.idl.cmiCallback.POA_CMIOrderBookConsumer_tie(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (BookDepthDelegate);
//            CMIOrderBookConsumer BookDepthConsumer = CMIOrderBookConsumerHelper.narrow (corbaObject);
//            sessionV2.getMarketQueryV2().subscribeBookDepthForClass("W_AM1", 917647, BookDepthConsumer);
//            sessionV2.getOrderQueryV2().subscribeOrders(theOrderConsumer, true);
//            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            com.cboe.idl.cmiCallbackV2.POA_CMIOrderBookConsumer_tie BookDepthDelegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMIOrderBookConsumer_tie(getCallback());
//            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (BookDepthDelegateV2);
//            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer BookDepthV2Consumer = com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper.narrow (corbaObject);
//            sessionV2.getMarketQueryV2().subscribeBookDepthForClassV2("W_AM1", 917647, BookDepthV2Consumer, action);
//           sessionV2.getMarketQueryV2().subscribeBookDepthForProductV2("W_AM1", 918805, BookDepthV2Consumer, action);
//
//           System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//           com.cboe.idl.cmiCallbackV2.POA_CMIExpectedOpeningPriceConsumer_tie eopDelegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMIExpectedOpeningPriceConsumer_tie(getCallback());
//           corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (eopDelegateV2);
//           com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer eopV2Consumer = com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.narrow (corbaObject);
//           sessionV2.getMarketQueryV2().subscribeExpectedOpeningPriceForClassV2("W_AM1", 917647, eopV2Consumer, action);
//           sessionV2.getMarketQueryV2().subscribeExpectedOpeningPriceForProductV2("W_AM1", 918805, eopV2Consumer, action);
           Thread.currentThread().sleep(36000);
           System.out.println("wait up and unsubscribe.....");
//           sessionV2.getMarketQueryV2().unsubscribeCurrentMarketForProductV2("W_AM1", 918805, currentMarketV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeRecapForProductV2("W_AM1", 918805, recapV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeNBBOForProductV2("W_AM1", 918805, NBBOV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeTickerForProductV2("W_AM1", 918805, tickerV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeBookDepthForProductV2("W_AM1", 918805, BookDepthV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeExpectedOpeningPriceForProductV2("W_AM1", 918805, eopV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeCurrentMarketForClassV2("W_AM1", 917647, currentMarketV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeRecapForClassV2("W_AM1", 917647, recapV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeNBBOForClassV2("W_AM1", 917647, NBBOV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeTickerForClassV2("W_AM1", 917647, tickerV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeBookDepthForClassV2("W_AM1", 917647, BookDepthV2Consumer);
//           sessionV2.getMarketQueryV2().unsubscribeExpectedOpeningPriceForClassV2("W_AM1", 917647, eopV2Consumer);
             //sessionV3.getMarketQueryV3().unsubscribeCurrentMarketForClassV3("W_MAIN", 917647, currentMarketV3Consumer);
             //sessionV3. getMarketQueryV3().unsubscribeCurrentMarketForProductV3("W_MAIN",918805, currentMarketV3Consumer);
             
            sessionV3.getMarketQueryV3().unsubscribeCurrentMarketForClassV3("W_MAIN", 196705, currentMarketV3Consumer);
           //product real cass--sessionV3.getMarketQueryV3().unsubscribeCurrentMarketForProductV3("W_MAIN",105317597, currentMarketV3Consumer);


            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

       } catch (Exception e)
       {

       }
    }

    private static void testSubscribeRFQ()
    {
        RFQConsumerDelegate delegateRFQ= new RFQConsumerDelegate(getCallback());
        org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateRFQ);
        CMIRFQConsumer rfqConsumer = CMIRFQConsumerHelper.narrow (corbaObject);
        try{
        sessionV2.getQuoteV2().subscribeRFQ("ONE_MAIN", 8520441, rfqConsumer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private static void testSubscribeQuotes() {
        try {
            System.out.println("Testing subscribe quotes");
            short action = 0;
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            CMIQuoteStatusConsumer theQuoteConsumer = (CMIQuoteStatusConsumer) new QuoteStatusConsumerDelegate(getCallback());
//            connection.register_object(theQuoteConsumer);
//            TestCallback myCall = getCallback();
//            LockedQuoteStatusV2ConsumerDelegate myDelegate = new LockedQuoteStatusV2ConsumerDelegate(myCall);
 //           org.omg.CORBA.Object myObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (myDelegate);
//            com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer theLockConsumer = com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumerHelper.narrow(myObject);

            QuoteStatusConsumerDelegate delegate= new QuoteStatusConsumerDelegate(getCallback());
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            CMIQuoteStatusConsumer theQuoteConsumer = CMIQuoteStatusConsumerHelper.narrow (corbaObject);

            RFQConsumerDelegate delegateRFQ= new RFQConsumerDelegate(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateRFQ);
            CMIRFQConsumer rfqConsumer = CMIRFQConsumerHelper.narrow (corbaObject);


            com.cboe.idl.cmiCallbackV2.POA_CMIQuoteStatusConsumer_tie delegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMIQuoteStatusConsumer_tie(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateV2);
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer quoteStatusV2Consumer = com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerHelper.narrow (corbaObject);

            com.cboe.idl.cmiCallbackV2.POA_CMIRFQConsumer_tie delegateRFQV2= new com.cboe.idl.cmiCallbackV2.POA_CMIRFQConsumer_tie(getCallback());
            corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateRFQV2);
            com.cboe.idl.cmiCallbackV2.CMIRFQConsumer rfqV2Consumer = com.cboe.idl.cmiCallbackV2.CMIRFQConsumerHelper.narrow (corbaObject);

//            sessionV2.getQuoteV2().subscribeQuoteStatusV2(quoteStatusV2Consumer, true, true, true);

//            QuoteEntryStruct quote = UnitTestHelper.CreateNewQuoteEntryStruct("W_AM1", 919003, 100, 100);
//            quote.bidPrice = PriceFactory.create( 2.0 ).toStruct();
//            quote.askPrice = PriceFactory.create( 3.0 ).toStruct();
//            quote.askQuantity = quote.bidQuantity = 10;
//            sessionV2.getQuoteV2().acceptQuote(quote);

            // OEX class 917658
//            sessionV2.getQuoteV2().subscribeQuoteStatusForClassV2(917658, true, true, quoteStatusV2Consumer, true);
//            quote.askQuantity = quote.bidQuantity = 30;
            //sessionV2.getQuoteV2().acceptQuote(quote);

//	    try {
//                sessionV2.getQuoteV2().subscribeQuoteLockedNotification(true, theLockConsumer, true);
//	    }
//	   catch (java.lang.NullPointerException e)  {
//		System.out.println ("CALL fails");
//		e.printStackTrace();
//	   }
            sessionV2.getQuoteV2().subscribeQuoteStatus(theQuoteConsumer, true);
            sessionV2.getQuoteV2().subscribeQuoteStatusV2(quoteStatusV2Consumer, true, true, true);
            sessionV2.getQuoteV2().subscribeQuoteStatusForClassV2(917647, true, true, quoteStatusV2Consumer, true);
            sessionV2.getQuoteV2().subscribeQuoteStatusForFirmV2(quoteStatusV2Consumer, true);
            sessionV2.getQuoteV2().subscribeQuoteStatusForFirmForClassV2(917607,quoteStatusV2Consumer, true );
            sessionV2.getQuoteV2().subscribeRFQV2("W_AM1", 917647, rfqV2Consumer);
            sessionV2.getQuoteV2().subscribeRFQ("W_AM1", 917647, rfqConsumer);



            // put break to wait for events
//            System.out.println("Waiting for quote events ....");
//            Thread.currentThread().sleep(5000);
//            System.out.println("Start unsubscribing ....");
//
//            sessionV2.getQuoteV2().unsubscribeRFQV2("W_AM1", 917647, rfqV2Consumer);
//            sessionV2.getQuoteV2().unsubscribeRFQ("W_AM1", 917647, rfqConsumer);
//            sessionV2.getQuoteV2().unsubscribeQuoteStatusV2(quoteStatusV2Consumer);
//            sessionV2.getQuoteV2().unsubscribeQuoteStatusForClassV2(917607,quoteStatusV2Consumer);
//            sessionV2.getQuoteV2().unsubscribeQuoteStatusForFirmV2(quoteStatusV2Consumer);
//            sessionV2.getQuoteV2().unsubscribeQuoteStatusForFirmForClassV2(917607,quoteStatusV2Consumer );

            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testAcceptQuoteForClass()
    {
        testAcceptQuoteForClass( 1, 9.0, 10.0 );
    }

    private static void testAcceptQuoteForClass( int quantity, double bid, double ask )
    {
        /* class OEX classkey 917658
         * product key 919005 919004
        */
        try {
            QuoteEntryStruct quote = UnitTestHelper.CreateNewQuoteEntryStruct("W_AM1", 919004, 100, 100);
            System.out.println("Testing Accept Quote for Class OEX ");
            quote.bidPrice = PriceFactory.create( bid ).toStruct();
            quote.askPrice = PriceFactory.create( ask ).toStruct();
            quote.askQuantity = quote.bidQuantity = quantity;
            System.out.println("-= Quote for product = " + 919004);
            System.out.println("-= Quote.askPrice.type = " + quote.askPrice.type);
            System.out.println("-= Quote.askPrice.whole = " + quote.askPrice.whole);
            System.out.println("-= Quote.askPrice.fraction = " + quote.askPrice.fraction);
            System.out.println("-= Quote.bidPrice.type = " + quote.bidPrice.type);
            System.out.println("-= Quote.bidPrice.whole = " + quote.bidPrice.whole);
            System.out.println("-= Quote.bidPrice.fraction = " + quote.bidPrice.fraction);
            System.out.println("-= Quote.askQuantity = " + quote.askQuantity);
            System.out.println("-= Quote.bidQuantity = " + quote.bidQuantity);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

            QuoteEntryStruct quote2 = UnitTestHelper.CreateNewQuoteEntryStruct("W_AM1", 919005, 100, 100);
            System.out.println("Testing Accept Quote");
            quote.bidPrice = PriceFactory.create( bid ).toStruct();
            quote.askPrice = PriceFactory.create( ask ).toStruct();
            quote.askQuantity = quote.bidQuantity = quantity;
            System.out.println("-= Quote for product = " + 919005);
            System.out.println("-= Quote.askPrice.type = " + quote.askPrice.type);
            System.out.println("-= Quote.askPrice.whole = " + quote.askPrice.whole);
            System.out.println("-= Quote.askPrice.fraction = " + quote.askPrice.fraction);
            System.out.println("-= Quote.bidPrice.type = " + quote.bidPrice.type);
            System.out.println("-= Quote.bidPrice.whole = " + quote.bidPrice.whole);
            System.out.println("-= Quote.bidPrice.fraction = " + quote.bidPrice.fraction);
            System.out.println("-= Quote.askQuantity = " + quote.askQuantity);
            System.out.println("-= Quote.bidQuantity = " + quote.bidQuantity);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

            QuoteEntryStruct[] quotes = new QuoteEntryStruct[2];
            quotes[0] = quote;
            quotes[1] = quote2;

            sessionV2.getQuoteV2().acceptQuotesForClass(917658, quotes);

            System.out.println("Quote accepted");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception adding quote : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testAcceptQuotesForClass( int quantity, double bid, double ask )
    {
        /* class OEX classkey 917658
         * product key 919005 919004
        */
        try {
            QuoteEntryStruct quote = UnitTestHelper.CreateNewQuoteEntryStruct("W_AM1", 919004, 100, 100);
            quote.bidPrice = PriceFactory.create( bid ).toStruct();
            quote.askPrice = PriceFactory.create( ask ).toStruct();
            quote.askQuantity = quote.bidQuantity = quantity;
            System.out.println("-= Quote for product = " + 919004);
            System.out.println("-= Quote.askPrice.type = " + quote.askPrice.type);
            System.out.println("-= Quote.askPrice.whole = " + quote.askPrice.whole);
            System.out.println("-= Quote.askPrice.fraction = " + quote.askPrice.fraction);
            System.out.println("-= Quote.bidPrice.type = " + quote.bidPrice.type);
            System.out.println("-= Quote.bidPrice.whole = " + quote.bidPrice.whole);
            System.out.println("-= Quote.bidPrice.fraction = " + quote.bidPrice.fraction);
            System.out.println("-= Quote.askQuantity = " + quote.askQuantity);
            System.out.println("-= Quote.bidQuantity = " + quote.bidQuantity);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

            QuoteEntryStruct quote2 = UnitTestHelper.CreateNewQuoteEntryStruct("W_AM1", 919005, 100, 100);
            quote.bidPrice = PriceFactory.create( bid ).toStruct();
            quote.askPrice = PriceFactory.create( ask ).toStruct();
            quote.askQuantity = quote.bidQuantity = quantity;
            System.out.println("-= Quote for product = " + 919005);
            System.out.println("-= Quote.askPrice.type = " + quote.askPrice.type);
            System.out.println("-= Quote.askPrice.whole = " + quote.askPrice.whole);
            System.out.println("-= Quote.askPrice.fraction = " + quote.askPrice.fraction);
            System.out.println("-= Quote.bidPrice.type = " + quote.bidPrice.type);
            System.out.println("-= Quote.bidPrice.whole = " + quote.bidPrice.whole);
            System.out.println("-= Quote.bidPrice.fraction = " + quote.bidPrice.fraction);
            System.out.println("-= Quote.askQuantity = " + quote.askQuantity);
            System.out.println("-= Quote.bidQuantity = " + quote.bidQuantity);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

            QuoteEntryStruct[] quotes = new QuoteEntryStruct[2];
            quotes[0] = quote;
            quotes[1] = quote2;

            sessionV2.getQuoteV2().acceptQuotesForClass(917658, quotes);

            // Pause for a few seconds before cancelling these quotes...
            pause(5);

            session.getQuote().cancelQuotesByClass("W_AM1", 917658);

            System.out.println("Quote accepted");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception adding quote : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void pause(int numSeconds)
    {
        try
        {
            Thread.sleep(numSeconds * 1000);
        }
        catch (InterruptedException ie)
        {
            // Don't care...
        }
    }

    private static void testAcceptQuote()
    {
        testAcceptQuote( 1, 9.0, 10.0 );
    }


    private static void testAcceptQuote( int quantity, double bid, double ask )
    {
        try {
            QuoteEntryStruct quote = UnitTestHelper.CreateNewQuoteEntryStruct(sessions[0].sessionName, products[0].productStruct.productKeys.productKey, 100, 100);
            System.out.println("Testing Accept Quote");
            quote.bidPrice = PriceFactory.create( bid ).toStruct();
            quote.askPrice = PriceFactory.create( ask ).toStruct();
            quote.askQuantity = quote.bidQuantity = quantity;
            System.out.println("-= Quote for product = " + products[0].productStruct.productKeys.productKey);
            System.out.println("-= Quote.askPrice.type = " + quote.askPrice.type);
            System.out.println("-= Quote.askPrice.whole = " + quote.askPrice.whole);
            System.out.println("-= Quote.askPrice.fraction = " + quote.askPrice.fraction);
            System.out.println("-= Quote.bidPrice.type = " + quote.bidPrice.type);
            System.out.println("-= Quote.bidPrice.whole = " + quote.bidPrice.whole);
            System.out.println("-= Quote.bidPrice.fraction = " + quote.bidPrice.fraction);
            System.out.println("-= Quote.askQuantity = " + quote.askQuantity);
            System.out.println("-= Quote.bidQuantity = " + quote.bidQuantity);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            session.getQuote().acceptQuote(quote);
            System.out.println("Quote accepted");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception adding quote : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testGetQuote() {
        try {
            System.out.println("Testing Get Quote");
            session.getQuote().getQuote(sessions[0].sessionName, products[0].productStruct.productKeys.productKey);
            System.out.println("Quote fetched");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception getting quote : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        try
        {
            userId = "EFJ";
            ////////// MUST BE CALLED /////////
            initORBConnection(args);

 //           initImUserSession();

             initUserSession();
//            testGetImSession();
//            testGetSessions();

//            testGetProductTypes();  // must called first
//            testGetProducts();      // must called second

//            testSubscribeRFQ();

//            testSubscribeOrders();
            
 //            testSubscribeQuotes();
            // CASMON testing
            //testSubscribeOrdersNonGMD();
            //testSubscribeQuotesNonGMD();
            
            
            testSubscribeOrdersGMD();

            testAcceptInternalizationOrder();
//            testSubscribeAuction();
                        
//            testSubscribeMarketData();

            //orderId = getRandomOrderId();
            //testAcceptOrder(orderId, 50, 16.0, Sides.BUY );

//            testAcceptQuote();

//            testAcceptQuotesForClass(10, 21, 22);

//
//            testAcceptOrder(orderId, 50, 17.0, Sides.BUY );


            try {
                java.lang.Object waiter = new java.lang.Object();
                synchronized(waiter)
                {
                    waiter.wait();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
         } catch (Throwable e)
         {
            e.printStackTrace();
         }
    }

}
