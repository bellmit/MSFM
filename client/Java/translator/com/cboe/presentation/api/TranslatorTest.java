package com.cboe.presentation.api;

/**
 * This class is the CAS remote client simulator.
 * @author Jeff Illian
 */
import java.util.*;

import com.cboe.exceptions.*;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiTraderActivity.*;
import com.cboe.idl.cmiConstants.QueryDirections;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiConstants.OrderOrigins;
import com.cboe.idl.cmiConstants.OrderCancelTypes;

import com.cboe.application.shared.*;

import com.cboe.util.event.*;

import com.cboe.domain.util.*;
import com.cboe.domain.util.OrderStructBuilder;

import com.cboe.interfaces.presentation.api.*;
import com.cboe.interfaces.presentation.product.*;
import com.cboe.interfaces.presentation.marketData.*;
import com.cboe.interfaces.presentation.bookDepth.BookDepth;


public class TranslatorTest {
    private static RemoteConnection connection;
    private static UserSessionManager session;
    private static ProductType[] productTypes;
    private static SessionProductClass[] classes;
    private static SessionProduct [] products;
    private static TradingSessionStruct[] sessions;
    private static TradingSessionStruct currentSession;
    private static final String USER_ID="CCC_CBOE";
    private static final String PASSWORD ="CCC_CBOE";
    private static String userId;
    private static String password;
    private static String firmKey;
    private static TestCASCallback casCallback;
    private static MarketMakerAPI mmAPI;
    private static EventChannelAdapter eventChannel;

    private static OrderIdStruct lastOrderIdStruct = null;

    private static void initConnection(String[] args)
    {
        if ( connection == null )
        {
            connection = RemoteConnectionFactory.create(args);
            System.out.println("Connection established " + connection);
        }
    }

    private static String getSystemProperty(String propertyName)
    {
        Properties props = System.getProperties();

        String theProperty = props.getProperty(propertyName);

        return theProperty;
    }

    private static void initUserSession()
    {
        try {
            userId = getSystemProperty("USER");
            if ( userId == null )
            {
                userId = USER_ID;
            }
            password = getSystemProperty("PASSWORD");
            if ( password == null )
            {
                password = PASSWORD;
            }
            firmKey = getSystemProperty("FIRMKEY");

            if ( mmAPI == null )
            {
                UserLogonStruct logonStruct = new UserLogonStruct(userId, password, Version.CMI_VERSION, LoginSessionModes.STAND_ALONE_TEST);
                casCallback = new TestCASCallback();
                System.out.println("Logging onto CAS");
                mmAPI = UserAccessFactory.marketMakerLogon(logonStruct, LoginSessionTypes.PRIMARY, casCallback);
                System.out.println("-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-");
                System.out.println("User Information");
                System.out.println("-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-");
                System.out.println("-= Fullname = " + mmAPI.getValidUser().getFullName());
                System.out.println("-= Firm = " + mmAPI.getValidUser().getFirm());
                System.out.println("-= Role = " + mmAPI.getValidUser().getRole().getName());
                System.out.println("-= Userid = " + mmAPI.getValidUser().getUserId());
                System.out.println("-= Username = " + mmAPI.getValidUser().getFullName());
                System.out.println("-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void testGetProductTypes()
    {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Getting product types:");

        try {
            productTypes = mmAPI.getProductTypesForSession(currentSession.sessionName);
            for (int i = 0; i < productTypes.length; i++) {
                System.out.println("\t - " + productTypes[i].getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        };
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    /** gets sessions  */
    public static void testGetSessions()
    {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Getting sessions:");

        try {
            sessions = mmAPI.getCurrentTradingSessions(casCallback);
            System.out.println("\t Got " + sessions.length + " sessions");
            for (int i = 0; i < sessions.length; i++) {
                System.out.println("\t Session - " + i + " : Name = " + sessions[i].sessionName);
                currentSession = sessions[i];
            }
        } catch (Exception e) {
            e.printStackTrace();
        };

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    /** gets products for all product types   */
    private static void testGetProducts() {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing Get Products");

        try {
            System.out.println("\t...calling getProductClassesForSession( session=" + currentSession.sessionName + " , productType=" + ProductTypes.OPTION );
            SessionProductClass[] classes = mmAPI.getProductClassesForSession(currentSession.sessionName, ProductTypes.OPTION, casCallback);
            System.out.println("\t...got " + classes.length + " classes");

            for (int i = 0; i < classes.length; i++) {
                System.out.println("\t - Class " + classes[i].getClassSymbol() + " " + classes[i].getClassKey());
//                ReflectiveStructTester.printStruct(classStructs[i],"ClassStruct");
            }


            // Try to get classes for IBM
            ProductClass productClass = mmAPI.getClassBySymbol(ProductTypes.OPTION, "IBM");

            // Now get products for IBM
            System.out.println("\t...calling getProductsForSession( session=" + currentSession.sessionName + " , classKey=" + productClass.getClassKey());
            products = mmAPI.getProductsForSession(currentSession.sessionName, productClass.getClassKey(), casCallback);
            System.out.println("\t...got " + products.length + " products for session=" + currentSession.sessionName + ", ClassKey=" + productClass.getClassKey() );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }


    /**
     * Gets product Strategies
     *
     * Pre-conditions: The class variable products[] must have at least one element.
     */
    private static void testGetProductStrategies() {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing Get Product Strategies");

        SessionStrategy[] containers = null;

        try
        {
             containers = mmAPI.getStrategiesByComponent(currentSession.sessionName, products[0].getProductKey());
        }
        catch(Exception e)
        {
             e.printStackTrace();
        }

        if ( containers == null )
        {
             System.out.println("No Product Strategies found");

        }
        else
        {
             System.out.println("Got " + containers.length + " strategies back for component leg " + products[0].getProductKey());
             for (int i = 0; i < containers.length; i++)
             {
                 System.out.println(" -- strategy = " + containers[i].getProductKey());
             }
        }

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    private static void testSessionlessProduct()
    {
        try {
            ProductClass[] classes;
            Product[] products;
            ProductType[] types = mmAPI.getAllProductTypes();
            for (int i = 0; i < types.length; i++)
            {
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                System.out.println("Classes for type " + types[i].getDescription() + " : " + types[i].getType());
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                classes = mmAPI.getAllClassesForType(types[i].getType());
                for (int j = 0; j < classes.length; j++)
                {
                    System.out.println("-=- Products for class " + classes[j].getClassSymbol() + " : " + classes[j].getClassKey());
                    System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                    products = mmAPI.getAllProductsForClass(classes[j].getClassKey(), false);
                    for (int k = 0; k < products.length; k++)
                    {
                        System.out.println("-=-=- Product  " + products[k].getDescription() + " : " + products[k].getProductKey());
                    }
                    System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                }
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    private static void testAcceptOrder(int sequenceNumber)
    {
        testAcceptOrder( sequenceNumber, 1, 11 );
    }

    private static void testAcceptOrder( int sequenceNumber, int quantity, double price )
    {
        testAcceptOrder( sequenceNumber, quantity, price, Sides.BUY );
    }


    /**
     * Accepts an order
     *
     * Pre-conditions: The class variable products[] must have at least one element.
     */
    private static void testAcceptOrder( int sequenceNumber, int quantity, double price, char side )
    {
            OrderIdStruct returnOrder = null;

//            OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct("690","BRA",sequenceNumber,"FRM","19991130");
            ExchangeFirmStruct firm = new ExchangeFirmStruct("CBOE","690");
            OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",sequenceNumber,"FRM","20002209");
            lastOrderIdStruct = inputOrderId;
            OrderEntryStruct order = UnitTestHelper.createOrderEntryStruct(currentSession.sessionName, products[0].getProductKey(), userId, inputOrderId);
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

        try
        {
             returnOrder = mmAPI.acceptOrder(order);
        }
        catch (DataValidationException e)
        {
              System.out.println("Exception adding order : " + e.details.message + " " + e.details.error);
        }
        catch (Exception e)
        {
              e.printStackTrace();
        }

        if ( returnOrder == null )
        {
            System.out.println("Order not accepted");
        }
        else
        {
            System.out.println("Order accepted");
            System.out.println("OrderIdStruct.date = " + returnOrder.orderDate);
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

    }

    private static void testCancelOrder(int sequenceNumber) {
        try {
            ExchangeFirmStruct firm = new ExchangeFirmStruct("CBOE","690");
            OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",sequenceNumber,"FRM","20002209");
            lastOrderIdStruct = inputOrderId;

            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing Cancel Order");
            CancelRequestStruct cancelRequest = new CancelRequestStruct(inputOrderId, currentSession.sessionName, userId + ":CANCEL", OrderCancelTypes.DESIRED_CANCEL_QUANTITY, 1000 );
            mmAPI.acceptOrderCancelRequest(cancelRequest);
            System.out.println("Order canceled");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception canceling order : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testCancelReplaceOrder(int sequenceNumber) {
        try {
            ExchangeFirmStruct firm = new ExchangeFirmStruct("CBOE","690");

            OrderIdStruct oldOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",sequenceNumber,"FRM","20002209");
//            OrderIdStruct newOrderId = UnitTestHelper.createOrderIdStruct("A","BRA",new Long(System.currentTimeMillis()).intValue(),"FRM","19991130");
            OrderIdStruct newOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",getRandomOrderId(),"FRM","20002209");
            OrderEntryStruct newOrder = UnitTestHelper.createOrderEntryStruct(currentSession.sessionName, products[0].getProductKey(),userId,newOrderId);
            newOrder.side = Sides.BUY;
            newOrder.price.whole = 11;
            newOrder.price.fraction = 0;
            newOrder.contingency.price.fraction = 0;

            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing acceptOrderCancelReplaceRequest");
            int remainingQuantity = OrderStructBuilder.getRemainingQuantity(mmAPI.getOrderById(oldOrderId).orderStruct);
            mmAPI.acceptOrderCancelReplaceRequest(oldOrderId, remainingQuantity, newOrder);
            System.out.println("Order CancelReplaced");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception acceptOrderCancelReplaceRequest: " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testAcceptQuote()
    {
        testAcceptQuote( 1, 9.0, 10.0 );
    }

    /**
     * Accepts quotes
     *
     * Pre-conditions: The class variable products[] must have at least one element.
     */
    private static void testAcceptQuote( int quantity, double bid, double ask )
    {
        try {
            QuoteEntryStruct quote = UnitTestHelper.CreateNewQuoteEntryStruct(currentSession.sessionName,products[0].getProductKey(), 100, 100);
            System.out.println("Testing Accept Quote");
            quote.bidPrice = PriceFactory.create( bid ).toStruct();
            quote.askPrice = PriceFactory.create( ask ).toStruct();
            quote.askQuantity = quote.bidQuantity = quantity;
            System.out.println("-= Quote for product = " + products[0].getProductKey());
            System.out.println("-= Quote.askPrice.type = " + quote.askPrice.type);
            System.out.println("-= Quote.askPrice.whole = " + quote.askPrice.whole);
            System.out.println("-= Quote.askPrice.fraction = " + quote.askPrice.fraction);
            System.out.println("-= Quote.bidPrice.type = " + quote.bidPrice.type);
            System.out.println("-= Quote.bidPrice.whole = " + quote.bidPrice.whole);
            System.out.println("-= Quote.bidPrice.fraction = " + quote.bidPrice.fraction);
            System.out.println("-= Quote.askQuantity = " + quote.askQuantity);
            System.out.println("-= Quote.bidQuantity = " + quote.bidQuantity);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            mmAPI.acceptQuote(quote);
            System.out.println("Quote accepted");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception adding quote : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a quote
     *
     * Pre-conditions: The class variable products[] must have at least one element.
     */
    private static void testGetQuote() {
        try {
            System.out.println("Testing Get Quote");
            mmAPI.getQuote(currentSession.sessionName, products[0].getProductKey());
            System.out.println("Quote fetched");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception getting quote : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels a quote
     *
     * Pre-conditions: The class variable products[] must have at least one element.
     */
    private static void testCancelQuote() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing Cancel Quote");
            mmAPI.cancelQuote(currentSession.sessionName, products[0].getProductKey());
            System.out.println("Quote accepted");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception adding quote : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeOrders(String firm)
    {
        try
        {
//            if ( firm == null )
//            {
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                System.out.println("Testing subscribe orders");
                mmAPI.getAllOrders(casCallback);
                mmAPI.subscribeOrders(casCallback);
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            }
//            else
//            {
//                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//                System.out.println("Testing subscribe orders for firm::" + firm);
//                mmAPI.getAllOrders(casCallback);
//                mmAPI.subscribeOrdersByFirm(casCallback);
//                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void testSubscribeQuotes(String firm)
    {
        try
        {
//            if ( firm == null )
//            {
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                System.out.println("Testing subscribe quotes");
                mmAPI.subscribeQuoteBustReportV2(casCallback);
                mmAPI.subscribeQuoteFilledReportV2(casCallback);
                mmAPI.subscribeQuoteDeletedReportV2(casCallback);
                mmAPI.getAllQuotes(casCallback);
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            }
//            else
//            {
//                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//                System.out.println("Testing subscribe quotes for firm::" + firm);
//                mmAPI.subscribeQuoteFilledReportForFirm(casCallback);
//                mmAPI.subscribeQuoteBustReportForFirm(casCallback);
//                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void testSubscribeCurrentMarket() {
        try {
            System.out.println("Testing subscribe current market for class : " + products[0].getProductKeysStruct().classKey);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            mmAPI.subscribeCurrentMarketForClass(currentSession.sessionName, products[0].getProductKeysStruct().classKey, casCallback);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeNBBO() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing subscribe NBBO for class : " + products[0].getProductKeysStruct().classKey);
            mmAPI.subscribeNBBOForClass(currentSession.sessionName, products[0].getProductKeysStruct().classKey, casCallback);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeUserMarketData() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing subscribe user market data");
            //DMG REMOVE
            if ( products[0] == null )
            {
                System.out.println("Null products[0]");
            }

            System.out.println("-= session=" + currentSession.sessionName + " classKey=" + products[0].getProductKeysStruct().classKey);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            UserMarketDataStruct[] marketData = mmAPI.getUserMarketData(currentSession.sessionName, products[0].getProductKeysStruct().classKey, casCallback);
            System.out.println("-=- Market Data returned for " + marketData.length + " products");
            for (int i = 0; i < marketData.length; i++) {
                System.out.println("-= Market Data =- session:" + marketData[i].currentMarket.sessionName +
                                                      " classKey:" + marketData[i].productKeys.classKey +
                                                      " productKey:" + marketData[i].productKeys.productKey +
                                                      " current ask:" + !PriceFactory.create(marketData[i].currentMarket.askPrice).isNoPrice()  +
                                                      " current bid:" + !PriceFactory.create(marketData[i].currentMarket.bidPrice).isNoPrice() +
                                                      " last ask:" + !PriceFactory.create(marketData[i].recap.askPrice).isNoPrice() +
                                                      " last bid:" + !PriceFactory.create(marketData[i].recap.bidPrice).isNoPrice()
                                    );
            }
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeUserMarketDataByProduct() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing subscribe user market data by product");

            for ( int i = 0; i < products.length; i++ )
            {
                System.out.println("   -=- subscribing by product::" + products[i].getProductKey());
                UserMarketDataStruct marketData = mmAPI.getUserMarketDataByProduct(currentSession.sessionName, products[i].getProductKey(), casCallback);
                System.out.println("   -=- Market Data returned" );
                System.out.println("   -=- " + marketData );
                System.out.println("   -=- done." );
            }
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeRFQ() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing subscribe RFQ");
            ProductType[] productTypes = mmAPI.getProductTypesForSession("W_AM1");
            for ( int j = 0; j < productTypes.length; j++ ) {
              SessionProductClass[] classes = mmAPI.getProductClassesForSession("W_AM1", productTypes[j].getType(), casCallback);
              for (int i = 0; i < classes.length; i++) {
                System.out.println("\t Subscribing for RFQ for " + classes[i].getClassStruct().classSymbol + " : " + classes[i].getClassStruct().classKey);
                mmAPI.subscribeRFQForClass(currentSession.sessionName, classes[i].getClassStruct().classKey, casCallback);
            }
            }
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeUnderlyingRecap() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing subscribe underlying recap");

            //int classKey = 393934; //products[0].getProductKeysStruct().classKey;
            int classKey = products[0].getProductKeysStruct().classKey;

            ProductClass classStruct = mmAPI.getProductClassByKey(classKey);

            System.out.println("Got classStruct for classKey = " + classKey + " = " + classStruct.getClassSymbol());

            int productKey = classStruct.getUnderlyingProduct().getProductKey();

            System.out.println("Got underlying for classKey = " + classStruct.getClassKey() + " = " + classStruct.getUnderlyingProduct().getProductNameStruct().productSymbol );
            System.out.println("Testing subscribe underlying recap for class " + classStruct.getClassKey() + " underlying product : " + productKey + " in underlying class = " + classStruct.getUnderlyingProduct().getProductKeysStruct().classKey);

            mmAPI.subscribeRecapForProduct("W_U1", productKey, casCallback);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeUnderlyingTicker() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing subscribe underlying ticker");

            int classKey = products[0].getProductKeysStruct().classKey;
            ProductClass classStruct = mmAPI.getProductClassByKey(classKey);

            int productKey = classStruct.getUnderlyingProduct().getProductKey();
            System.out.println("Testing subscribe underlying ticker for class " + classStruct.getClassKey() + " underlying product : " + productKey);

            mmAPI.subscribeTicker(currentSession.sessionName, productKey, casCallback);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSubscribeRecap() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing subscribe recap");

            System.out.println("Testing subscribe recap for class : " + products[0].getProductKeysStruct().classKey + " and session " + currentSession.sessionName);
            mmAPI.subscribeRecapForClass(currentSession.sessionName, products[0].getProductKeysStruct().classKey, casCallback);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
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

    private static void testGetOrderBook() {
        try {
            System.out.println("Testing Get Book Depth for Product " + products[0].getProductKey());
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            BookDepth bookDepth = mmAPI.getCmiBookDepth(products[0]);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("-= Buy Side -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            for (int i = 0; i < bookDepth.getBuySide().length; i++) {
                System.out.println("Price : " + bookDepth.getBuySide()[i].getPrice().getWhole() +
                     " : " + bookDepth.getBuySide()[i].getPrice().getFraction() + " -- Indicators : " +
                     ((bookDepth.isAllPricesIncluded()) ? "true" : "false") +
                     bookDepth.getTransactionSequenceNumber());
            }
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("-= Sell Side =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            for (int i = 0; i < bookDepth.getSellSide().length; i++) {
                System.out.println("Price : " + bookDepth.getSellSide()[i].getPrice().getWhole() +
                     " : " + bookDepth.getSellSide()[i].getPrice().getFraction() + " -- Indicators : " +
                     ((bookDepth.isAllPricesIncluded()) ? "true" : "false") +
                     bookDepth.getTransactionSequenceNumber());
            }
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (DataValidationException e) {
            System.out.println("Exception adding quote : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testException() {
        try {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing DataValidationException Marshalling");
            session.getProductQuery().getProductNameStruct(-32000);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSendMessage()
    {
        String subject = "This is a test sendMessage() subject field.";
        String text = "This is a test sendMessage() text field.";

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing Send Message");

        MessageStruct messageStruct = new MessageStruct( UnitTestHelper.createCurrentDateTimeStruct(), 0, 0, userId, subject, false, text );
        try
        {
            mmAPI.sendMessage(messageStruct);
            System.out.println("Sent:: " + subject );
            System.out.println("From:: " + userId );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }


    private static void testGetAllUserPreferences()
    {
        PreferenceStruct[]      userPreferences = null;

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing getAllUserPreferences");

        try
        {
            userPreferences = mmAPI.getAllUserPreferences();

            //DMG 20001215
            if ( userPreferences == null) {
                 System.out.println("RemoteTestClient.testGetAllUserPreferences() - userPreferences are null");
                 System.exit(1);
            }

            System.out.println( "\t user preference length=" + userPreferences.length );
            for( int i = 0; i < userPreferences.length; i++ )
            {
                System.out.println( "\t user preference::" + i + "  name=" + userPreferences[ i ].name );
            }

            UserMarketDataStruct userMarketData = mmAPI.getUserMarketDataByProduct(currentSession.sessionName, products[0].getProductKey(), null);


            //DMG 20001215
            if ( userMarketData == null) {
                 System.out.println("RemoteTestClient.testGetAllUserPreferences() - userMarketData are null");
                 System.exit(1);
            }
            System.out.println( "user market data::" + userMarketData.productKeys.productKey + ":" + userMarketData.productKeys.classKey );

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    private static void testQueryOrderHistory()
    {
        ActivityHistoryStruct  activityHistory;

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing QueryOrderHistory");

        try
        {
            activityHistory = mmAPI.queryOrderHistory( lastOrderIdStruct );

            for ( int i = 0; i < activityHistory.activityRecords.length; i++ )
            {
                for ( int j = 0; j < activityHistory.activityRecords[i].activityFields.length; j++)
                {
                    System.out.println("activityHistory.activityRecords[i].activityFields.length = " + activityHistory.activityRecords[i].activityFields.length); //REMOVE
                    System.out.println( "activityHistory.activityRecord[" + i + "].activityField[" + j + "]::" +
                                        " name::"  + activityHistory.activityRecords[i].activityFields[j].fieldName +
                                        " value::" + activityHistory.activityRecords[i].activityFields[j].fieldValue );
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    private static void testGetTraderClassActivityByTime(int classKey, DateTimeStruct startTime, short direction)
    {
        ActivityHistoryStruct  activityHistory;

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing GetTraderClassActivityByTime");

        try
        {
            activityHistory = mmAPI.getTraderClassActivityByTime(currentSession.sessionName, classKey, startTime, direction );

            for ( int i = 0; i < activityHistory.activityRecords.length; i++ )
            {
                for ( int j = 0; j < activityHistory.activityRecords[i].activityFields.length; j++)
                {
                    System.out.println( "activityHistory.activityRecord[" + i + "].activityField[" + j + "]::" +
                                        " name::"  + activityHistory.activityRecords[i].activityFields[j].fieldName +
                                        " value::" + activityHistory.activityRecords[i].activityFields[j].fieldValue );
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    private static void testGetTraderProductActivityByTime(int productKey, DateTimeStruct startTime, short direction)
    {
        ActivityHistoryStruct  activityHistory;

        System.out.println("Testing GetTraderProductActivityByTime");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        try
        {
            activityHistory = mmAPI.getTraderProductActivityByTime(currentSession.sessionName, productKey, startTime, direction );

            for ( int i = 0; i < activityHistory.activityRecords.length; i++ )
            {
                for ( int j = 0; j < activityHistory.activityRecords[i].activityFields.length; j++)
                {
                    System.out.println( "activityHistory.activityRecord[" + i + "].activityField[" + j + "]::" +
                                        " name::"  + activityHistory.activityRecords[i].activityFields[j].fieldName +
                                        " value::" + activityHistory.activityRecords[i].activityFields[j].fieldValue );
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    private static void testGetClassByKeyForSession(int classKey) {

        SessionProductClass sessionProductClass = null;

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing GetClassByKeyForSession( classKey=" + classKey + ')');

        try
        {
            sessionProductClass = mmAPI.getClassByKeyForSession("W_AM1", classKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ( sessionProductClass == null )
        {
            System.out.println("No session product class found");
        }
        else
        {
            System.out.println("Session Product Class retrieved:" );
            SessionClassStruct classStruct = sessionProductClass.getSessionClassStruct();
            System.out.println("\t session Name:"+classStruct.sessionName);
            System.out.println("\t underlying session name:"+classStruct.underlyingSessionName);
            System.out.println("\t class state:"+classStruct.classState);
            System.out.println("\t sequence Number:"+classStruct.classStateTransactionSequenceNumber);
            System.out.println("\t class symbol:"+classStruct.classStruct.classSymbol);
            System.out.println("\t Primary Exchange:"+classStruct.classStruct.primaryExchange);
            System.out.println("\t eligible sessions:"+classStruct.eligibleSessions.toString());
         }

         System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

    }

    private static void testGetClassBySymbolForSession(short productType, String className) {
        SessionProductClass sessionProductClass = null;

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing GetClassBySymbolForSession( productType=" + productType + " ,className=" + className + ')');

        try
        {
            sessionProductClass = mmAPI.getClassBySymbolForSession("W_AM1", productType, className);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if  ( sessionProductClass == null )
        {
            System.out.println("No Session Product Class found" );
        }
        else
        {
            System.out.println("The SessionProductClass found was:" );
            SessionClassStruct classStruct = sessionProductClass.getSessionClassStruct();
            System.out.println("\t session Name:"+classStruct.sessionName);
            System.out.println("\t underlying session name:"+classStruct.underlyingSessionName);
            System.out.println("\t class state:"+classStruct.classState);
            System.out.println("\t sequence Number:"+classStruct.classStateTransactionSequenceNumber);
            System.out.println("\t class struct:"+classStruct.classStruct.toString());
            System.out.println("\t eligible sessions:"+classStruct.eligibleSessions.toString());
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

    }

    private static void testGetProductByKeyForSession(int productKey) {

        SessionProduct sessionProduct = null;

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing GetProductByKeyForSession( productKey=" + productKey + ')');

        try
        {
            sessionProduct = mmAPI.getProductByKeyForSession("W_AM1",productKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ( sessionProduct == null )
        {
            System.out.println("No Session Product found");
        }
        else
        {
            System.out.println("The Session Product found was:");
            SessionProductStruct productStruct = sessionProduct.getSessionProductStruct();
            System.out.println("\t product state:"+productStruct.productState);
            System.out.println("\t sequence number:"+productStruct.productStateTransactionSequenceNumber);
            System.out.println("\t product struct:"+productStruct.productStruct.toString());
            System.out.println("\t session name:"+productStruct.sessionName);
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

    }
    private static void testGetProductByNameForSession(ProductNameStruct productName) {

        SessionProduct sessionProduct = null;

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing GetProductByNameForSession");
        System.out.println("\t\t product name::product symbol :" + productName.productSymbol);
        System.out.println("\t\t product name::option type    :" + productName.optionType);
        System.out.println("\t\t product name::exercise price :" + productName.exercisePrice.type + ", " + productName.exercisePrice.whole + ", " + productName.exercisePrice.fraction );
        System.out.println("\t\t product name::expiration date:" + productName.expirationDate.month + '/' + productName.expirationDate.day + '/' + productName.expirationDate.year );


        try
        {
            sessionProduct = mmAPI.getProductByNameForSession("W_AM1",productName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        if ( sessionProduct == null )
        {
            System.out.println("No Product found");
        }
        else
        {
            System.out.println("The Product found was:");

            SessionProductStruct productStruct = sessionProduct.getSessionProductStruct();
            System.out.println("\t product state  :"+productStruct.productState);
            System.out.println("\t sequence number:"+productStruct.productStateTransactionSequenceNumber);
            System.out.println("\t product struct :"+productStruct.productStruct.toString());
            System.out.println("\t session name   :"+productStruct.sessionName);
        }

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }
    private static void testGetStrategyByKeyForSession(int productKey) {

        SessionStrategy sessionStrategy = null;

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Testing GetStrategyByKeyForSession( productKey=" + productKey +')' );

        try
        {
            sessionStrategy = mmAPI.getStrategyByKeyForSession("W_AM1",productKey);

            if ( sessionStrategy == null )
            {
                System.out.println("No Strategy found");
            }
            else
            {
                System.out.println("The Strategy found was:");
                System.out.println("\t session      :"+"W_AM1");
                System.out.println("\t productKey   :"+productKey);
                System.out.println("\t session name :"+sessionStrategy.getTradingSessionName());
                System.out.println("\t product      :"+sessionStrategy.getProductStruct());
                System.out.println("\t strategy legs:"+sessionStrategy.getStrategyLegs().toString());
            }
         } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    public static void main(String[] args)
    {
        boolean     printStructOn = false;
        int         OrderId;

        MockHomeBuilder.initialize();
        //HomeBuilder.initialize();

        initConnection(args);
        System.out.println("Connecting to CAS");
        eventChannel = EventChannelAdapterFactory.find();
        System.out.println("Getting a session");
        initUserSession();

        printStructOn = casCallback.setPrintStructOn( false );

        System.out.flush();

        testSessionlessProduct();

        testGetSessions();

        testGetProductTypes();
        testGetProducts();
        /*
        testGetProductStrategies();

        testGetAllUserPreferences();

        testSubscribeUserMarketData();

        testSubscribeUserMarketDataByProduct();

        testSubscribeCurrentMarket();
        */
        testSubscribeRecap();

        testSubscribeUnderlyingRecap();
        testSubscribeUnderlyingTicker();

        testSubscribeOrders( firmKey );
        testSubscribeQuotes( firmKey );

        try
        {
            mmAPI.subscribeOrderFilledReport(casCallback);
        }
        catch( Exception e )
        {
        }

        OrderId = getRandomOrderId();
        testAcceptOrder(OrderId, 50, 16.0, Sides.BUY );


        OrderId = getRandomOrderId();
        testAcceptOrder(OrderId, 50, 17.0, Sides.BUY );

        OrderId = getRandomOrderId();
        testAcceptOrder(OrderId, 50, 18.0, Sides.BUY );


        // What are valid class and product keys to use for this?


        testGetTraderClassActivityByTime( 10,                                                                 // int classKey
                                          (new DateWrapper(System.currentTimeMillis()).toDateTimeStruct()),   // DateTimeStruct startTime
                                          QueryDirections.QUERY_BACKWARD                                      // short direction
                                        );

        testGetTraderProductActivityByTime( 10,                                                                 // int productKey
                                            (new DateWrapper(System.currentTimeMillis()).toDateTimeStruct()),   // DateTimeStruct startTime
                                            QueryDirections.QUERY_BACKWARD                                      // short direction
                                          );



        OrderId = getRandomOrderId();
        testAcceptOrder(OrderId, 150, 15.0, Sides.SELL );

        testAcceptOrder(getRandomOrderId(), 25, 18.0, Sides.BUY );
        testCancelReplaceOrder( OrderId );
        testCancelOrder( OrderId );
        testSubscribeRFQ();
        short productType = 7;
        testGetClassByKeyForSession(917647); //chose a class in session W_AM1
        testGetClassBySymbolForSession(productType, "IBM");
        testGetProductByKeyForSession(918874); // chose a product in session W_AM1

        short type = 2;
        PriceStruct exercisePrice = new PriceStruct(type,65,0);
        byte month = 7;
        byte day = 21;
        short year = 2011;
        DateStruct expirationDate = new DateStruct(month,day,year);
        ProductNameStruct productName = new ProductNameStruct("IBM",exercisePrice,expirationDate,'P',"");
        testGetProductByNameForSession(productName);

        testGetStrategyByKeyForSession(2818051);

        try
        {
            java.lang.Object waiter = new java.lang.Object();
            synchronized(waiter)
            {
                waiter.wait();
            }
            System.out.println("Calling session logout");
            session.logout();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Calling logout");
        try
        {
             mmAPI.logout();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

    }
}

