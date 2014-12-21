package com.cboe.application.order;


import com.cboe.application.shared.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiSession.*;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.domain.util.StructBuilder;

import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.application.test.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.OrderStructBuilder;

import java.util.Properties;
import java.io.FileInputStream;

/**
 * The test methods contained in this class all depend on several assumtions with regard to how
 * the order manager order collections are initialized.  The test methods assume there is an
 * order for every single product the product service contains (for a given type).  If this is changed the
 * test assumptions also need to be changed.
 * The tests currently only test option products.
 * @author Thomas Lynch
 */
public class UnitTestUserOrderQuery extends junit.framework.TestCase
{
    protected static RemoteConnection connection;
    protected static UserSessionManagerV3 session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static CMIClassStatusConsumer theClassConsumer;
    protected static CMIProductStatusConsumer theProductConsumer;
    protected static TestCallback callbackConsumer;
    protected static CMIOrderStatusConsumer cmiOrderStatusConsumer;
    private static com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer orderV2Consumer;

    protected static SessionProfileUserStruct user = null;
    protected static OrderEntry orderEntry = null;
    protected static OrderQuery orderQuery = null;
    protected static Quote      quote = null;
    protected static OrderDetailStruct order = null;
    protected static OrderIdStruct orderId = null;
    protected static OrderEntryStruct orderEntryStruct = null;
    private static String   memberKey;
    protected static boolean remote = true;

    private static int      productKey = 3; // default
    private static int      productKey2 = 6;
    //private static String   SESSION_NAME = "W_MAIN";

    private static int[]    productKeys;

    private static int      classKey = 1;   // default
    private static int[]    classKeys;


    public static final String SYSTEM_SECTION = "System";
    public static final String SETUP_SETTINGS = "Setup";
    public static final String USER = "User";
    public static final String PASSWORD = "Password";
    public static final String LOGIN_MODE = "LoginOperationMode";
    public static final String LOGIN_SESSION = "SessionLoginType";
    public static final String SESSION_NAME = "SessionName";
    public static final String PRODUCT_TYPE = "ProductType";
    public static final String CLASS_SYMBOL = "ClassSymbol";
    public static final String CLASS_SYMBOL_2= "ClassSymbol2";
    public static final String GMD = "gmd";
    public static final String PROPERTY_FILE = "PropertyFile";
    private static Properties properties;
    private static String sessionName;

    public UnitTestUserOrderQuery(String name) {
        super(name);
    }

   protected static void initUserSession()
    {
        try {
            if ( session == null )
            {
                // UserLogonStruct logonStruct = new UserLogonStruct("sbtUser", "", "2.0", LoginSessionModes.STAND_ALONE_TEST);
                UserLogonStruct logonStruct = new UserLogonStruct(
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

                if ( remote )
                {
                    UserAccessV3 userAccess =  TestUserAccessV3Factory.find();
                    session = userAccess.logon(logonStruct, LoginSessionTypes.PRIMARY, userSessionListener, true);
                }
                else
                {
                    com.cboe.interfaces.application.UserAccessV3Home home = (com.cboe.interfaces.application.UserAccessV3Home)HomeFactory.getInstance().findHome(com.cboe.interfaces.application.UserAccessHome.HOME_NAME);
                    com.cboe.interfaces.application.UserAccessV3 userAccess = (com.cboe.interfaces.application.UserAccessV3)home.find();
                    session = userAccess.logon(logonStruct, LoginSessionTypes.PRIMARY, userSessionListener, true);
                    session.authenticate(logonStruct);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static void initConnection(String[] args)
    {
        if ( connection == null )
        {
            connection = RemoteConnectionFactory.create(args);
            System.out.println("Connection established " + connection);
        }
    }
    protected static void initStructs() {
        try
        {
            user = session.getValidSessionProfileUser();
            memberKey = user.userId;
            orderEntry = session.getOrderEntry();
            orderQuery = session.getOrderQuery();
            quote = session.getQuote();
            sessionName = properties.getProperty(SESSION_NAME);
            short[] types = {ProductTypes.OPTION};
            SessionClassStruct[] classes = session.getTradingSession().getClassesForSession(sessionName, ProductTypes.OPTION,theClassConsumer);

            // init class key
            if (classes.length > 1 )
            {
                classKey = classes[1].classStruct.classKey;
                SessionProductStruct[] tempProduct = session.getTradingSession().getProductsForSession(sessionName, classKey, theProductConsumer);

                if ( tempProduct.length > 1)
                {
                    productKey = tempProduct[0].productStruct.productKeys.productKey;
                    productKey2 = tempProduct[1].productStruct.productKeys.productKey;
                }
            }

            ////////class keys
            classKeys = new int[1];
            classKeys[0] = classKey;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     * initializes the callback consumer objects
     */
    protected static void initializeCallbacks()
    {
        try {
            callbackConsumer = new TestCallback();
            UserSessionAdminConsumerDelegate sessionListener = new UserSessionAdminConsumerDelegate(callbackConsumer);

            org.omg.CORBA.Object sessionObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(sessionListener);
            userSessionListener = CMIUserSessionAdminHelper.narrow(sessionObject);

            ClassStatusConsumerDelegate classListener = new ClassStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object classObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(classListener);
            theClassConsumer = CMIClassStatusConsumerHelper.narrow(classObject);

            ProductStatusConsumerDelegate productListener = new ProductStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object productObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(productListener);
            theProductConsumer = CMIProductStatusConsumerHelper.narrow(productObject);

            OrderStatusConsumerDelegate orderListener = new OrderStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object orderObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(orderListener);
            cmiOrderStatusConsumer = CMIOrderStatusConsumerHelper.narrow(orderObject);

            com.cboe.idl.cmiCallbackV2.POA_CMIOrderStatusConsumer_tie delegateV2= new OrderStatusV2ConsumerDelegate(callbackConsumer); // new com.cboe.idl.cmiCallbackV2.POA_CMIOrderStatusConsumer_tie(getCallback());
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateV2);
            orderV2Consumer = com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerHelper.narrow (corbaObject);

        }
        catch (Throwable e) {
            e.printStackTrace();
        }
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
     * Runs the test.
     */
    public static void main(String args[])
    {
        if ( System.getProperties().getProperty(PROPERTY_FILE) != null)
        {
            loadProperties(System.getProperties().getProperty(PROPERTY_FILE));
        }
        else
        {
            properties = System.getProperties();
        }

        java.util.Properties prop = System.getProperties();
        String unsubscribeStr =(String)prop.get("REMOTE");

        if (unsubscribeStr != null && unsubscribeStr.equalsIgnoreCase("TRUE"))
        {
            System.out.println("============> The Test is configure to connect to CAS remotely");
            remote = true;
        }
        try
        {
            initConnection(args);
            if(!remote)
            {
                UnitTestHelper.initFFEnv();
            }
            initializeCallbacks();
            initUserSession();
            initStructs();
            String[] testArgs = {UnitTestUserOrderQuery.class.getName()};
            junit.ui.TestRunner.main(testArgs);
        } catch (Exception e) {
            System.out.println(e);
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {
            }
        }
    }

    protected void setUp()
    {
        ExchangeFirmStruct firm = StructBuilder.buildExchangeFirmStruct("CBOE", "SBT");
        orderId = UnitTestHelper.createOrderIdStruct(firm, "CHGO", UnitTestHelper.positiveRandomInt(), "ABC123", "19990909");
        orderEntryStruct = UnitTestHelper.createOrderEntryStruct(sessionName,productKey, memberKey, orderId);
        orderId = OrderStructBuilder.buildOrderIdStruct(orderEntryStruct);
   }

    protected void tearDown() {
      }

    /**
      * Returns a set of unit tests.
      * @return suite of unit tests
      * @author Thomas Lynch
      */
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTest(new UnitTestUserOrderQuery("testAcceptOrderAndRetrieve"));
        suite.addTest(new UnitTestUserOrderQuery("testSubscribeOrderByClass"));

        return suite;
    }

    public void testSubscribeOrderByClass()
     throws SystemException, CommunicationException, NotFoundException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {

        session.getOrderQueryV2().subscribeOrderStatusForClassV2(classKey, orderV2Consumer, true, true);
        assertTrue("TestSubscribeOrderForClassV2", true);
        // attempt to send the order.
        orderId = orderEntry.acceptOrder(orderEntryStruct);


    }

    public void testAcceptOrderAndRetrieve()
     throws SystemException, CommunicationException, NotFoundException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
            assertTrue("TestOrderEntryStruct", false==ReflectiveStructTester.testNullStruct(orderEntryStruct));
            // attempt to send the order.
            orderId = orderEntry.acceptOrder(orderEntryStruct);

            // get the order from the order handling service.
            order = orderQuery.getOrderById(orderId);
            assertTrue("TestOrderStruct", false==ReflectiveStructTester.testNullStruct(order));

            OrderDetailStruct[] addedOrders = orderQuery.getOrdersForProduct(order.orderStruct.productKey);
            assertTrue("getOrdersForProduct", addedOrders.length > 0);

            addedOrders = orderQuery.getOrdersForClass(order.orderStruct.classKey);
            assertTrue("getOrdersForClass", addedOrders.length > 0);

            addedOrders = orderQuery.getOrdersForType(order.orderStruct.productType);
            assertTrue("getAllOrdersForType", addedOrders.length > 0);

            addedOrders = orderQuery.getOrdersForSession( sessionName);
            assertTrue("getOrdersForSession", addedOrders.length > 0);

            PendingOrderStruct[] pendingOrders = orderQuery.getPendingAdjustmentOrdersByClass(sessionName, order.orderStruct.classKey);
            assertTrue("getPendingAdjustmentOrdersByClass", addedOrders.length > 0);

            pendingOrders = orderQuery.getPendingAdjustmentOrdersByProduct(sessionName, order.orderStruct.productKey);
            assertTrue("getPendingAdjustmentOrdersByProduct", addedOrders.length > 0);


     }

}// end of class

