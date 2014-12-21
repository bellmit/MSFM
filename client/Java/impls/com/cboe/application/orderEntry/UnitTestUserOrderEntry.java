package com.cboe.application.orderEntry;

import com.cboe.application.session.*;
import com.cboe.application.shared.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiTraderActivity.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.product.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.util.*;
import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.application.test.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.idl.cmi.*;
import junit.framework.*;
import com.cboe.domain.util.OrderStructBuilder;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

public class UnitTestUserOrderEntry extends junit.framework.TestCase
{
    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static CMIClassStatusConsumer theClassConsumer;
    protected static CMIProductStatusConsumer theProductConsumer;
    protected static TestCallback callbackConsumer;

    static SessionProfileUserStruct user = null;
    static OrderEntry orderEntry = null;
    static OrderQuery orderQuery = null;
    static Quote      quote = null;
    static OrderDetailStruct order = null;
    static OrderIdStruct orderId = null;
    static OrderEntryStruct orderEntryStruct = null;
    static RFQStruct rfq = null;
    static RFQEntryStruct rfqEntry = null;

    private static int      productKey = 6; // default
    private static int      productKey2 = 7;

    private static int[]    productKeys;
    private static final String SESSION_NAME = "W_AM1";

    private static int      classKey = 1;   // default
    private static int[]    classKeys;
    private static String   memberKey;
    protected static boolean remote = true;

    public UnitTestUserOrderEntry(String name) {
        super(name);
    }
   protected static void initUserSession()
    {
        try {
            if ( session == null )
            {
                UserLogonStruct logonStruct = new UserLogonStruct("sbtUser", "", "2.0", LoginSessionModes.STAND_ALONE_TEST);
                if ( remote )
                {
                    UserAccess userAccess =  TestUserAccessFactory.find();
                    session = userAccess.logon(logonStruct, LoginSessionTypes.PRIMARY, userSessionListener, true);
                }
                else
                {
                    com.cboe.interfaces.application.UserAccessHome home = (com.cboe.interfaces.application.UserAccessHome)HomeFactory.getInstance().findHome(com.cboe.interfaces.application.UserAccessHome.HOME_NAME);
                    com.cboe.interfaces.application.UserAccess userAccess = (com.cboe.interfaces.application.UserAccess)home.find();
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

    /*
     * initializes the callback consumer objects
     */
     protected static void initializeCallbacks()
    {
        callbackConsumer = new TestCallback();
        try {
            UserSessionAdminConsumerDelegate sessionListener = new UserSessionAdminConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object sessionObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(sessionListener);
            userSessionListener = CMIUserSessionAdminHelper.narrow(sessionObject);

            ClassStatusConsumerDelegate classListener = new ClassStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object classObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(classListener);
            theClassConsumer = CMIClassStatusConsumerHelper.narrow(classObject);

            ProductStatusConsumerDelegate productListener = new ProductStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object productObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(productListener);
            theProductConsumer = CMIProductStatusConsumerHelper.narrow(productObject);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    protected void setUp()
    {
        ExchangeFirmStruct firm = com.cboe.domain.util.StructBuilder.buildExchangeFirmStruct("CBOE", "SBT");
        orderId = UnitTestHelper.createOrderIdStruct(firm, "CHGO", UnitTestHelper.positiveRandomInt(), "ABC123", "19990909");
        orderEntryStruct = UnitTestHelper.createOrderEntryStruct(SESSION_NAME, productKey, memberKey, orderId);
        orderId = OrderStructBuilder.buildOrderIdStruct(orderEntryStruct);
        rfqEntry = UnitTestHelper.createRFQEntryStruct(SESSION_NAME, productKey, 100);
    }

    protected static void initStructs() {
        try
        {
            user = session.getValidSessionProfileUser();
            memberKey = user.userId;
            orderEntry = session.getOrderEntry();
            orderQuery = session.getOrderQuery();
            quote = session.getQuote();

            ClassStruct ibmProductClass = session.getProductQuery().getClassBySymbol(ProductTypes.OPTION, "IBM");
            ProductStruct[] ibmProducts = session.getProductQuery().getProductsByClass(ibmProductClass.classKey);
            if ( ibmProducts.length > 1)
            {
                productKey = ibmProducts[0].productKeys.productKey;
                productKey2 = ibmProducts[1].productKeys.productKey;
            }
            /*
            short[] types = {ProductTypes.OPTION};
            ClassStruct[] classes = session.getProductQuery().getProductClasses(ProductTypes.OPTION,false,theClassConsumer);
            // init class key
            if (classes.length > 1 )
            {
                classKey = classes[1].classKey;
                ProductStruct[] tempProduct = session.getProductQuery().getProducts(classKey, false, theProductConsumer);

                if ( tempProduct.length > 1)
                {
                    productKey = tempProduct[0].productKeys.productKey;
                    productKey2 = tempProduct[1].productKeys.productKey;
                }
            }
            */

            ////////class keys
            classKeys = new int[1];
            classKeys[0] = ibmProductClass.classKey;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new UnitTestUserOrderEntry("testAcceptOrder"));
        suite.addTest(new UnitTestUserOrderEntry("testAcceptOrderByProductName"));
        suite.addTest(new UnitTestUserOrderEntry("testAcceptOrderCancelReplaceRequest"));
        suite.addTest(new UnitTestUserOrderEntry("testAcceptOrderCancelRequest"));
        suite.addTest(new UnitTestUserOrderEntry("testAcceptOrderUpdateRequest"));
        suite.addTest(new UnitTestUserOrderEntry("testRequestForQuote"));
        suite.addTest(new UnitTestUserOrderEntry("testAcceptCrossing"));
        suite.addTest(new UnitTestUserOrderEntry("testGetPendingAdjustmentOrdersForClasses"));
        suite.addTest(new UnitTestUserOrderEntry("testGetPendingAdjustmentOrdersForProduct"));
        return suite;
    }

    public static void main(String[] args) {
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

            String[] testArgs = {UnitTestUserOrderEntry.class.getName()};
            junit.ui.TestRunner.main(testArgs);
        } catch (Exception e) {
            System.out.println(e);
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {
            }
        }
    }

    public void testAcceptOrder()
     throws SystemException, CommunicationException, NotFoundException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        System.out.println("\n<======== Testing acceptOrder() ========>");
        assertTrue("TestOrderEntryStruct", false==ReflectiveStructTester.testNullStruct(orderEntryStruct));

        // attempt to send the order.
        orderId = orderEntry.acceptOrder(orderEntryStruct);
        ActivityHistoryStruct activities = orderQuery.queryOrderHistory(orderId);
        ReflectiveStructTester.printStruct(activities, "Activities: " + activities.activityRecords.length);

         // get the order from the order handling service.
        order = orderQuery.getOrderById(orderId);

        assertTrue("TestOrderStruct", false==ReflectiveStructTester.testNullStruct(order));

        CancelRequestStruct cancelRequest = new CancelRequestStruct(orderId
                                                                    , SESSION_NAME
                                                                    , order.orderStruct.userAssignedId + ":CANCEL"
                                                                    , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                                    , getRemainingQuantity(orderQuery.getOrderById(orderId))
                                                                    );
        orderEntry.acceptOrderCancelRequest(cancelRequest);
     }


    public void testAcceptOrderByProductName()
     throws SystemException, CommunicationException, NotFoundException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        System.out.println("\n<======== Testing acceptOrderByProductName() ========>");
        assertTrue("TestOrderEntryStruct", false==ReflectiveStructTester.testNullStruct(orderEntryStruct));

        // attempt to send the order.
        com.cboe.idl.cmiProduct.ProductNameStruct productName = session.getProductQuery().getProductNameStruct(productKey);
        orderId = orderEntry.acceptOrderByProductName(productName, orderEntryStruct);
        ActivityHistoryStruct activities = orderQuery.queryOrderHistory(orderId);
        ReflectiveStructTester.printStruct(activities, "Activities: " + activities.activityRecords.length);

         // get the order from the order handling service.
        order = orderQuery.getOrderById(orderId);

        assertTrue("TestOrderStruct", false==ReflectiveStructTester.testNullStruct(order));

        CancelRequestStruct cancelRequest = new CancelRequestStruct(orderId
                                                                    , SESSION_NAME
                                                                    , order.orderStruct.userAssignedId + ":CANCEL"
                                                                    , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                                    , getRemainingQuantity(orderQuery.getOrderById(orderId))
                                                                    );
        orderEntry.acceptOrderCancelRequest(cancelRequest);
     }


    public void testAcceptOrderCancelReplaceRequest()
            throws SystemException, CommunicationException, NotFoundException, AlreadyExistsException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("\n<======== Testing acceptOrderCancelReplaceRequest() ========>");
        assertTrue("TestOrderEntryStruct", false==ReflectiveStructTester.testNullStruct(orderEntryStruct));
       // send the order.
        orderId = orderEntry.acceptOrder(orderEntryStruct);

        // create a replace OrderEntryStruct
        ExchangeFirmStruct firm = com.cboe.domain.util.StructBuilder.buildExchangeFirmStruct("CBOE", "SBT");
        OrderIdStruct newOrderId = UnitTestHelper.createOrderIdStruct(firm, "CHGO", UnitTestHelper.positiveRandomInt(), "ABC123", "1999/09/09");
        //OrderEntryStruct newOrder = UnitTestHelper.createOrderEntryStruct(6, "sbtUser", newOrderId);
        OrderEntryStruct newOrder = UnitTestHelper.createOrderEntryStruct(SESSION_NAME, productKey, "sbtUser", newOrderId);
        newOrderId = OrderStructBuilder.buildOrderIdStruct(newOrder);

        newOrder.originalQuantity = 100;

        // get the order from the order handling service.
        order = orderQuery.getOrderById(orderId);

        CancelRequestStruct cancelRequestStruct = new CancelRequestStruct(orderId
                                                                    , SESSION_NAME
                                                                    , order.orderStruct.userAssignedId + ":CANCEL"
                                                                    , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                                    , getRemainingQuantity(order.orderStruct)
                                                                    );

        // attempt to cancel replace the order.
        newOrderId = orderEntry.acceptOrderCancelReplaceRequest(cancelRequestStruct, newOrder);
        ActivityHistoryStruct activities = orderQuery.queryOrderHistory(newOrderId);
        ReflectiveStructTester.printStruct(activities, "Cancel Replace new Order Actvities: " + activities.activityRecords.length);

        activities = orderQuery.queryOrderHistory(orderId);
        ReflectiveStructTester.printStruct(activities, "Cancel Replace Old order Actvities: " + activities.activityRecords.length);

        // get the order from the order handling service.
        order = orderQuery.getOrderById(newOrderId);
        assertTrue("TestOrderStruct", false==ReflectiveStructTester.testNullStruct(order));

        // test the cancel replace operation results.
        assertTrue("The replacement order contains the incorrect quantity.", order.orderStruct.originalQuantity == 100);
        CancelRequestStruct cancelRequest = new CancelRequestStruct(newOrderId
                                                                    , SESSION_NAME
                                                                    , order.orderStruct.userAssignedId + ":CANCEL"
                                                                    , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                                    , getRemainingQuantity(order.orderStruct)
                                                                    );
        orderEntry.acceptOrderCancelRequest(cancelRequest);
        activities = orderQuery.queryOrderHistory(newOrderId);
        ReflectiveStructTester.printStruct(activities, "Cancel Request Actvities: " + activities.activityRecords.length);
   }

    public void testAcceptOrderCancelRequest()
            throws SystemException, CommunicationException, AlreadyExistsException, NotFoundException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("\n<======== Testing acceptOrderCancelRequest() ========>");
        // send the order.
        orderId = orderEntry.acceptOrder(orderEntryStruct);

        // attempt to cancel 10 from the order.
        CancelRequestStruct cancelRequest = new CancelRequestStruct(orderId
                                                                    , SESSION_NAME
                                                                    , order.orderStruct.userAssignedId + ":CANCEL"
                                                                    , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                                    , 10
                                                                    );
        orderEntry.acceptOrderCancelRequest(cancelRequest);

        // get the order from the order handling service.
        // get the order from the order handling service.
        order = orderQuery.getOrderById(orderId);
        assertTrue("TestOrderStruct", false==ReflectiveStructTester.testNullStruct(order));

        // **cannot do this due to the order fill execution changes on different product.
        // test the cancel replace operation results.
        assertTrue("The canceled quantity is incorrent.", order.orderStruct.cancelledQuantity == 10);
        assertTrue("The current remaining quantity is incorrent", 15 == getRemainingQuantity(order.orderStruct));
        cancelRequest = new CancelRequestStruct(orderId
                                                , SESSION_NAME
                                                , order.orderStruct.userAssignedId + ":CANCEL"
                                                , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                , getRemainingQuantity(order.orderStruct)
                                                );
        orderEntry.acceptOrderCancelRequest(cancelRequest);
    }

    public void testAcceptOrderUpdateRequest()
            throws SystemException, CommunicationException, AlreadyExistsException, NotFoundException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("\n<======== Testing acceptOrderUpdateRequest() ========>");
        // generate the updated order
        //OrderEntryStruct updatedOrder = UnitTestHelper.createOrderEntryStruct(6, "sbtUser", orderId);
         // send the order.
        orderId = orderEntry.acceptOrder(orderEntryStruct);
        OrderEntryStruct updatedOrder = UnitTestHelper.createOrderEntryStruct(SESSION_NAME, productKey, "sbtUser", orderId);
        updatedOrder.optionalData = "OPTIONALTEST";

         // get the order from the order handling service.
        order = orderQuery.getOrderById(orderId);

        System.out.println("===========>Original remaining:" + getRemainingQuantity(order.orderStruct));

        // attempt to update the order.
        orderEntry.acceptOrderUpdateRequest(getRemainingQuantity(order.orderStruct), updatedOrder);
        ActivityHistoryStruct activities = orderQuery.queryOrderHistory(orderId);
        ReflectiveStructTester.printStruct(activities, "Order Update Actvities: " + activities.activityRecords.length);

        // make sure the order events are processed in order query
        try
        {
            Thread.sleep(2000);
        }
        catch(Exception e)
        {
        }
        // get the order from the order handling service.
       // get the order from the order handling service.
        order = orderQuery.getOrderById(orderId);

        // test the update operation results.
        assertTrue("The updated requested optionalData", order.orderStruct.optionalData.equals("OPTIONALTEST"));
        CancelRequestStruct cancelRequest = new CancelRequestStruct(orderId
                                                                    , SESSION_NAME
                                                                    , order.orderStruct.userAssignedId + ":CANCEL"
                                                                    , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                                    , getRemainingQuantity(order.orderStruct)
                                                                    );
        orderEntry.acceptOrderCancelRequest(cancelRequest);
        activities = orderQuery.queryOrderHistory(orderId);
        ReflectiveStructTester.printStruct(activities, "Order Cancel Actvities: " + activities.activityRecords.length);
  }

    public void testRequestForQuote()
            throws SystemException, CommunicationException, NotFoundException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        System.out.println("\n<======== Testing acceptRequestForQuote() ========>");
        assertTrue("TestOrderEntryStruct", false==ReflectiveStructTester.testNullStruct(rfqEntry));
        orderEntry.acceptRequestForQuote(rfqEntry);
     }

    public void testGetPendingAdjustmentOrdersForClasses()
        throws SystemException, CommunicationException, AlreadyExistsException, NotAcceptedException, NotFoundException, TransactionFailedException,AuthorizationException, DataValidationException
    {
        System.out.println("\n<======== Testing getPendingAdjustmentOrdersForClasses() ========>");
        // send the order.
        orderEntry.acceptOrder(orderEntryStruct);

        // get the order from the order handling service.
       OrderDetailStruct order = orderQuery.getOrderById(orderId);
       System.out.println("the member logged in: " + user.userId);
       System.out.println("The Order member: " + order.orderStruct.userId);
       assertTrue("TestOrderDetailStruct", false==ReflectiveStructTester.testNullStruct(order));

        int[] classKeys = {order.orderStruct.classKey};

        // attempt to get pending order.
        PendingOrderStruct[] pendingOrders = orderQuery.getPendingAdjustmentOrdersByClass(SESSION_NAME, classKeys[0]);

        System.out.println("PendingOrders: " + pendingOrders.length);
        assertTrue("TestPendingOrders", false==ReflectiveStructTester.testNullStruct(pendingOrders));

        // assuming it is using the server emulator which will have pending products
        assertTrue("test pending order quantity incorrect for class", 0<pendingOrders.length);

        CancelRequestStruct cancelRequest = new CancelRequestStruct(orderId
                                                                    , SESSION_NAME
                                                                    , order.orderStruct.userAssignedId + ":CANCEL"
                                                                    , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                                    , getRemainingQuantity(order.orderStruct)
                                                                    );
        orderEntry.acceptOrderCancelRequest(cancelRequest);
    }

    public void testGetPendingAdjustmentOrdersForProduct()
        throws SystemException, CommunicationException, AlreadyExistsException, NotAcceptedException, NotFoundException, TransactionFailedException,AuthorizationException, DataValidationException
    {
        System.out.println("\n<======== Testing getPendingAdjustmentOrdersForProduct() ========>");
        // send the order.
        orderEntry.acceptOrder(orderEntryStruct);

        // get the order from the order handling service.
        OrderDetailStruct order = orderQuery.getOrderById(orderId);

        assertTrue("TestOrderDetailStruct", false==ReflectiveStructTester.testNullStruct(order));

        // attempt to get pending order.
        PendingOrderStruct[] pendingOrders = orderQuery.getPendingAdjustmentOrdersByProduct(SESSION_NAME, order.orderStruct.productKey);

        assertTrue("TestPendingOrdersForProduct", false==ReflectiveStructTester.testNullStruct(pendingOrders));
        // assuming it is using the server emulator which will have pending products
        assertTrue("test pending order quantity incorrect for product", 0<pendingOrders.length);

        CancelRequestStruct cancelRequest = new CancelRequestStruct(orderId
                                                                    , SESSION_NAME
                                                                    , order.orderStruct.userAssignedId + ":CANCEL"
                                                                    , OrderCancelTypes.DESIRED_CANCEL_QUANTITY
                                                                    , getRemainingQuantity(order.orderStruct)
                                                                    );
        orderEntry.acceptOrderCancelRequest(cancelRequest);
    }

    private int getRemainingQuantity(OrderDetailStruct order) {
        return getRemainingQuantity(order.orderStruct);
    }
    private int getRemainingQuantity(OrderStruct order) {
        return order.leavesQuantity;
    }

    public void testAcceptCrossing()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        System.out.println("\n<======== Testing acceptCrossingOrder() ========>");
        ExchangeFirmStruct firm = com.cboe.domain.util.StructBuilder.buildExchangeFirmStruct("CBOE", "SBT");
        OrderIdStruct orderId1 = UnitTestHelper.createOrderIdStruct(firm, "CHGO", UnitTestHelper.positiveRandomInt(), "ABC123", "19990909");
        OrderEntryStruct orderEntryStruct1 = UnitTestHelper.createOrderEntryStruct(SESSION_NAME, productKey, memberKey, orderId1);
        orderEntryStruct1.side = Sides.BUY;

        OrderIdStruct orderId2 = UnitTestHelper.createOrderIdStruct(firm, "CHGO", UnitTestHelper.positiveRandomInt(), "ABC123", "19990909");
        OrderEntryStruct orderEntryStruct2 = UnitTestHelper.createOrderEntryStruct(SESSION_NAME, productKey, memberKey, orderId2);
        orderEntryStruct2.side = Sides.SELL;

        orderEntry.acceptCrossingOrder(orderEntryStruct1, orderEntryStruct2);

   }
}
