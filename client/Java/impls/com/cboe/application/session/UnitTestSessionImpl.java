package com.cboe.application.session;

import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiCallback.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.application.shared.*;
import com.cboe.delegates.callback.*;
import com.cboe.application.test.*;
import com.cboe.application.cas.*;

public class UnitTestSessionImpl extends junit.framework.TestCase
{
    private static SessionProfileUserStructV2 validUserStruct;
    private static RemoteConnection remoteConnection;
    private static com.cboe.interfaces.application.SessionManager sessionManager;
    private static String logonUserName = "sbtUser";
    private static String logonPassword = "";
    private static CMIUserSessionAdmin userAdminListener;

    /**
     * This is the extensive constructor.
     */
    public UnitTestSessionImpl(String name)
    {
        super(name);
    }

    /**
     * Runs the test.
     */
    public static void main(String args[])
    {
        remoteConnection = RemoteConnectionFactory.create(null);
        UnitTestHelper.initFFEnv();
        try {
            UserSessionAdminConsumerDelegate sessionListener = new UserSessionAdminConsumerDelegate(new TestCallback());
            org.omg.CORBA.Object orbObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(sessionListener);
            userAdminListener = CMIUserSessionAdminHelper.narrow(orbObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        initStruct();
        String[] testArgs = {UnitTestSessionImpl.class.getName()};
        junit.ui.TestRunner.main(testArgs);
    }


    public void setUp() {
    }

    public void tearDown() {
    }

    /**
      * Returns a set of unit tests.
      * @return suite of unit tests
      */
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTest(new UnitTestSessionImpl("getMarketQuery"));
        suite.addTest(new UnitTestSessionImpl("getOrderQuery"));
        suite.addTest(new UnitTestSessionImpl("getProductQuery"));
        suite.addTest(new UnitTestSessionImpl("getQuote"));
        suite.addTest(new UnitTestSessionImpl("getOrderEntry"));
        suite.addTest(new UnitTestSessionImpl("getAdministrator"));
        suite.addTest(new UnitTestSessionImpl("getProductDefinition"));
        suite.addTest(new UnitTestSessionImpl("getUserPreferenceQuery"));
        suite.addTest(new UnitTestSessionImpl("getTradingSession"));
        suite.addTest(new UnitTestSessionImpl("getValidUser"));
        suite.addTest(new UnitTestSessionImpl("getVersion"));
        suite.addTest(new UnitTestSessionImpl("getSystemDateTime"));
        suite.addTest(new UnitTestSessionImpl("getTradingSession"));
        suite.addTest(new UnitTestSessionImpl("getSessionId"));
        suite.addTest(new UnitTestSessionImpl("getUserHistory"));

        //suite.addTest(new UnitTestSessionImpl("logout"));
        return suite;
    }

    /* Since the configuration for the services should be set to the Null Impls in the
      * Configuration file, all services should be the null (test) versions. The test
      * version of the orderquery object auto intitializes it's order manager by creating
      * orders for all products.
      * @author Thomas Lynch
      */
    private static com.cboe.interfaces.application.SessionManager getSessionManager() {
        if (sessionManager == null)
        {
            try {
                sessionManager = ServicesHelper.createSessionManager(validUserStruct, "W_AM1", 1234, userAdminListener, LoginSessionTypes.PRIMARY, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionManager;
    }

    /**
     * Initialize the test environment
     */
    private static void initStruct () {
        try
        {
            validUserStruct = UnitTestHelper.createNewValidSessionProfileUserStructV2(logonUserName);
            sessionManager = getSessionManager();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("exception in initialize: " + e.toString());
        }
    }


    public void getMarketQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.MarketQuery marketQuery = getSessionManager().getMarketQuery();
        assertTrue("MarketQuery", null!=marketQuery);
    }

    public void getOrderQuery()
           throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.OrderQuery orderQuery = getSessionManager().getOrderQuery();
        assertTrue("orderQuery", null!=orderQuery);
    }

    public void getProductQuery()
               throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.ProductQuery productQuery = getSessionManager().getProductQuery();
        assertTrue("productQuery", null!=productQuery);
    }

    public void getQuote()
               throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.Quote quote = getSessionManager().getQuote();
        assertTrue("quote", null!=quote);
    }

    public void getOrderEntry()
              throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.OrderEntry orderEntry = getSessionManager().getOrderEntry();
        assertTrue("orderEntry", null!=orderEntry);
    }

    public void getAdministrator()
                throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.Administrator admin = getSessionManager().getAdministrator();
        assertTrue("Administrator", null!=admin);
    }

    public void getProductDefinition()
                throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.ProductDefinition productDefinition = getSessionManager().getProductDefinition();
        assertTrue("ProductDefinition", null!=productDefinition);
    }

    public void getUserPreferenceQuery()
                 throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.UserPreferenceQuery userPreference = getSessionManager().getUserPreferenceQuery();
        assertTrue("UserPreferenceQuery", null!=userPreference);
    }

    public void getTradingSession()
                 throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.TradingSession tradingSession = getSessionManager().getTradingSession();
        assertTrue("TradingSession", null!=tradingSession);
    }

    public void getValidUser()
             throws SystemException, CommunicationException, AuthorizationException
    {
        System.out.println("\n<======== Testing getValidUser() ========>");
        SessionProfileUserStruct theUser = getSessionManager().getValidSessionProfileUser();
        assertTrue("Valid User", true==logonUserName.equals(theUser.userId));
        System.out.println("UserName: " + theUser.userId);
    }

    public void logout()
             throws SystemException, CommunicationException, AuthorizationException
    {
        getSessionManager().logout();
    }

    public void getSystemDateTime()
             throws SystemException, CommunicationException, AuthorizationException
    {
        System.out.println("\n<======== Testing getSystemDateTime() ========>");
        ReflectiveStructTester.printStruct(getSessionManager().getSystemDateTime(), "SystemDateTime: ");
    }

    public void getVersion()
           throws SystemException, CommunicationException, AuthorizationException
    {
        System.out.println("\n<======== Testing getVersion() ========>");
        System.out.println("Version: " + getSessionManager().getVersion());
    }

    public void getSessionId()
           throws SystemException, CommunicationException, AuthorizationException
    {
        System.out.println("\n<======== Testing getSessionId() ========>");
        System.out.println("Session ID: " + getSessionManager().getSessionId());
    }

    public void getUserHistory()
           throws SystemException, CommunicationException, AuthorizationException
    {
        com.cboe.idl.cmi.UserHistory userHistory = getSessionManager().getUserHistory();
        assertTrue("UserHistory", null!=userHistory);
    }


}
