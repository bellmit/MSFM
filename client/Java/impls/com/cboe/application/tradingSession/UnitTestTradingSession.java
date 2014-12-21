package com.cboe.application.tradingSession;

import com.cboe.idl.cmi.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.application.shared.*;
import com.cboe.application.test.*;

import com.cboe.infrastructureServices.foundationFramework.*;

/**
 * A unit tester for the product query.
 *
 * @author Connie Liang
 */
public class UnitTestTradingSession extends junit.framework.TestCase
{
    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static CMIClassStatusConsumer theClassConsumer;
    protected static CMIProductStatusConsumer theProductConsumer;
    protected static CMITradingSessionStatusConsumer theTradingSessionStatusConsumer;
    protected static boolean remote = true;
    protected static TestCallback              callbackConsumer;
    public    static TradingSessionStruct[]    sessions;
    public    static ProductTypeStruct[]       productTypes;
    public    static SessionProductStruct[]    products;
    public    static SessionClassStruct[]      classes;
    protected  CMIStrategyStatusConsumer strategyStatusConsumer;

    private   static SessionProfileUserStruct validUserStruct;
    private   int[]  productKeys = {3, 6};
    private   int    classKey = 1;




    /**
     * UnitTestProductQuery constructor comment.
     * @param name java.lang.String
     */
    public UnitTestTradingSession(String name)
    {
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
        try
        {
            if (remote)
            {
                UserSessionAdminConsumerDelegate sessionDelegate = new UserSessionAdminConsumerDelegate(callbackConsumer);
                org.omg.CORBA.Object orbObject = (org.omg.CORBA.Object) connection.register_object(sessionDelegate);
                userSessionListener = CMIUserSessionAdminHelper.narrow(orbObject);

                TradingSessionStatusConsumerDelegate statusDelegate = new TradingSessionStatusConsumerDelegate(callbackConsumer);
                org.omg.CORBA.Object statusObject = (org.omg.CORBA.Object) connection.register_object(statusDelegate);
                theTradingSessionStatusConsumer = CMITradingSessionStatusConsumerHelper.narrow(statusObject);
            }
        }
        catch (Exception e)
        {
            System.out.println ("CORBA object not found, servant not active");
            e.printStackTrace();
        }
    }
    private static void initStructs()
             throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
      /////// user struct
      validUserStruct = session.getValidSessionProfileUser();
    }

    /**
     * Initializes foundation framework.
     * @author Connie Feng
     */
    protected static void initEnv()
    {
        if(!remote)
        {
            UnitTestHelper.initFFEnv();
        }
    }

    /**
     *
     * Runs the unit test.
     */
    public static void main(String args[])
    {
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
            initEnv();
            initializeCallbacks();
            initUserSession();
            initStructs();

            String[] testArgs = {UnitTestTradingSession.class.getName()};
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
        suite.addTest(new UnitTestTradingSession("testGetCurrentTradingSessions"));
        suite.addTest(new UnitTestTradingSession("testGetClassesForTradingSession"));
        suite.addTest(new UnitTestTradingSession("testGetProductClassesForSession"));
        suite.addTest(new UnitTestTradingSession("testGetProductTypesForSession"));
        suite.addTest(new UnitTestTradingSession("testGetStrategiesByClassForSession"));
        return suite;
    }
    /**
     *
     * @author Connie Liang
     */
    public void tearDown()
    {
    }

    /****************************************************************************************/
    /*                             TESTING METHODS                                          */
    /****************************************************************************************/

    /**
    * @description Retrieves current trading sessions and subscribes the user for subsequent trading session updates.
    * @param clientListener  EventChannelListener object to receive trading session
    * updates
    */
    public void testGetCurrentTradingSessions()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        sessions = session.getTradingSession().getCurrentTradingSessions(theTradingSessionStatusConsumer);
        assertTrue("test null value for TradingSessionStruct", false==ReflectiveStructTester.testNullStruct(sessions));

        // Print out results
        System.out.println("Got " + sessions.length + " sessions");
        for (int i=0; i<sessions.length; i++)
        {
            System.out.println("  session[" + i + "].sessionName: "  + sessions[i].sessionName);
        }
    }

    /**
    * @description Retrieves classes for current trading session.
    *
    * Pre-conditions:
    *   1) sessionName must have a value.
    *
    */
    public void testGetClassesForTradingSession()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        assertTrue("test null value for sessionName", false==(sessions[0].sessionName == null));

        System.out.println("\nTesting getClassesForTradingSession ====>");

/*        SessionClassDetailStruct[] classDetails = session.getTradingSession().getClassDetailForTradingSession( sessions[0].sessionName );
        assertTrue("test null value for SessionClassDetailStruct", false==ReflectiveStructTester.testNullStruct(classDetails));

        // Print out results
        System.out.println("Got " + classDetails.length + " classes for session " + sessions[0].sessionName);
        for (int i=0; i < classDetails.length; i++)
        {
            System.out.println("  class[" + i + "]: " + classDetails[i].classDetail.classStruct.classSymbol);
        }*/
    }



    /**
    * @description Retrieves product classes for current trading session.
    *              Also tests getProductsForSession.
    * Pre-conditions:
    *   1) sessionName must have a value.
    *
    */
    public void testGetProductClassesForSession()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        assertTrue("test null value for sessionName", false==(sessions[0].sessionName == null));

        System.out.println("\nTesting getProductClassesForSession ====>");

        ClassStatusConsumerDelegate classDelegate = new ClassStatusConsumerDelegate(new TestCallback());
        org.omg.CORBA.Object orbObject = (org.omg.CORBA.Object) connection.register_object(classDelegate);
        theClassConsumer = CMIClassStatusConsumerHelper.narrow(orbObject);

        classes = session.getTradingSession().getClassesForSession(sessions[0].sessionName, ProductTypes.OPTION, theClassConsumer);

        // Print out results
        System.out.println("Got " + classes.length + " product classes for session " + sessions[0].sessionName);
        for (int i=0; i < classes.length; i++)
        {
            products = session.getTradingSession().getProductsForSession(sessions[0].sessionName, classes[i].classStruct.classKey, theProductConsumer);
            System.out.println("  Got " + products.length + " products for session " + sessions[0].sessionName + " and classKey " + classes[i].classStruct.classKey);
            for (int j=0; j < products.length; j++)
            {
               System.out.println("    Product[" + j + "].productKey: " + products[j].productStruct.productKeys.productKey);
            }
        }
    }


    /**
    * @description Retrieves product types for current trading session.
    *              Also tests getProductsForSession.
    * Pre-conditions:
    *   1) sessionName must have a value.
    *
    */
     public void testGetProductTypesForSession()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        assertTrue("test null value for sessionName", false==(sessions[0].sessionName == null));

        System.out.println("\nTesting getProductTypesForSession ====>");

        ClassStatusConsumerDelegate classDelegate = new ClassStatusConsumerDelegate(new TestCallback());
        org.omg.CORBA.Object orbObject = (org.omg.CORBA.Object) connection.register_object(classDelegate);
        theClassConsumer = CMIClassStatusConsumerHelper.narrow(orbObject);

        ProductTypeStruct[] types = session.getTradingSession().getProductTypesForSession(sessions[0].sessionName);

        // Print out results
        System.out.println("Got " + types.length + " product types for session " + sessions[0].sessionName);
        for (int i=0; i < types.length; i++)
        {
            products = session.getTradingSession().getProductsForSession(sessions[0].sessionName, classes[i].classStruct.classKey, theProductConsumer);
            System.out.println( "  ProductTypes[" + i + "].type: " + types[i].type +  " name: " +  types[i].name);
        }
    }


    /**
    * @description Retrieves strategies for current trading session.
    *              Also tests getProductsForSession.
    * Pre-conditions:
    *   1) sessionName must have a value.
    *
    */
     public void testGetStrategiesByClassForSession()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        assertTrue("test null value for sessionName", false==(sessions[0].sessionName == null));

        System.out.println("\nTesting getStrategiesByClassForSession ====>");

        StrategyStatusConsumerDelegate strategyDelegate = new StrategyStatusConsumerDelegate(callbackConsumer);
        org.omg.CORBA.Object orbObject = (org.omg.CORBA.Object) connection.register_object(strategyDelegate);
        strategyStatusConsumer = CMIStrategyStatusConsumerHelper.narrow(orbObject);

//       SessionClassDetailStruct[] classes = session.getTradingSession().getClassDetailForTradingSession( sessions[0].sessionName );
//
        // Print out results
//        System.out.println("Got " + classes.length + " classes for session " + sessions[0].sessionName);
//        for (int i=0; i < classes.length; i++)
//        {
//            SessionStrategyStruct[] strategies = session.getTradingSession().getStrategiesByClassForSession(sessions[0].sessionName, classes[i].classDetail.classStruct.classKey, strategyStatusConsumer);
//            System.out.println("  Got " + strategies.length + " strategies for session " + sessions[0].sessionName + " and classKey " + classes[i].classDetail.classStruct.classKey );
//            for (int j=0; j < strategies.length; j++)
//            {
//               for (int k=0; k < strategies[j].sessionStrategyLegs.length; k++)
//               {
//                  System.out.println("    Strategy[" + j + "] side: "         + strategies[j].sessionStrategyLegs[k].side
//                                                         + " product: "       + strategies[j].sessionStrategyLegs[k].product
//                                                         + " ratioQuantity: " + strategies[j].sessionStrategyLegs[k].ratioQuantity );
//               }
//            }
//        }
    }


}// EOF
