package com.cboe.application.product;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiCallback.*;

import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.interfaces.businessServices.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;

import com.cboe.application.shared.*;
import com.cboe.application.test.*;

import com.cboe.domain.util.ReflectiveStructBuilder;

import com.cboe.infrastructureServices.foundationFramework.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

/**
 * A unit tester for the product query.
 *
 * @author Connie Liang
 */
public class UnitTestProductQuery extends junit.framework.TestCase
{
    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static CMIClassStatusConsumer theClassConsumer;
    protected static CMIProductStatusConsumer theProductConsumer;
    protected static TestCallback callbackConsumer;

    private static short productType = 0;
    private static int[] productKeys = {6, 7};
    private static int   classKey = 4;

    private static ProductNameStruct    productNameStruct;
    private static ProductStruct        productStruct;

    private static RemoteConnection     remoteConnection;
    private static ProductQueryService  productQueryService;

    private static SessionProfileUserStruct validUserStruct;

    private static int strategyClassKey = 8;
    private static int strategyKey = 10;
    private static int strategyComponentKey = 6;
    protected static boolean remote = true;

    /**
     * UnitTestProductQuery constructor comment.
     * @param name java.lang.String
     */
    public UnitTestProductQuery(String name)
    {
        super(name);
    }

    private static void initStructs()
    {
        try
        {
            /////// user struct
            validUserStruct = session.getValidSessionProfileUser();

            ClassStruct[] classes = null;

            short[] types = {ProductTypes.OPTION};
            classes = session.getProductQuery().getProductClasses(ProductTypes.OPTION);

            // init class key for OPTION class and products
            if (classes.length > 1 )
            {
                classKey = classes[0].classKey;
                ProductStruct[] tempProduct = session.getProductQuery().getProductsByClass(classKey);

                if ( tempProduct.length > 1)
                {
                    productKeys = new int[2];
                    productKeys[0] = tempProduct[0].productKeys.productKey;
                    productKeys[1] = tempProduct[1].productKeys.productKey;
                    strategyComponentKey = productKeys[0];

//                    productStruct = tempProduct[0];
//                    productNameStruct = tempProduct[0].productName;

                }

            }

            types[0] = ProductTypes.STRATEGY;

            ClassStruct[] productClasses = session.getProductQuery().getProductClasses(ProductTypes.STRATEGY);
            //ProductClassStruct []productClasses = ServicesHelper.getProductQueryService().getProductClassesByType(types, false, true, true);

            // init class key for OPTION class and products
            if (productClasses.length > 1 )
            {
                strategyClassKey = productClasses[1].classKey;

                ProductStruct[] tempProduct = session.getProductQuery().getProductsByClass(strategyClassKey);

                if ( tempProduct.length > 1)
                {
                    strategyKey = tempProduct[0].productKeys.productKey;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

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
            org.omg.CORBA.Object orbObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(sessionListener);
            userSessionListener = CMIUserSessionAdminHelper.narrow(orbObject);

            ClassStatusConsumerDelegate classListener = new ClassStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object classObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(classListener);
            theClassConsumer = CMIClassStatusConsumerHelper.narrow(classObject);

            ProductStatusConsumerDelegate productListener = new ProductStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object productObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(productListener);
            theProductConsumer = CMIProductStatusConsumerHelper.narrow(productObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
   }

    /**
     * Initializes foundation framework to start up CAS simulator
     * when runs in process test
     *
     * @author Connie Liang
     */
    private static void initEnv()
    {
        if(!remote)
        {
            UnitTestHelper.initFFEnv();
        }
    }

    /**
     * Runs the unit test.
     * Example to run remote: java -DREMOTE=true com.cboe.aplication.product.UnitTestProductQuery
     * Example to run in process: java com.cboe.aplication.product.UnitTestProductQuery
     * @param args[]
     */
    public static void main(String args[])
    {
        java.util.Properties prop = System.getProperties();
        String parameter =(String)prop.get("REMOTE");

        if (parameter != null && parameter.equalsIgnoreCase("TRUE"))
        {
            System.out.println("============> The Test is configure to connect to CAS remotely");
            remote = true;
        }
        try
        {
            initConnection(args);
            initEnv();
            initializeCallbacks();
            initUserSession();
            initStructs();

            String[] testArgs = {UnitTestProductQuery.class.getName()};
            junit.ui.TestRunner.main(testArgs);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Initialize testing, only needs to be performed once.
     *
     * @author Connie Liang
     */
    public void setUp()
    {
    }
    /**
     * Returns a set of unit tests.
     *
     * @return suite of unit tests
     *
     * @author Connie Liang
     */
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTest(new UnitTestProductQuery("testGetProductTypes"));
        suite.addTest(new UnitTestProductQuery("testGetProductClasses"));
        suite.addTest(new UnitTestProductQuery("testGetProductClasses"));
        suite.addTest(new UnitTestProductQuery("testGetProductNameStruct"));
        suite.addTest(new UnitTestProductQuery("testIsValidProductName"));
        suite.addTest(new UnitTestProductQuery("testGetProductByName"));
        suite.addTest(new UnitTestProductQuery("testGetProducts"));
        suite.addTest(new UnitTestProductQuery("testGetProductNameStruct"));
        suite.addTest(new UnitTestProductQuery("testGetProductClassByKey"));
        suite.addTest(new UnitTestProductQuery("testGetProductByKey"));
        suite.addTest(new UnitTestProductQuery("testGetClassBySymbol"));
        suite.addTest(new UnitTestProductQuery("testGetAllPendingAdjustments"));
        suite.addTest(new UnitTestProductQuery("testGetPendingAdjustments"));
        suite.addTest(new UnitTestProductQuery("testGetPendingAdjustmentProducts"));
        suite.addTest(new UnitTestProductQuery("testGetStrategyByKey"));
        suite.addTest(new UnitTestProductQuery("testGetStrategiesByClass"));
        suite.addTest(new UnitTestProductQuery("testGetStrategiesByComponent"));
        suite.addTest(new UnitTestProductQuery("testAcceptStrategy"));

        return suite;
    }
    /**
     *
     * @author Connie Liang
     */
    public void tearDown()
    {
    }

    /**
     * Performs unit test on the <code>getProductTypes</code> method.
     *
     * @author Connie Liang
     */
    public void testGetProductTypes()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductTypeStruct[] productAllTypes = session.getProductQuery().getProductTypes(); //all, not only active for trading
        ProductTypeStruct[] productActiveTypes = session.getProductQuery().getProductTypes();

        assertTrue("ProductActiveType", productActiveTypes.length > 0);

        productType = productAllTypes[0].type;

        assertTrue("ProductAllTypes", productAllTypes.length > 0);
        assertTrue("test null value for productAllTypes", false==ReflectiveStructTester.testNullStruct(productAllTypes));
   }

    /**
     * Performs unit test on the <code>getProductClasses</code> method.
     *
     * @author Connie Liang
     */
    public void testGetProductClasses()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        System.out.println("ProductType: " + productType);
        ClassStruct[] classStructs = session.getProductQuery().getProductClasses(productType);
        System.out.println("Total Classes for type: " + productType + " is " + classStructs.length);
        assertTrue("test null value for classStructs", false==ReflectiveStructTester.testNullStruct(classStructs));
    }

    /**
     * Performs unit test on the <code>getProductNameStruct</code> method.
     *
     * @author Connie Liang
     */
    public void testGetProductNameStruct()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        productNameStruct = session.getProductQuery().getProductNameStruct(productKeys[0]);
        assertTrue("test null value for productNameStruct", false==ReflectiveStructTester.testNullStruct(productNameStruct));
  }

    /**
     * Performs unit test on the <code>getProductByName</code> method.
     *
     * @author Connie Liang
     */
    public void testGetProductByName()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        productStruct = session.getProductQuery().getProductByName(productNameStruct);
        assertTrue("test null value for ProductStruct", false==ReflectiveStructTester.testNullStruct(productStruct));
        assertTrue("test null value for ProductStruct", false==ReflectiveStructTester.testNullStruct(productNameStruct));
    }

    /**
     * Performs unit test on the <code>isValidProductName</code> method.
     *
     * @author Connie Liang
     */
    public void testIsValidProductName()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
           boolean valid = session.getProductQuery().isValidProductName(productNameStruct);

           assertTrue("testIsValidProductName", valid == true);
    }

    /**
     * Performs unit test on the <code>getProducts</code> method.
     *
     * @author Connie Liang
     */
    public void testGetProducts()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
           ProductStruct[] productStructs = session.getProductQuery().getProductsByClass(classKey);

           ReflectiveStructBuilder.printStruct(productStructs, "ProductStructs" );

           assertTrue("test null value for ProductStructs", false==ReflectiveStructTester.testNullStruct(productStructs));
    }

    /**
     * Performs unit test on the <code>getProductClassByKey</code> method.
     *
     * @author Connie Liang
     */
    public void testGetProductClassByKey()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        ClassStruct classStruct = session.getProductQuery().getClassByKey(classKey);
        ReflectiveStructTester.printStruct(classStruct, "getProductClassByKey");
        assertTrue("test null value for getProductClassByKey.classStruct", false==ReflectiveStructTester.testNullStruct(classStruct));
    }

    /**
     * Performs unit test on the <code>getProductByKey</code> method.
     *
     * @author Connie Liang
     */
    public void testGetProductByKey()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        ProductStruct productStruct = session.getProductQuery().getProductByKey(productKeys[0]);
        ReflectiveStructTester.printStruct(productStruct, "getProductByKey");
        assertTrue("test null value for getProductByKey.productStruct", false==ReflectiveStructTester.testNullStruct(productStruct));
    }

    /**
     * Performs unit test on the <code>getClassBySymbol</code> method.
     *
     * @author Connie Liang
     */
    public void testGetClassBySymbol()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        ClassStruct classStruct = session.getProductQuery().getClassBySymbol(ProductTypes.OPTION, "IBM" );
        ReflectiveStructTester.printStruct(classStruct, "getClassBySymbol: " + "IBM");
        assertTrue("test null value for getClassBySymbol.classStruct", false==ReflectiveStructTester.testNullStruct(classStruct));
    }

    /**
     * Performs unit test on the <code>getAllPendingAdjustments</code> method.
     *
     * @author Connie Liang
     */
    public void testGetAllPendingAdjustments()
          throws SystemException, CommunicationException, AuthorizationException
    {
        PendingAdjustmentStruct[] pendingInfo = session.getProductQuery().getAllPendingAdjustments();
        assertTrue("test null value for getAllPendingAdjustments.pendingInfo", false==ReflectiveStructTester.testNullStruct(pendingInfo));
        System.out.println("Total PendingAdjustment: " + pendingInfo.length);
    }

    /**
     * Performs unit test on the <code>getPendingAdjustments</code> method.
     *
     * @author Connie Liang
     */
    public void testGetPendingAdjustments()
          throws SystemException, CommunicationException, AuthorizationException,DataValidationException
    {
        PendingAdjustmentStruct[] pendingInfo = session.getProductQuery().getPendingAdjustments(classKey, true);
        assertTrue("test null value for getAllPendingAdjustments.pendingInfo", false==ReflectiveStructTester.testNullStruct(pendingInfo));
        System.out.println("Total PendingAdjustment for class: " + classKey + " is " + pendingInfo.length);
        ReflectiveStructTester.printStruct(pendingInfo, "getPendingAdjustments: " + "classKey");
    }

    /**
     * Performs unit test on the <code>getPendingAdjustments</code> method.
     *
     * @author Connie Liang
     */
    public void testGetPendingAdjustmentProducts()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        PendingNameStruct[] pendingName = session.getProductQuery().getPendingAdjustmentProducts(classKey);
        assertTrue("test null value for getPendingAdjustmentProducts.pendingInfo", false==ReflectiveStructTester.testNullStruct(pendingName));
        System.out.println("Total getPendingAdjustmentProducts for class: " + classKey + " is " + pendingName.length);
        ReflectiveStructTester.printStruct(pendingName, "getPendingAdjustmentProducts: " + "classKey");
    }

    public void testGetStrategyByKey()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        StrategyStruct strategy = session.getProductQuery().getStrategyByKey(strategyKey);
        assertTrue("test null value for getStrategyByKey", false==ReflectiveStructTester.testNullStruct(strategy));
        ReflectiveStructTester.printStruct(strategy, "strategy: " + "strategyKey");

    }
    public void testGetStrategiesByClass()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        StrategyStruct[] strategies = session.getProductQuery().getStrategiesByClass(strategyClassKey);
        assertTrue("test null value for getStrategiesByClass", false==ReflectiveStructTester.testNullStruct(strategies));
        ReflectiveStructTester.printStruct(strategies, "strategy: " + "strategyKey");

    }
    public void testGetStrategiesByComponent()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        StrategyStruct[] strategies = session.getProductQuery().getStrategiesByComponent(strategyComponentKey);
        assertTrue("test null value for getStrategiesByComponent", false==ReflectiveStructTester.testNullStruct(strategies));
        ReflectiveStructTester.printStruct(strategies, "strategy: " + "strategyKey");

    }

    public void testAcceptStrategy()
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
        StrategyRequestStruct request = UnitTestHelper.createStraddleStrategyRequestStruct(productKeys[0], productKeys[1]);

        SessionStrategyStruct newStategy = session.getProductDefinition().acceptStrategy("W_AM1", request);

        assertTrue("test null value for acceptStrategy", false==ReflectiveStructTester.testNullStruct(newStategy));
        ReflectiveStructTester.printStruct(newStategy, "strategy: " + newStategy.sessionProductStruct.productStruct.productKeys.productKey);

    }
}// EOF
