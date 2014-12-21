package com.cboe.application.userServices;

import com.cboe.idl.cmiUser.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.domain.util.UserStructBuilder;
import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.application.shared.*;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;

/**
 * A unit tester for the product query.
 *
 * @author Connie Liang
 */
public class UnitTestUserPreferenceQuery extends junit.framework.TestCase
{
    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static boolean remote = true;
    protected static TestCallback callbackConsumer;

    private static SessionProfileUserStruct validUserStruct;
    /**
     * UnitTestProductQuery constructor comment.
     * @param name java.lang.String
     */


    public UnitTestUserPreferenceQuery(String name)
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
        try {
            UserSessionAdminConsumerDelegate sessionDelegate = new UserSessionAdminConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object orbObject = (org.omg.CORBA.Object) connection.register_object(sessionDelegate);
            userSessionListener = CMIUserSessionAdminHelper.narrow(orbObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    protected static void initStructs()
             throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
      /////// user struct
      validUserStruct = session.getValidSessionProfileUser();
    }
    /**
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

            String[] testArgs = {UnitTestUserPreferenceQuery.class.getName()};
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
        suite.addTest(new UnitTestUserPreferenceQuery("testGetUserPreferences"));

        suite.addTest(new UnitTestUserPreferenceQuery("testSetUserPreferences"));

        suite.addTest(new UnitTestUserPreferenceQuery("testRemoveUserPreferences"));

        suite.addTest(new UnitTestUserPreferenceQuery("testSetUserPreferences"));
        suite.addTest(new UnitTestUserPreferenceQuery("testGetUserPreferences"));

        suite.addTest(new UnitTestUserPreferenceQuery("testGetUserPreferencesByPrefix"));
        suite.addTest(new UnitTestUserPreferenceQuery("testRemoveUserPreferencesByPrefix"));

        suite.addTest(new UnitTestUserPreferenceQuery("testSetUserPreferences"));
        suite.addTest(new UnitTestUserPreferenceQuery("testUpdateUserPreferences"));

        suite.addTest(new UnitTestUserPreferenceQuery("testGetUserPreferences"));

        suite.addTest(new UnitTestUserPreferenceQuery("testNewUserPreferences"));

        suite.addTest(new UnitTestUserPreferenceQuery("testGetUserPreferences"));

        suite.addTest(new UnitTestUserPreferenceQuery("testGetAllSystemPreferences"));
        suite.addTest(new UnitTestUserPreferenceQuery("testGetSystemPreferencesByPrefix"));

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
     * Performs unit test on the <code>getUserPreferences</code> method.
     *
     * @author Connie Liang
     */
    public void testGetUserPreferences()
    {
        PreferenceStruct[] prefs = null;
        try {
            prefs = session.getUserPreferenceQuery().getAllUserPreferences();
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        for ( int i = 0; i < prefs.length; i++ )
        {
            System.out.println("****** User Preferences: " + UserStructBuilder.toString(prefs[i]));
        }
    }

    /**
     * Performs unit test on the <code>getAllSystemPreferences</code> method.
     *
     * @author Connie Liang
     */
    public void testGetAllSystemPreferences()
    {
        PreferenceStruct[] prefs = null;
        try {
            prefs = session.getUserPreferenceQuery().getAllSystemPreferences();
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        for ( int i = 0; i < prefs.length; i++ )
        {
            System.out.println("****** System Preferences: " + UserStructBuilder.toString(prefs[i]));
        }
    }

    /**
     * Performs unit test on the <code>getAllSystemPreferences</code> method.
     *
     * @author Connie Liang
     */
    public void testGetSystemPreferencesByPrefix()
    {
        PreferenceStruct[] prefs = null;
        try {
            prefs = session.getUserPreferenceQuery().getSystemPreferencesByPrefix("User");
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        for ( int i = 0; i < prefs.length; i++ )
        {
            System.out.println("****** System Preferences: " + UserStructBuilder.toString(prefs[i]));
        }
    }

    /**
     * Performs unit test on the <code>getUserPreferencesByPrefix</code> method.
     *
     * @author Connie Liang
     */
    public void testGetUserPreferencesByPrefix()
    {
        PreferenceStruct[] prefs = null;
        try {
            prefs = session.getUserPreferenceQuery().getUserPreferencesByPrefix("FirstPath");
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        for ( int i = 0; i < prefs.length; i++ )
        {
            System.out.println("****** User Preferences by prefix: " + UserStructBuilder.toString(prefs[i]));
        }
    }

    /**
     * Performs unit test on the <code>getUserPreferences</code> method.
     *
     * @author Connie Liang
     */
    public void testRemoveUserPreferencesByPrefix()
    {
        PreferenceStruct[] prefs = null;
        try {
           session.getUserPreferenceQuery().removeUserPreferencesByPrefix("FirstPath");
           prefs = session.getUserPreferenceQuery().getUserPreferencesByPrefix("FirstPath");
           assertTrue("there should be no pref by prefix FirstPath", 0==prefs.length);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }

        /**
     * Performs unit test on the <code>getUserPreferences</code> method.
     *
     * @author Connie Liang
     */
    public void testRemoveUserPreferences()
    {
        PreferenceStruct[] prefs = null;
        try {
            prefs = session.getUserPreferenceQuery().getAllUserPreferences();
            session.getUserPreferenceQuery().removeUserPreference(prefs);
            prefs = session.getUserPreferenceQuery().getAllUserPreferences();
            assertTrue("there should be no  prefs after the remove", 0==prefs.length);
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        for ( int i = 0; i < prefs.length; i++ )
        {
            System.out.println("****** User Preferences: " + UserStructBuilder.toString(prefs[i]));
        }
    }

    /**
     * Performs unit test on the <code>setUserPreferences</code> method.
     *
     * @author Connie Liang
     */
    public void testSetUserPreferences()
    {
        PreferenceStruct[] prefs = new PreferenceStruct[2];

        prefs[0] = new PreferenceStruct();
        prefs[1] = new PreferenceStruct();

        prefs[0].name = "FirstPath.FirstName";
        prefs[0].value = "FirstValue";
        prefs[1].name = "FirstPath.SecondName";
        prefs[1].value = "SecondValue";

        try {
            session.getUserPreferenceQuery().setUserPreferences(prefs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs unit test on the <code>setUserPreferences</code> method.
     *
     * @author Connie Liang
     */
    public void testNewUserPreferences()
    {
        PreferenceStruct pref = new PreferenceStruct();

        pref.name = "SecondPath.FirstName";
        pref.value = "SecondValue";

        PreferenceStruct[] prefs = {pref};
        try {
            session.getUserPreferenceQuery().setUserPreferences(prefs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs unit test on the <code>setUserPreferences</code> method.
     *
     * @author Connie Liang
     */
    public void testUpdateUserPreferences()
    {
        PreferenceStruct pref = new PreferenceStruct();

        pref.name = "FirstPath.FirstName";
        pref.value = "UpdatedFirstValue";

        PreferenceStruct[] prefs = {pref};
        try {
            session.getUserPreferenceQuery().setUserPreferences(prefs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}//EOF
