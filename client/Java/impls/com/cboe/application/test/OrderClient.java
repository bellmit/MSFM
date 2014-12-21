package com.cboe.application.test;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiV2.OrderQuery;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiIntermarket.IntermarketSessionManagerStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.domain.util.*;
import com.cboe.application.cas.TestUserAccessV2Factory;
import com.cboe.application.cas.TestCallback;
import com.cboe.application.cas.TestUserAccessV3Factory;
import com.cboe.application.shared.RemoteConnection;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.UnitTestHelper;
import com.cboe.delegates.callback.UserSessionAdminConsumerDelegate;
import com.cboe.delegates.callback.QuoteStatusConsumerDelegate;
import com.cboe.delegates.callback.RFQConsumerDelegate;
import com.cboe.delegates.callback.OrderStatusConsumerDelegate;
import com.cboe.util.event.EventChannelAdapterFactory;


import java.util.Properties;
import java.util.Random;
import java.io.FileInputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

public class OrderClient
{

    public static final String SYSTEM_SECTION = "System";
    public static final String SETUP_SETTINGS = "Setup";
    public static final String USER = "User";
    public static final String PASSWORD = "Password";
    public static final String LOGIN_MODE = "LoginOperationMode";
    public static final String LOGIN_SESSION = "SessionLoginType";
    public static final String SESSION_NAME = "SessionName";
    public static final String PRODUCT_TYPE = "ProductType";
    public static final String CLASS_SYMBOL = "ClassSymbol";
    public static final String GMD = "gmd";
    public static final String PROPERTY_FILE = "PropertyFile";

    public static RemoteConnection connection;
    public static SessionManagerStructV2 sessionManagerV2;
    public static SessionManagerStructV2 userSessionManager;
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
    private static POA poaReference;
    private static UserAccessV2 userAccessV2;
    private static UserAccessV3 userAccessV3;
    private static Properties properties;

    private static CMIOrderStatusConsumer orderConsumer;
    private static com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer orderV2Consumer;

    private static String classSymbol;
    private static String sessionName;
    private static int classKey;
    private static int productKey;
    private static short productType;

    private static double currentPrice;

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

    /** initializes the ORB connection object */
    public static void initORBConnection(String[] args)
    {
        if ( connection == null )
        {
            connection = RemoteConnectionFactory.create(args);
            EventChannelAdapterFactory.find().setDynamicChannels(true);
        }
    }

    private static UserAccessV2 getUserAccessV2()
    {
        if(userAccessV2 == null)
        {
            userAccessV2 = TestUserAccessV2Factory.find();
        }
        return userAccessV2;
    }

    private static UserAccessV3 getUserAccessV3()
    {
        if(userAccessV3 == null)
        {
            userAccessV3 = TestUserAccessV3Factory.find();
        }
        return userAccessV3;
    }
    public static TestCallback getCallback() {
        if (callback == null) {
            callback = new TestCallback();
        }
        return callback;
    }

    private static CMIUserSessionAdmin getUserSessionAdmin()
    {

        TestCallback callbackConsumer = getCallback();
        UserSessionAdminConsumerDelegate clientListener = new UserSessionAdminConsumerDelegate(callbackConsumer);
        try {
        org.omg.CORBA.Object orbObject = getPOA().servant_to_reference(clientListener);
            CMIUserSessionAdmin cmiCallback = CMIUserSessionAdminHelper.narrow(orbObject);
            return cmiCallback;
        } catch ( Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private static CMIOrderStatusConsumer getOrderConsumer()
    {
        if (orderConsumer == null )
        {
        OrderStatusConsumerDelegate delegate= new OrderStatusConsumerDelegate(getCallback());
        org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
        orderConsumer = CMIOrderStatusConsumerHelper.narrow (corbaObject);
        }
        return  orderConsumer;
    }



    private static com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer getOrderV2Consumer()
    {
        if ( orderV2Consumer == null)
        {
        com.cboe.idl.cmiCallbackV2.POA_CMIOrderStatusConsumer_tie delegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMIOrderStatusConsumer_tie(getCallback());
        org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateV2);
        orderV2Consumer = com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerHelper.narrow (corbaObject);
        }
        return orderV2Consumer;
    }

    private static CMIRFQConsumer getRFQConsumer()
    {
          RFQConsumerDelegate delegateRFQ= new RFQConsumerDelegate(getCallback());
               org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateRFQ);
               CMIRFQConsumer rfqConsumer = CMIRFQConsumerHelper.narrow (corbaObject);
               return rfqConsumer;
    }

    private static com.cboe.idl.cmiCallbackV2.CMIRFQConsumer getRFQV2Consumer()
    {
        com.cboe.idl.cmiCallbackV2.POA_CMIRFQConsumer_tie delegateRFQV2= new com.cboe.idl.cmiCallbackV2.POA_CMIRFQConsumer_tie(getCallback());
               org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateRFQV2);
               com.cboe.idl.cmiCallbackV2.CMIRFQConsumer rfqV2Consumer = com.cboe.idl.cmiCallbackV2.CMIRFQConsumerHelper.narrow (corbaObject);
    return rfqV2Consumer;
    }

     private static void logon() throws AuthenticationException, DataValidationException, SystemException, CommunicationException, AuthorizationException, NotFoundException
     {
         UserLogonStruct userLogonStruct = new UserLogonStruct(
                 properties.getProperty(USER).toString(),
                 properties.getProperty(PASSWORD).toString(),
                 Version.CMI_VERSION,
                 properties.getProperty(LOGIN_MODE).toString().charAt(0));
         short sessionType = Short.parseShort(properties.getProperty(LOGIN_SESSION).toString());
         boolean gmd = false;
         if ( Short.parseShort(properties.getProperty(GMD).toString()) == 1)
            gmd = true;
         else
            gmd = false;
         sessionV3 = getUserAccessV3().logon(userLogonStruct, sessionType, getUserSessionAdmin(), gmd);
         sessionV2 = sessionV3;
         session =  sessionV3;
     }

     private static Properties loadProperties(String fileName)
     {
         try{
             properties = new Properties();
             properties.load(new FileInputStream(fileName));
         }
         catch (Exception ex)
         {
             System.out.println("Exception in specified ini file " + fileName + ". Skipping file.");
             System.out.println(ex);
             ex.printStackTrace();
             System.exit(1);
         }
         return properties;
     }
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(String[] args)
{

    if ( System.getProperties().getProperty(PROPERTY_FILE) != null)
    {
        loadProperties(System.getProperties().getProperty(PROPERTY_FILE));
    }
    else
    {
        properties = System.getProperties();
    }

	try
	{
        initORBConnection(args);
        initPOA();
        logon();

        sessionName = properties.getProperty(SESSION_NAME).toString();
        classSymbol = properties.getProperty(CLASS_SYMBOL).toString();
        productType = Short.parseShort(properties.getProperty(PRODUCT_TYPE).toString());
//        classKey = session.getProductQuery().getClassBySymbol(productType, classSymbol).classKey;
//        productKey = session.getProductQuery().getProductsByClass(classKey)[0].productKeys.productKey;

//        classKey = 8520441;     HOB
//        productKey = 100925820;

        classKey = 196821;         //IBM
        productKey = 106431946;

        System.out.println("1)subscribeOrderStatusForClassV2");
        System.out.println("2)subscribeOrderStatusForFirmForClassV2");
        System.out.println("3)subscribeOrderStatusForFirmV2");
        System.out.println("4)subscribeOrderStatusV2");
        System.out.println("5)unsubscribeOrderStatusForClassV2");
        System.out.println("6)unsubscribeOrderStatusForFirmForClassV2");
        System.out.println("7)unsubscribeOrderStatusForFirmV2");
        System.out.println("8)unsubscribeOrderStatusV2");
        System.out.println("9)getOrderById(OrderIdStruct orderIdStruct");
        System.out.println("10)getOrdersForClass");
        System.out.println("11)getOrdersForProduct");
        System.out.println("12)getOrdersForSession");
        System.out.println("13)getOrdersForType");
        System.out.println("14)getPendingAdjustmentOrdersByClass");
        System.out.println("15)getPendingAdjustmentOrdersByProduct");
        System.out.println("16)queryOrderHistory");
        System.out.println("17)subscribeOrders");
        System.out.println("18)subscribeOrdersByFirm");
        System.out.println("19)subscribeOrdersByFirmWithoutPublish");
        System.out.println("20)subscribeOrdersWithoutPublish");
        System.out.println("21)unsubscribeAllOrderStatusForType");
        System.out.println("22)unsubscribeOrderStatusByClass");
        System.out.println("23)unsubscribeOrderStatusForFirm");
        System.out.println("24)unsubscribeOrderStatusForProduct");
        System.out.println("25)unsubscribeOrderStatusForSession");
        System.out.println("26)acceptCrossingOrder");
        System.out.println("27)acceptOrder");
        System.out.println("28)acceptOrderByProductName");
        System.out.println("29)acceptOrderCancelReplaceRequest");
        System.out.println("30)acceptOrderCancelRequest");
        System.out.println("31)acceptOrderUpdateRequest");
        System.out.println("32)acceptRequestForQuote");
        System.out.println("33)acceptStrategyOrder");
        System.out.println("34)acceptStrategyOrderCancelReplaceRequest");
        System.out.println("35)acceptStrategyOrderUpdateRequest");

        int choice = 0;
        while ( choice !=-1 )
        {
            int ch;
            choice = 0;
            while ((ch = System.in.read ()) != '\n')
               if (ch >= '0' && ch <= '9')
               {
                   choice *= 10;
                   choice += ch - '0';
               }
               else
                   break;
            System.out.println("Entering your choice :");
            System.out.println ("choice = " + choice);

            switch (choice) {
                case 1:
                    subscribeOrderStatusForClassV2(classKey, getOrderV2Consumer(), false, false);
                break;
                case 2:
                    subscribeOrderStatusForFirmForClassV2(classKey, getOrderV2Consumer(), true, true);
                    break;
                case 3:
                    subscribeOrderStatusForFirmV2(getOrderV2Consumer(), true, true);
                    break;
                case 4:
                    subscribeOrderStatusV2(getOrderV2Consumer(), true, true);
                    break;
                case 5:
                    unsubscribeOrderStatusForClassV2(classKey, getOrderV2Consumer());
                    break;
                case 6:
                    unsubscribeOrderStatusForFirmForClassV2(classKey, getOrderV2Consumer());
                    break;
                case 7:
                    unsubscribeOrderStatusForFirmV2(getOrderV2Consumer());
                    break;
                case 8:
                    unsubscribeOrderStatusV2(getOrderV2Consumer());
                    break;
                case 9:
                    OrderIdStruct orderId = new OrderIdStruct();
                    getOrderById(orderId);
                    break;
                case 10:
                    getOrdersForClass(classKey);
                    break;
                case 11:
                    getOrdersForProduct(productKey);
                    break;
                case 12:
                    getOrdersForSession(sessionName);
                    break;
                case 13:
                    getOrdersForType(productType);
                    break;
                case 14:
                    getPendingAdjustmentOrdersByClass(sessionName, classKey);
                    break;
                case 15:
                    getPendingAdjustmentOrdersByProduct(sessionName, productKey);
                    break;
                case 16:
                    OrderIdStruct id = new OrderIdStruct();
                    queryOrderHistory(id);
                case 17:
                    subscribeOrders(getOrderConsumer(), true);
                    break;
                case 18:
                    subscribeOrdersByFirm(getOrderConsumer(), true);
                    break;
                case 19:
                    subscribeOrdersByFirmWithoutPublish(getOrderConsumer(), true);
                    break;
                case 20:
                    subscribeOrdersWithoutPublish(getOrderConsumer(), true);
                    break;
                case 21:
                   unsubscribeAllOrderStatusForType(productType, getOrderConsumer());
                    break;
                case 22:
                    unsubscribeOrderStatusByClass(classKey, getOrderConsumer());
                    break;
                case 23:
                    unsubscribeOrderStatusForFirm(getOrderConsumer());
                    break;
                case 24:
                    unsubscribeOrderStatusForProduct(productKey, getOrderConsumer());
                    break;
                case 25:
                    unsubscribeOrderStatusForSession(sessionName, getOrderConsumer());
                    break;
                case 26:
                    acceptCrossingOrder(makeAOrder(), makeAOrder());
                    break;
                case 27:
                    acceptOrder(makeAOrder());
                    break;
                case 28:
                    acceptOrderByProductName(null, null);
                    break;
                case 29:
                    acceptOrderCancelReplaceRequest(null, null);
                    break;
                case 30:
                    //acceptOrderCancelRequest(null, null, null);
                    break;
                case 31:
                    //acceptOrderUpdateRequest()
                    break;
                case 32:
                    // acceptRequestForQuote(makeRFQ());
                    break;
                case 33:
                    acceptStrategyOrder(null, null);
                    break;
                case 34:
                    //acceptStrategyOrderCancelReplaceRequest
                    break;
                case 35:
                    //acceptStrategyOrderUpdateRequest(productKey, get)
                    break;


            }
        }

    }
	catch (Exception e)
	{
		System.out.println("caught " + e);
		e.printStackTrace();
	}

	System.out.println("done");
	System.exit(1);
}



        private static OrderEntryStruct makeAOrder()
        {
            currentPrice  = currentPrice + 0.5;
            OrderEntryStruct newOrder = getBuyOrder();
            newOrder.price = PriceFactory.create(currentPrice).toStruct();
            return newOrder;

        }

    private static OrderEntryStruct getBuyOrder()
    {
        ExchangeFirmStruct firm = StructBuilder.buildExchangeFirmStruct("CBOE", "671");
        OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",getRandomOrderId(),"FRM","20040420");
        OrderEntryStruct order = UnitTestHelper.createOrderEntryStruct(
            "ONE_MAIN",
            productKey,
            properties.getProperty(USER).toString(),
            inputOrderId);        order.account = properties.getProperty(USER).toString();
        order.price = PriceFactory.create(2.0).toStruct();
        order.originalQuantity = 10;
        order.contingency.price = PriceFactory.create( 0 ).toStruct();
        order.branchSequenceNumber = 10;
	    order.side = Sides.BUY;
	    return order;
    }

    private static OrderEntryStruct createSellOrder()
    {
	    OrderEntryStruct order = getBuyOrder();
	    order.side = Sides.SELL;
	    return order;
    }

    private static void subscribeOrderStatusForClassV2(int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer cmiOrderStatusConsumer, boolean b, boolean b1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling subscribeOrderStatusForClassV2");
        try {

            sessionV2.getOrderQueryV2().subscribeOrderStatusForClassV2(classKey, cmiOrderStatusConsumer, b, b1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeOrderStatusForClassV2 done");

    }

    private static void subscribeOrderStatusForFirmForClassV2(int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer cmiOrderStatusConsumer, boolean b, boolean b1)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling subscribeOrderStatusForFirmForClassV2");
        try {

            sessionV2.getOrderQueryV2().subscribeOrderStatusForFirmForClassV2(classKey, getOrderV2Consumer(), b, b1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeOrderStatusForFirmForClassV2 done");

    }

    private static void subscribeOrderStatusForFirmV2( com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer cmiOrderStatusConsumer, boolean b, boolean b1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling subscribeOrderStatusForFirmV2");
        try {

            sessionV2.getOrderQueryV2().subscribeOrderStatusForFirmV2(getOrderV2Consumer(), b, b1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeOrderStatusForFirmV2 done");

    }

    private static void subscribeOrderStatusV2(com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer cmiOrderStatusConsumer, boolean b, boolean b1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling subscribeOrderStatusV2");
        try {

            sessionV2.getOrderQueryV2().subscribeOrderStatusV2(getOrderV2Consumer(), b, b1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeOrderStatusV2 done");

    }

    private static void unsubscribeOrderStatusForClassV2(int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer cmiOrderStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling unsubscribeOrderStatusForClassV2");
        try {

            sessionV2.getOrderQueryV2().unsubscribeOrderStatusForClassV2(classKey, getOrderV2Consumer());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("unsubscribeOrderStatusForClassV2 done");

    }

    private static void unsubscribeOrderStatusForFirmForClassV2(int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer cmiOrderStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling unsubscribeOrderStatusForFirmForClassV2");
        try {

            sessionV2.getOrderQueryV2().unsubscribeOrderStatusForFirmForClassV2(classKey, getOrderV2Consumer());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("unsubscribeOrderStatusForFirmForClassV2 done");

    }

    private static void unsubscribeOrderStatusForFirmV2(com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer cmiOrderStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling unsubscribeOrderStatusForFirmV2");
        try {

            sessionV2.getOrderQueryV2().unsubscribeOrderStatusForFirmV2(getOrderV2Consumer());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("unsubscribeOrderStatusForFirmV2 done");


    }

    private static void unsubscribeOrderStatusV2(com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer cmiOrderStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling unsubscribeOrderStatusV2");
        try {

            sessionV2.getOrderQueryV2().unsubscribeOrderStatusV2(getOrderV2Consumer());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("unsubscribeOrderStatusV2 done");
    }

    private static OrderDetailStruct getOrderById(OrderIdStruct orderIdStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        System.out.println("calling getOrderById");
        try {

            return sessionV2.getOrderQueryV2().getOrderById(orderIdStruct);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getOrderById done");
        return new OrderDetailStruct();
    }

    private static OrderDetailStruct[] getOrdersForClass(int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling getOrdersForClass");
        try {

            return sessionV2.getOrderQueryV2().getOrdersForClass(classKey);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getOrdersForClass done");
        return new OrderDetailStruct[0];

    }

    private static OrderDetailStruct[] getOrdersForProduct(int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling getOrdersForProduct");
        try {

            return sessionV2.getOrderQueryV2().getOrdersForClass(productKey);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getOrdersForProduct done");
        return new OrderDetailStruct[0];

    }

    private static OrderDetailStruct[] getOrdersForSession(String session)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling getOrdersForSession");
        try {

            return sessionV2.getOrderQueryV2().getOrdersForSession(session);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getOrdersForSession done");
        return new OrderDetailStruct[0];

    }

    private static OrderDetailStruct[] getOrdersForType(short productType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling getOrdersForType");
        try {

            return sessionV2.getOrderQueryV2().getOrdersForType(productType);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getOrdersForType done");
        return new OrderDetailStruct[0];

    }

    private static PendingOrderStruct[] getPendingAdjustmentOrdersByClass(String session, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling getPendingAdjustmentOrdersByClass");
        try {

            return sessionV2.getOrderQueryV2().getPendingAdjustmentOrdersByClass(session, classKey);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getPendingAdjustmentOrdersByClass done");
        return new PendingOrderStruct[0];

    }

    private static PendingOrderStruct[] getPendingAdjustmentOrdersByProduct(String session, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("calling getPendingAdjustmentOrdersByProduct");
        try {

            return sessionV2.getOrderQueryV2().getPendingAdjustmentOrdersByProduct(session, productKey);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getPendingAdjustmentOrdersByProduct done");
        return new PendingOrderStruct[0];

    }

    private static ActivityHistoryStruct queryOrderHistory(OrderIdStruct orderIdStruct)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        System.out.println("calling queryOrderHistory");
        try {

            return sessionV2.getOrderQueryV2().queryOrderHistory(orderIdStruct);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("queryOrderHistory done");
        return new ActivityHistoryStruct();

    }

  private static void subscribeOrders(CMIOrderStatusConsumer cmiOrderStatusConsumer, boolean b)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling subscribeOrders");
      try {

          sessionV2.getOrderQueryV2().subscribeOrders(cmiOrderStatusConsumer, b);
      } catch (Exception e)
      {
          e.printStackTrace();
      }
      System.out.println("subscribeOrders done");
  }

  private static void subscribeOrdersByFirm(CMIOrderStatusConsumer cmiOrderStatusConsumer, boolean b)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling subscribeOrdersByFirm");
      try {

          sessionV2.getOrderQueryV2().subscribeOrdersByFirm(cmiOrderStatusConsumer, b);
      } catch (Exception e)
      {
          e.printStackTrace();
      }
      System.out.println("subscribeOrdersByFirm done");

  }

  private static void subscribeOrdersByFirmWithoutPublish(CMIOrderStatusConsumer cmiOrderStatusConsumer, boolean b)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling subscribeOrdersByFirmWithoutPublish");
      try {

          sessionV2.getOrderQueryV2().subscribeOrdersByFirmWithoutPublish(cmiOrderStatusConsumer, b);
      } catch (Exception e)
      {
          e.printStackTrace();
      }
      System.out.println("subscribeOrdersByFirmWithoutPublish done");

  }

  private static void subscribeOrdersWithoutPublish(CMIOrderStatusConsumer cmiOrderStatusConsumer, boolean b)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling subscribeOrdersWithoutPublish");
      try {

          sessionV2.getOrderQueryV2().subscribeOrdersWithoutPublish(cmiOrderStatusConsumer, b);
      } catch (Exception e)
      {
          e.printStackTrace();
      }
      System.out.println("subscribeOrdersWithoutPublish done");

  }

  private static void unsubscribeAllOrderStatusForType(short type, CMIOrderStatusConsumer cmiOrderStatusConsumer)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling unsubscribeAllOrderStatusForType");
      try {

          sessionV2.getOrderQueryV2().unsubscribeAllOrderStatusForType(type, cmiOrderStatusConsumer);
      } catch (Exception e)
      {
          e.printStackTrace();
      }
      System.out.println("unsubscribeAllOrderStatusForType done");

  }

  private static void unsubscribeOrderStatusByClass(int classKey, CMIOrderStatusConsumer cmiOrderStatusConsumer)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling unsubscribeOrderStatusByClass");
      try {

          sessionV2.getOrderQueryV2().unsubscribeOrderStatusByClass(classKey, cmiOrderStatusConsumer);
      } catch (Exception e)
      {
          e.printStackTrace();
      }
      System.out.println("unsubscribeOrderStatusByClass done");

  }

  private static void unsubscribeOrderStatusForFirm(CMIOrderStatusConsumer cmiOrderStatusConsumer)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling unsubscribeOrderStatusForFirm");
      try {

          sessionV2.getOrderQueryV2().unsubscribeOrderStatusForFirm(cmiOrderStatusConsumer);
      } catch (Exception e)
      {
          e.printStackTrace();
      }
      System.out.println("unsubscribeOrderStatusForFirm done");

  }

  private static void unsubscribeOrderStatusForProduct(int productKey, CMIOrderStatusConsumer cmiOrderStatusConsumer)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling unsubscribeOrderStatusForProduct");
//      try {
//
//          sessionV2.getOrderQueryV2().unsubscribeOrderStatusForProduct();
//      } catch (Exception e)
//      {
//          e.printStackTrace();
//      }
      System.out.println("unsubscribeOrderStatusForFirm NOT IMPLEMENTED");

  }

  private static void unsubscribeOrderStatusForSession(String session, CMIOrderStatusConsumer cmiOrderStatusConsumer)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      System.out.println("calling unsubscribeOrderStatusForSession");
      try {

          sessionV2.getOrderQueryV2().unsubscribeOrderStatusForSession(session, cmiOrderStatusConsumer);
      } catch (Exception e)
      {
          e.printStackTrace();
      }
      System.out.println("unsubscribeOrderStatusForSession done");

  }

    private static void acceptCrossingOrder(OrderEntryStruct orderEntryStruct, OrderEntryStruct orderEntryStruct1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        System.out.println("calling acceptCrossingOrder");
        try {

            session.getOrderEntry().acceptCrossingOrder(orderEntryStruct, orderEntryStruct1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptCrossingOrder done");

    }

    private static OrderIdStruct acceptOrder(OrderEntryStruct orderEntryStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        System.out.println("calling acceptOrder");
        try {

            return session.getOrderEntry().acceptOrder(orderEntryStruct);
        }
        catch (DataValidationException de)
        {
            System.out.println(de.details);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptOrder done");
        return new OrderIdStruct();

    }

    private static OrderIdStruct acceptOrderByProductName(ProductNameStruct productNameStruct, OrderEntryStruct orderEntryStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        System.out.println("calling acceptOrderByProductName");
        try {

            return session.getOrderEntry().acceptOrderByProductName(productNameStruct, orderEntryStruct);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptOrderByProductName done");
        return new OrderIdStruct();

    }

    private static OrderIdStruct acceptOrderCancelReplaceRequest(CancelRequestStruct cancelRequestStruct, OrderEntryStruct orderEntryStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("calling acceptOrderCancelReplaceRequest");
        try {

            return session.getOrderEntry().acceptOrderCancelReplaceRequest(cancelRequestStruct, orderEntryStruct);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptOrderCancelReplaceRequest done");
        return new OrderIdStruct();
    }

    private static void acceptOrderCancelRequest(CancelRequestStruct cancelRequestStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("calling acceptOrderCancelRequest");
        try {

            session.getOrderEntry().acceptOrderCancelRequest(cancelRequestStruct);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptOrderCancelRequest done");


    }

    private static void acceptOrderUpdateRequest(int i, OrderEntryStruct orderEntryStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("calling acceptOrderUpdateRequest");
        try {

            session.getOrderEntry().acceptOrderUpdateRequest(i, orderEntryStruct);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptOrderUpdateRequest done");

    }

    private static void acceptRequestForQuote(RFQEntryStruct rfqEntryStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("calling acceptRequestForQuote");
        try {

            session.getOrderEntry().acceptRequestForQuote(rfqEntryStruct);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptRequestForQuote done");

    }

    private static OrderIdStruct acceptStrategyOrder(OrderEntryStruct orderEntryStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        System.out.println("calling acceptStrategyOrder");
        try {

            return session.getOrderEntry().acceptStrategyOrder(orderEntryStruct, legOrderEntryStructs);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptStrategyOrder done");
        return new OrderIdStruct();

    }

    private static OrderIdStruct acceptStrategyOrderCancelReplaceRequest(CancelRequestStruct cancelRequestStruct, OrderEntryStruct orderEntryStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("calling acceptStrategyOrderCancelReplaceRequest");
        try {

            return session.getOrderEntry().acceptStrategyOrderCancelReplaceRequest(cancelRequestStruct, orderEntryStruct, legOrderEntryStructs);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptStrategyOrderCancelReplaceRequest done");
        return new OrderIdStruct();

    }

    private static void acceptStrategyOrderUpdateRequest(int i, OrderEntryStruct orderEntryStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("calling acceptStrategyOrderUpdateRequest");
        try {

            session.getOrderEntry().acceptStrategyOrderUpdateRequest(i, orderEntryStruct, legOrderEntryStructs);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptStrategyOrderUpdateRequest done");
    }



    private static RFQStruct makeRFQ()
    {
        RFQStruct newRFQ = (RFQStruct) ReflectiveStructBuilder.newStruct(RFQStruct.class);
        newRFQ.sessionName = sessionName;
		newRFQ.productKeys.productKey = productKey;
		newRFQ.productKeys.classKey = classKey;
		newRFQ.productKeys.productType = productType;
		newRFQ.quantity = 10;
		newRFQ.timeToLive = 30;
        return newRFQ;
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

}
