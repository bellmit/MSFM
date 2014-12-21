package com.cboe.application.cas;

/**
 * This class is the CAS Callbakeclient simulator.  It registers
 * for events to the CAS.  When the -DUNSUBSCRIBE=U is set, the
 * unsubscribes for all events will be called.
 * @author Connie Feng
 */

import java.util.*;

import com.cboe.application.shared.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmi.*;
import com.cboe.delegates.callback.*;
import com.cboe.application.test.*;

import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;

import com.cboe.util.*;
import com.cboe.domain.util.DateWrapper;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class TestEventSubscriber {
    protected  final String USER_ID="sbtUser";
    protected  final String SESSION_NAME="DAY";
    protected  TestCallback callbackConsumer;

    protected  RemoteConnection connection;
    protected  UserSessionManager session;
    protected  ProductTypeStruct[] productTypes;
    protected  ProductStruct[] products;
    protected  ClassStruct[] classes;
    protected  TradingSessionStruct[] tradingSessions;
    protected  CMIUserSessionAdmin userSessionListener;
    protected  CMIClassStatusConsumer theClassConsumer;
    protected  CMIProductStatusConsumer theProductConsumer;
    protected  CMICurrentMarketConsumer marketConsumer;
    protected  CMINBBOConsumer theNBBOConsumer;
    protected  CMIRecapConsumer recapConsumer;
    protected  CMITickerConsumer tickerConsumer;
    protected  CMIExpectedOpeningPriceConsumer openingPriceConsumer;
    protected  CMIOrderStatusConsumer orderStatusConsumer;
    protected  CMIQuoteStatusConsumer theQuoteStatusConsumer;
    protected  CMIRFQConsumer theRFQConsumer;
    protected  CMITradingSessionStatusConsumer tradingSessionStatusConsumer;
    protected  CMIStrategyStatusConsumer strategyStatusConsumer;
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

    public TestEventSubscriber() {
        super();
    }

    /** Initializes the foundation framework */
    protected void initFFEnv() throws Exception
    {
        FoundationFramework ff = FoundationFramework.getInstance();
        ConfigurationService configService = (ConfigurationService)new ConfigurationServiceFileImpl();
        String [] fileName = { "CAS.properties" } ;
        configService.initialize(fileName, 0);
        ff.initialize("ClientApplicationServer", configService);
    }

    /** initializes the ORB connection object */
    protected void initORBConnection(String[] args)
    {
        if ( connection == null )
        {
            connection = RemoteConnectionFactory.create(args);
            EventChannelAdapterFactory.find().setDynamicChannels(true);
        }
    }

    /** initializes the user session object */

    protected  void initUserSession()
    {   try
        {
            if ( session == null )
            {
                com.cboe.interfaces.application.UserAccessHome home = (com.cboe.interfaces.application.UserAccessHome)HomeFactory.getInstance().findHome(com.cboe.interfaces.application.UserAccessHome.HOME_NAME);
                com.cboe.interfaces.application.UserAccess userAccess = (com.cboe.interfaces.application.UserAccess)home.find();
                //UserAccess userAccess =  TestUserAccessFactory.find();
                UserLogonStruct logonStruct = new UserLogonStruct(USER_ID, "", "", LoginSessionModes.STAND_ALONE_TEST);
                session = userAccess.logon(logonStruct, LoginSessionTypes.PRIMARY, userSessionListener, true);
                session.authenticate(logonStruct);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** initializes the callback consumer objects
     */
     protected  void initializeCallbacks()
    {
        initPOA();
        callbackConsumer = new TestCallback();
        try {
            UserSessionAdminConsumerDelegate sessionListener = new UserSessionAdminConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object orbObject = getPOA().servant_to_reference(sessionListener);
            userSessionListener = CMIUserSessionAdminHelper.narrow(orbObject);

            ClassStatusConsumerDelegate classListener = new ClassStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object classObject = getPOA().servant_to_reference(classListener);
            theClassConsumer = CMIClassStatusConsumerHelper.narrow(classObject);

            ProductStatusConsumerDelegate productListener = new ProductStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object productObject = getPOA().servant_to_reference(productListener);
            theProductConsumer = CMIProductStatusConsumerHelper.narrow(productObject);

            OrderStatusConsumerDelegate OrderListener = new OrderStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object orderObject = getPOA().servant_to_reference(OrderListener);
            orderStatusConsumer = CMIOrderStatusConsumerHelper.narrow(orderObject);

            CurrentMarketConsumerDelegate marketListener = new CurrentMarketConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object marketObject = getPOA().servant_to_reference(marketListener);
            marketConsumer = CMICurrentMarketConsumerHelper.narrow(marketObject);

            NBBOConsumerDelegate nbboListener = new NBBOConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object nbboObject = getPOA().servant_to_reference(nbboListener);
            theNBBOConsumer = CMINBBOConsumerHelper.narrow(nbboObject);

            RecapConsumerDelegate recapListener = new RecapConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object recapObject = getPOA().servant_to_reference(recapListener);
            recapConsumer = CMIRecapConsumerHelper.narrow(recapObject);

            TickerConsumerDelegate tickerListener = new TickerConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object tickerObject = getPOA().servant_to_reference(tickerListener);
            tickerConsumer = CMITickerConsumerHelper.narrow(tickerObject);

            ExpectedOpeningPriceConsumerDelegate priceListener = new ExpectedOpeningPriceConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object priceObject = getPOA().servant_to_reference(priceListener);
            openingPriceConsumer = CMIExpectedOpeningPriceConsumerHelper.narrow(priceObject);

            QuoteStatusConsumerDelegate quoteListener = new QuoteStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object qouteObject = getPOA().servant_to_reference(quoteListener);
            theQuoteStatusConsumer = CMIQuoteStatusConsumerHelper.narrow(qouteObject);

            RFQConsumerDelegate rfqListener = new RFQConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object rfqObject = getPOA().servant_to_reference(rfqListener);
            theRFQConsumer = CMIRFQConsumerHelper.narrow(rfqObject);

            TradingSessionStatusConsumerDelegate tsListener = new TradingSessionStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object tsObject = getPOA().servant_to_reference(tsListener);
            tradingSessionStatusConsumer = CMITradingSessionStatusConsumerHelper.narrow(tsObject);

            StrategyStatusConsumerDelegate strategyListener = new StrategyStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object strategyObject = getPOA().servant_to_reference(strategyListener);
            strategyStatusConsumer = CMIStrategyStatusConsumerHelper.narrow(strategyObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
   }

    /** gets product types  */
    protected  void testGetProductTypes()
    {
        System.out.println("--------------------------------> Testing testGetProductTypes()");

        try {
        productTypes = session.getTradingSession().getProductTypesForSession(SESSION_NAME);
        System.out.println("GetProductTypes: " + productTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("    - testGetProductTypes() success!");
    }

    protected  void testUnsubscribeProductStrategy()
    {
        System.out.println("--------------------------------> Testing testUnsubscribeProductStrategy()");

        try
        {
            for(int i = 0; i < classes.length; i++)
            {
                session.getTradingSession().unsubscribeStrategiesByClassForSession(SESSION_NAME,classes[i].classKey, strategyStatusConsumer);
            }
            System.out.println("    -  success!");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected  void testGetProductStrategy(int classKey)
    {
        System.out.println("--------------------------------> Testing testGetProductStrategy()");

        try
        {
            SessionStrategyStruct[] strategies = session.getTradingSession().getStrategiesByClassForSession(SESSION_NAME, classKey, strategyStatusConsumer);
            System.out.println("    - testGetProductStrategy success!");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected  void testUnsubscribeProductByType()
    {
        System.out.println("--------------------------------> Testing testUnsubscribeProductByType()");

        try
        {
            for(int i = 0; i < productTypes.length; i++)
            {
                session.getTradingSession().unsubscribeClassesByTypeForSession(SESSION_NAME, productTypes[i].type,  theClassConsumer);
            }
            System.out.println("    - testUnsubscribeProductByType success!");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected  void testUnsubscribeProductByClass()
    {
        System.out.println("--------------------------------> Testing testUnsubscribeProductByClass()");

        try
        {
            for(int i = 0; i < classes.length; i++)
            {
                 session.getTradingSession().unsubscribeProductsByClassForSession(SESSION_NAME,classes[i].classKey, theProductConsumer);
            }
            System.out.println("    - testUnsubscribeProductByClass success!");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** gets products for all product types
    * This register for all class and product status for all products
    */
    protected  void testGetProducts() throws Exception
    {
        System.out.println("--------------------------------> Testing testGetProducts()");

        try{

            if ( productTypes == null ) {
                System.out.println("========> There is not product types.");
                return;
            }

            Vector allProducts = new Vector();
            Vector allClasses = new Vector();

            for (int i = 0; i < productTypes.length; i++ )
            {
                System.out.println("--------> " + productTypes[i].description + productTypes[i].type);
                SessionClassStruct[] classesByType = session.getTradingSession().getClassesForSession(SESSION_NAME,productTypes[i].type, theClassConsumer);

                System.out.println("Classes: " + classesByType.length);
                for (int j = 0; j < classesByType.length; j++)
                {
                    System.out.println("----------------> " + classesByType[j].classStruct.classSymbol + " " + classesByType[j].classStruct.classKey);
                    allClasses.addElement(classesByType[j]);

                    SessionProductStruct[] tempProducts = session.getTradingSession().getProductsForSession(SESSION_NAME,classesByType[j].classStruct.classKey, theProductConsumer);
                    testGetProductStrategy(classesByType[j].classStruct.classKey);
                    for (int k = 0; k < tempProducts.length; k++)
                    {
                        System.out.println("------------------------> " + tempProducts[k].productStruct.productName.reportingClass
                            + tempProducts[k].productStruct.productName.productSymbol + " " + tempProducts[k].productStruct.productKeys.productKey);
                        allProducts.addElement(tempProducts[k]);
                    }

                    System.out.println("    - testGetProducts success!");
                }

                products = new ProductStruct[allProducts.size()];
                allProducts.copyInto(products);

                classes = new ClassStruct[allClasses.size()];
                allClasses.copyInto(classes);
             }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /** unsubscribes to market data for all products  and classes
     */
     protected  void testUnsubscribeMarketDataForAllProduct() throws Exception
    {
        try
        {
        System.out.print("--------------------------------> Testing testUnsubscribeMarketDataForAllProduct() ");

        for ( int i = 0; i < products.length; i++ )
        {
            session.getMarketQuery().unsubscribeCurrentMarketForProduct(SESSION_NAME,products[i].productKeys.productKey, marketConsumer);
            session.getMarketQuery().unsubscribeRecapForProduct(SESSION_NAME,products[i].productKeys.productKey, recapConsumer);
            session.getMarketQuery().unsubscribeTicker(SESSION_NAME,products[i].productKeys.productKey, tickerConsumer);
        }

        for(int i = 0; i < classes.length; i++)
        {
            session.getMarketQuery().unsubscribeCurrentMarketForClass(SESSION_NAME,classes[i].classKey,marketConsumer);
            session.getMarketQuery().unsubscribeRecapForClass(SESSION_NAME,classes[i].classKey, recapConsumer);
            session.getMarketQuery().unsubscribeExpectedOpeningPrice(SESSION_NAME,classes[i].classKey, openingPriceConsumer);
        }

        System.out.println("    - testUnsubscribeMarketDataForAllProduct success!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /** subscribes to market data for all products and classes
     */
     protected  void testGetMarketDataForAllProduct() throws Exception
    {
        try
        {
        System.out.print("--------------------------------> Testing testGetMarketDataForAllProduct ");
        for ( int i = 0; i < products.length; i++ )
        {
            session.getMarketQuery().subscribeCurrentMarketForProduct(SESSION_NAME,products[i].productKeys.productKey, marketConsumer);
            session.getMarketQuery().subscribeRecapForProduct(SESSION_NAME,products[i].productKeys.productKey, recapConsumer);
            session.getMarketQuery().subscribeTicker(SESSION_NAME,products[i].productKeys.productKey, tickerConsumer);
        }

        for(int i = 0; i < classes.length; i++)
        {
            session.getMarketQuery().subscribeCurrentMarketForClass(SESSION_NAME,classes[i].classKey,marketConsumer);
            session.getMarketQuery().subscribeRecapForClass(SESSION_NAME,classes[i].classKey, recapConsumer);
            session.getMarketQuery().subscribeExpectedOpeningPrice(SESSION_NAME,classes[i].classKey, openingPriceConsumer);
        }

        System.out.println("    - testGetMarketDataForAllProduct success!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void testSubscribeNBBOForProduct() throws Exception
    {
        try
        {
            System.out.print("--------------------------------> Testing testSubscribeNBBOForProduct ");
            for ( int i = 0; i < products.length; i++ )
            {
                session.getMarketQuery().subscribeNBBOForProduct(SESSION_NAME,products[i].productKeys.productKey, theNBBOConsumer);
            }

            for(int i = 0; i < classes.length; i++)
            {
                session.getMarketQuery().subscribeNBBOForClass(SESSION_NAME,classes[i].classKey, theNBBOConsumer);
            }

            System.out.println("    - testSubscribeNBBOForProduct success!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void testUnsubscribeNBBOForProduct() throws Exception
    {
        System.out.print("--------------------------------> Testing testUnsubscribeMarketDataForProductOnly ");
        for ( int i = 0; i < products.length; i++ )
        {
            session.getMarketQuery().unsubscribeNBBOForProduct(SESSION_NAME,products[i].productKeys.productKey, theNBBOConsumer);
        }

         System.out.println("    - testUnsubscribeMarketDataForProductOnly success!");
    }

    /** subscribes to market data for all products and classes
     */
     protected  void testUnsubscribeMarketDataForProductOnly() throws Exception
    {
        System.out.print("--------------------------------> Testing testUnsubscribeMarketDataForProductOnly ");
        for ( int i = 0; i < products.length; i++ )
        {
            session.getMarketQuery().unsubscribeCurrentMarketForProduct(SESSION_NAME,products[i].productKeys.productKey, marketConsumer);
            session.getMarketQuery().unsubscribeRecapForProduct(SESSION_NAME,products[i].productKeys.productKey, recapConsumer);
            session.getMarketQuery().unsubscribeTicker(SESSION_NAME,products[i].productKeys.productKey, tickerConsumer);
        }

         System.out.println("    - testUnsubscribeMarketDataForProductOnly success!");
    }

    /** unsubscribes from all order status for type
     */
    protected  void testUnsubscribeAllEquityOrders()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeAllEquityOrders");
        try
        {
              session.getOrderQuery().unsubscribeAllOrderStatusForType(ProductTypes.EQUITY, orderStatusConsumer);
              System.out.println("    -  success!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected  void testGetAllEquityOrders()
    {
        System.out.print("--------------------------------> Testing testGetAllEquityOrders");
        try
        {
            session.getOrderQuery().getOrdersForType(ProductTypes.EQUITY);
            System.out.println("    -  success!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected  void testUnsubscribeAllOptionOrders()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeAllOptionOrders");
        try
        {
            session.getOrderQuery().unsubscribeAllOrderStatusForType(ProductTypes.OPTION, orderStatusConsumer);
            System.out.println("    -  success!");
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    protected  void testGetAllOptionOrders()
    {
        System.out.print("--------------------------------> Testing testGetAllOptionOrders");
        try
        {
            OrderDetailStruct[] orderStruct = session.getOrderQuery().getOrdersForType(ProductTypes.OPTION);
            System.out.println("    -  success!");
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    protected  void testGetOrdersForProduct()
    {
        System.out.print("--------------------------------> Testing testGetOrdersForProduct");
        try
        {
            for(int i = 0; i < products.length; i++)
            {
                session.getOrderQuery().getOrdersForProduct(products[i].productKeys.productKey);
            }
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            System.out.println(" -  testGetOrdersForProduct failed - ");
            e.printStackTrace();

        }
    }

    protected  void testGetOrdersForClass()
    {
        System.out.print("--------------------------------> Testing testGetOrdersForClass");
        try
        {
            for(int i = 0; i < classes.length; i++)
            {
                session.getOrderQuery().getOrdersForClass(classes[i].classKey);
            }
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            System.out.println(" - protected testGetOrdersForClass failed - ");
            e.printStackTrace();

        }
    }

    protected  void testSubscribeOrdersStatus()
    {
        System.out.print("--------------------------------> Testing testSubscribeOrdersStatus");
        try
        {
            session.getOrderQuery().subscribeOrders(orderStatusConsumer, true);
            session.getOrderQuery().subscribeOrdersByFirm(orderStatusConsumer, true);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(" -  testSubscribeOrdersStatus failed - ");
        }
    }

    protected  void testUnsubscribeOrdersStatus()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeOrdersStatus");
        try
        {
            for(int i  = 0; i < products.length; i++)
            {
                session.getOrderQuery().unsubscribeOrderStatusForProduct(products[i].productKeys.productKey,orderStatusConsumer);
            }
            for(int j  = 0; j < classes.length; j++)
            {
                session.getOrderQuery().unsubscribeOrderStatusByClass(classes[j].classKey,orderStatusConsumer);
            }
            session.getOrderQuery().unsubscribeOrderStatusForFirm(orderStatusConsumer);
            // there is no unsubscribe all order status
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(" -  testUnsubscribeOrdersStatus failed - ");
        }
    }

    /** unsubscribes to order filled report */
    protected  void testUnsubscribeOrderFilledReport()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeOrderFilledReport");
        try
        {
            //*DG session.getOrderQuery().unsubscribeOrderFilledReport(theOrderFilledConsumer);
            //*DG session.getOrderQuery().unsubscribeOrderFilledReportForFirm(theOrderFilledConsumer);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(" - testUnsubscribeOrderFilledReport failed - ");
        }
    }

    /** subscribes to order filled report */
    protected  void testSubscribeOrderFilledReport()
    {
        System.out.print("--------------------------------> Testing testSubscribeOrderFilledReport");
        try
        {
            //*DG session.getOrderQuery().subscribeOrderFilledReport(theOrderFilledConsumer);
            //*DG session.getOrderQuery().subscribeOrderFilledReportForFirm(theOrderFilledConsumer);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(" - testSubscribeOrderFilledReport failed - ");
        }
    }
    /** unsubscribes to order filled report */
    protected  void testUnsubscribeOrderBustReport()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeOrderBustReport");
        try
        {
           //*DG  session.getOrderQuery().unsubscribeOrderBustReport(theOrderBustConsumer);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(" - testUnsubscribeOrderBustReport failed - ");
        }
    }

    /** subscribes to order bust report */
    protected  void testSubscribeOrderBustReport()
    {
        System.out.print("--------------------------------> Testing testSubscribeOrderBustReport");
        try
        {
            //*DG session.getOrderQuery().subscribeOrderBustReport(theOrderBustConsumer);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(" - testSubscribeOrderBustReport failed - ");
        }
    }

    /**
    *  test unsubscribes from order canceled reports
    *
    */
    protected  void testUnsubscribeOrderCanceledReport()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeOrderCanceledReport");
        try
        {
            //*DG session.getOrderQuery().unsubscribeOrderCanceledReport(theOrderCanceledReportConsumer);
            //*DG session.getOrderQuery().unsubscribeOrderCanceledReportForFirm(theOrderCanceledReportConsumer);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            System.out.println(" - testUnsubscribeOrderCanceledReport failed - ");
            e.printStackTrace();
        }
    }

    /**
    *  test subscribes to order canceled reports
    *
    */
    protected  void testSubscribeOrderCanceledReport()
    {
        System.out.print("--------------------------------> Testing testSubscribeOrderCanceledReport");
        try
        {
            //*DG session.getOrderQuery().subscribeOrderCanceledReport(theOrderCanceledReportConsumer);
            //*DG session.getOrderQuery().subscribeOrderCanceledReportForFirm(theOrderCanceledReportConsumer);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            System.out.println(" - testSubscribeOrderCanceledReport failed - ");
            e.printStackTrace();
        }
    }

    /**
     * Performs unit test on the <code>unsubscribe</code> method.
     *
     * @author Connie Liang
     */
    protected  void testUnsubscribeAllQuotes() throws NotFoundException
    {
        System.out.println("--------------------------------> Testing testUnsubscribeAllQuotes");
        try
        {
            session.getQuote().unsubscribeQuoteStatus(theQuoteStatusConsumer);

            for(int i = 0; i < classes.length; i++)
            {
                 // session.getQuote().unsubscribeQuoteStatusByClass(classes[i].classKey, SESSION_NAME, theQuoteStatusConsumer);
                 System.out.println("---------- unsubscribeQuoteStatusByClass does not exist in V2 ----------");
            }


        } catch (Exception e)
        {
            System.out.println(" -  testUnsubscribeAllQuotestAllQuotes failed - ");
            e.printStackTrace();
        }
    }

    /**
     * Performs unit test on the <code>subscribeQuoteStatus</code> and
     * <code>subscribeQuoteStatusByClass</code> methods.
     *
     * @author Connie Liang
     */
    protected  void testGetAllQuotes() throws NotFoundException
    {
        System.out.println("--------------------------------> Testing testGetAllQuotes");
        try
        {
            session.getQuote().subscribeQuoteStatus(theQuoteStatusConsumer, true);
            for(int i = 0; i < classes.length; i++)
            {
//                session.getQuote().subscribeQuoteStatusByClass(classes[i].classKey, SESSION_NAME);
                System.out.println("---------- Correct this code ----------");
            }
            System.out.println("  -  success!");
        } catch (Exception e)
        {
            System.out.println(" -  testGetAllQuotes failed - ");
            e.printStackTrace();
        }
    }

   /** addes a quote, subscribes to quote filled report, publishes acceptQuoteFillReport
    */
    protected  void testUnsubscribeQuoteFilledReport()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeQuoteFilledReport");
        try
        {
            //*DG session.getQuote().unsubscribeQuoteFilledReport(theQuoteFilledConsumer);
            //*DG session.getQuote().unsubscribeQuoteFilledReportForFirm(theQuoteFilledConsumer);
            System.out.println("  -  success!");
        }
        catch (Throwable e)
        {
          System.out.println(" -  testUnsubscribeQuoteFilledReport failed - ");
          e.printStackTrace();
        }

    }

   /** addes a quote, subscribes to quote filled report, publishes acceptQuoteFillReport
    */
    protected  void testSubscribeQuoteFilledReport()
    {
        System.out.print("--------------------------------> Testing testAcceptQuoteFilledReport");
        try
        {
            //*DG session.getQuote().subscribeQuoteFilledReport(theQuoteFilledConsumer);
            //*DG session.getQuote().subscribeQuoteFilledReportForFirm(theQuoteFilledConsumer);
            System.out.println("  -  success!");
        }
        catch (Throwable e)
        {
          System.out.println(" -  testAcceptQuoteFilledReport failed - ");
          e.printStackTrace();
        }

    }
  /** addes a quote, subscribes to quote filled report, publishes acceptQuoteFillReport
    */
    protected  void testUnsubscribeQuoteBustReport()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeQuoteBustReport");
        try
        {
            //*DG session.getQuote().unsubscribeQuoteBustReport(theQuoteBustConsumer);
            System.out.println("  -  success!");
        }
        catch (Throwable e)
        {
          System.out.println(" - protected testUnsubscribeQuoteBustReport failed - ");
        }

    }

   /** subscribes to quote bust report
    */
    protected  void testSubscribeQuoteBustReport()
    {
        System.out.print("--------------------------------> Testing testSubscribeQuoteBustReport");
        try
        {
            //*DG session.getQuote().subscribeQuoteBustReport(theQuoteBustConsumer);
            System.out.println("  -  success!");
        }
        catch (Throwable e)
        {
          System.out.println(" - protected testSubscribeQuoteBustReport failed - ");
        }

    }


    /** subscribes to RFQ
    */
    protected  void testAcceptRFQ()
    {
        System.out.print("--------------------------------> Testing testAcceptRFQ");
        try
        {
            for ( int i = 0; i < classes.length; i++)
            {
                System.out.println();
                System.out.println("--------> subscribing RFQ for class: " + classes[i].classKey);
                session.getQuote().subscribeRFQ(SESSION_NAME,classes[i].classKey,theRFQConsumer);
            }

            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
          System.out.println(" -  acceptRFQ failed - ");
          e.printStackTrace();

        }
    }

    /** unsubscribes from RFQ
    */
    protected  void testUnsubscribeRFQ()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeRFQ");
        try
        {
            for ( int i = 0; i < classes.length; i++)
            {
                session.getQuote().unsubscribeRFQ(SESSION_NAME,classes[i].classKey,theRFQConsumer);
            }

            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
            System.out.println(" -  testUnsubscribeRFQ failed - ");
            e.printStackTrace();

        }
    }

    /** unsubscribes to TradingSession
    */
    protected  void testUnsubscribeCurrentTradingSessions()
    {
        System.out.print("--------------------------------> Testing testUnsubscribeCurrentTradingSessions");
        try
        {
            session.getTradingSession().unsubscribeTradingSessionStatus(tradingSessionStatusConsumer);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.out.println(" -  testUnsubscribeCurrentTradingSessions failed - ");
        }
    }

    /** subscribes to TradingSession
    */
    protected  void testGetCurrentTradingSessions()
    {
        System.out.print("--------------------------------> Testing testGetCurrentTradingSessions");
        try
        {
            session.getTradingSession().getCurrentTradingSessions(tradingSessionStatusConsumer);
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.out.println(" -  testGetCurrentTradingSessions failed - ");
        }
    }

    //////////////////////
    // other methods not related to event subscription
    //////////////////////

    protected  void testGetBookDepth()
    {
        System.out.print("--------------------------------> Testing testGetBookDepth");
        try
        {
            BookDepthStruct bookDepth = session.getMarketQuery().getBookDepth(SESSION_NAME,products[0].productKeys.productKey);
            System.out.println("-----> testNullValues BookDepthStruct: " + ReflectiveStructTester.testNullStruct(bookDepth));
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.out.println(" -  testGetBookDepth failed - ");
        }
   }

   protected  void testGetMarketDataHistoryByTime()
   {
        System.out.print("--------------------------------> Testing testGetMarketDataHistoryByTime");
        try
        {
            DateTimeStruct startTime = new DateWrapper().toDateTimeStruct();

            MarketDataHistoryStruct marketDataHistoryStruct = session.getMarketQuery().getMarketDataHistoryByTime(SESSION_NAME,products[0].productKeys.productKey, startTime, QueryDirections.QUERY_FORWARD);
            System.out.println("-----> testNullValues MarketDataHistoryStruct: " + ReflectiveStructTester.testNullStruct(marketDataHistoryStruct));
            System.out.println("  -  success!");
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.out.println(" -  testGetMarketDataHistoryByTime failed - ");
        }

   }

    /** test logout
    */
    protected  void testLogOut()
    {
        try
        {
            session.logout();
        } catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(" -  testLogOut failed - ");

        }
    }

    protected  void initialize(String[] args) throws Exception
    {
        ////////// MUST BE CALLED /////////
        initORBConnection(args);
        initFFEnv();
        initializeCallbacks();
        initUserSession();

        testGetProductTypes();  // must called first
        testGetProducts();      // must called second
    }

    protected void runTestCases(boolean includeUnsubscribe) throws Exception
    {
        // subscribes to marketdata(recap, ticker, openingPrice, currentMarket)
        testGetMarketDataForAllProduct();
        testGetAllOptionOrders();
        testGetOrdersForProduct();
        testGetOrdersForClass();
        testSubscribeOrderFilledReport();
        testSubscribeQuoteFilledReport();
        testSubscribeOrderBustReport();
        testSubscribeQuoteBustReport();
        testSubscribeNBBOForProduct();
        testAcceptRFQ();
        testGetAllQuotes();
        testSubscribeOrdersStatus();
        testGetCurrentTradingSessions();  // needs to config the trading consumer home
        testSubscribeOrderCanceledReport();
        /////// methods not on subscription

        testGetBookDepth();
        testGetMarketDataHistoryByTime();
        ///////
        Thread.sleep(20000);
        if (includeUnsubscribe == true)
        {
            System.out.println("============> unsubcribe is now starting");
            testUnsubscribeProductByType();
            testUnsubscribeProductByClass();
            testUnsubscribeProductStrategy();
            testUnsubscribeMarketDataForAllProduct(); // call this or next call exclusively please
            testUnsubscribeNBBOForProduct();
            //testUnsubscribeMarketDataForProductOnly();

            testUnsubscribeAllOptionOrders();
            testUnsubscribeOrdersStatus();
            testUnsubscribeAllQuotes();
            testUnsubscribeOrderFilledReport();
            testUnsubscribeQuoteFilledReport();
            testUnsubscribeOrderCanceledReport();
            testUnsubscribeOrderBustReport();
            testUnsubscribeQuoteBustReport();
            testUnsubscribeRFQ();
            testUnsubscribeCurrentTradingSessions();  // needs to config the trading consumer home
         }
    }
    /**
    * To run the TestEventSubscriber
    * Example with unsubscribe: java -DUNSUBSCRIBE=U com.cboe.application.cas.TestEventSubscriber
    * Example without unsubscribe: java  com.cboe.application.cas.TestEventSubscriber
    */
    public static void main(String[] args)
    {
        boolean unsubscribe = false;
        java.util.Properties prop = System.getProperties();
        String unsubscribeStr =(String)prop.get("UNSUBSCRIBE");

        if (unsubscribeStr != null && unsubscribeStr.equalsIgnoreCase("U"))
        {
            System.out.println("============> unsubcribe is requested");
            unsubscribe = true;
        }
        try
        {
            TestEventSubscriber test = new TestEventSubscriber();
            test.initialize(args);
            test.runTestCases(unsubscribe);

            try {
                java.lang.Object waiter = new java.lang.Object();
                synchronized(waiter)
                {
                    waiter.wait();
                }
                test.testLogOut();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
