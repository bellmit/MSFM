package com.cboe.application.userServices;

import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.application.shared.*;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;

/**
 * A unit tester User Trading Parameters
 *
 * @author Mike Pyatetsky
 */
public class UnitTestUserTradingParameters extends test.framework.TestCase
{

    protected static UserTradingParameters  userTradingParameters;
    static boolean QRMGlobalSwitchStatus = false;
    protected static UserQuoteRiskManagementProfileStruct userQuoteRiskManagmentProfileStruct;
    protected static QuoteRiskManagementProfileStruct quoteRiskManagmentDefaultProfile;
    static int classKey = 2;
    static int []classKeys;
    static private String PASSWORD = null;
    static private String USER_NAME = null;

    private static boolean initFlag;

    protected static SessionProfileUserStruct validUserStruct;
    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static boolean remote = true;
    protected static TestCallback callbackConsumer;
    /**
    /**
     * UnitTestProductQuery constructor comment.
     * @param name java.lang.String
     */


    public UnitTestUserTradingParameters(String name)
    {
        super(name);
    }

    protected static void initUserSession()
    {
        try {
            if ( session == null )
            {
                UserLogonStruct logonStruct = new UserLogonStruct(USER_NAME, PASSWORD, "2.0", LoginSessionModes.STAND_ALONE_TEST);
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
     * Use command line properties as in following example:
     *  -DUSER_NAME=CCC -DPASSWORD=CCC -DCLASS_KEY=315
     */
    public static void main(String args[])
    {
        java.util.Properties prop = System.getProperties();
        String unsubscribeStr =(String)prop.get("REMOTE");
        PASSWORD = (String)prop.get("PASSWORD");
        USER_NAME = (String)prop.get("USER");
        String class_Key  = (String)prop.get("CLASS_KEY");

        if(USER_NAME == null)
            USER_NAME = "sbtMike";
        if(PASSWORD == null)
            PASSWORD = "";
        if(class_Key != null)
            classKey = Integer.parseInt(class_Key);

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
            userTradingParameters = session.getUserTradingParameters();
            String[] testArgs = {UnitTestUserTradingParameters.class.getName()};
            test.ui.TestRunner.main(testArgs);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Initialize testing, only needs to be performed once.
     *
     * @author Mike Pyatetsky
     */
    public void setUp()
    {
    }
    /**
     * Returns a set of unit tests.
     *
     * @return suite of unit tests
     *
     * @author Mike Pyatetsky
     */
    public static test.framework.Test suite()
    {
        test.framework.TestSuite suite = new test.framework.TestSuite();

        suite.addTest(new UnitTestUserTradingParameters("testGetAllQuoteRiskProfiles"));


        suite.addTest(new UnitTestUserTradingParameters("testGetQuoteRiskManagementEnabledStatus"));

        suite.addTest(new UnitTestUserTradingParameters("testSetQuoteRiskManagementEnabledStatus"));

        suite.addTest(new UnitTestUserTradingParameters("testGetQuoteRiskManagementEnabledStatus"));


        suite.addTest(new UnitTestUserTradingParameters("testGetDefaultQuoteRiskProfile"));

        suite.addTest(new UnitTestUserTradingParameters("testSetDefaultQuoteRiskProfile"));

        suite.addTest(new UnitTestUserTradingParameters("testGetDefaultQuoteRiskProfile"));


        suite.addTest(new UnitTestUserTradingParameters("testSetQuoteRiskProfile"));
        suite.addTest(new UnitTestUserTradingParameters("testGetQuoteRiskProfileByClass"));


        suite.addTest(new UnitTestUserTradingParameters("testRemoveQuoteRiskProfile"));
        suite.addTest(new UnitTestUserTradingParameters("testGetAllQuoteRiskProfiles"));

        suite.addTest(new UnitTestUserTradingParameters("testRemoveAllQuoteRiskProfile"));
        suite.addTest(new UnitTestUserTradingParameters("testGetAllQuoteRiskProfiles"));


        return suite;
    }

    /**
     * Test <code>getAllQuoteRiskProfiles()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testGetAllQuoteRiskProfiles()
     {
        try
        {
            System.out.println("*******************Start testGetAllQuoteRiskProfiles **********************");

            userQuoteRiskManagmentProfileStruct = userTradingParameters.getAllQuoteRiskProfiles();

            if(userQuoteRiskManagmentProfileStruct.quoteRiskProfiles != null &&
               userQuoteRiskManagmentProfileStruct.quoteRiskProfiles.length != 0)
            {
                classKey = userQuoteRiskManagmentProfileStruct.quoteRiskProfiles[0].classKey;
            }
            else
            {
                System.out.println(" No Class QRM Profiles are recieved");
            }
            quoteRiskManagmentDefaultProfile = userQuoteRiskManagmentProfileStruct.defaultQuoteRiskProfile;
            ReflectiveStructBuilder.printStruct(userQuoteRiskManagmentProfileStruct, "testGetAllQuoteRiskProfiles");
            System.out.println("*******************testGetAllQuoteRiskProfiles completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testGetAllQuoteRiskProfiles");
            e.printStackTrace();
        }
     }

    /**
     * Test <code>setQuoteRiskManagementEnabledStatus()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testSetQuoteRiskManagementEnabledStatus()
     {
        if(QRMGlobalSwitchStatus)
            QRMGlobalSwitchStatus = false;
        else
            QRMGlobalSwitchStatus = true;

        try
        {
            System.out.println("*******************Start testSetQuoteRiskManagementEnabledStatus **********************");
            System.out.println("Set GLOBAL QRM status to <<" + (new Boolean(QRMGlobalSwitchStatus)).toString() + ">>");

            userTradingParameters.setQuoteRiskManagementEnabledStatus(QRMGlobalSwitchStatus);
            System.out.println("*******************testSetQuoteRiskManagementEnabledStatus completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testSetQuoteRiskManagementEnabledStatus");
            e.printStackTrace();
        }
     }

    /**
     * Test <code>testGetQuoteRiskManagementEnabledStatus()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testGetQuoteRiskManagementEnabledStatus()
     {
        try
        {
            System.out.println("*******************Start testGetQuoteRiskManagementEnabledStatus  **********************");

            QRMGlobalSwitchStatus = userTradingParameters.getQuoteRiskManagementEnabledStatus();

            System.out.println("QRMGlobalSwitchStatus is << " + (new Boolean(QRMGlobalSwitchStatus)).toString() + ">>");
            System.out.println("*******************testGetQuoteRiskManagementEnabledStatus completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testGetQuoteRiskManagementEnabledStatus");
            e.printStackTrace();
        }
     }

    /**
     * Test <code>getDefaultQuoteRiskProfile()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testGetDefaultQuoteRiskProfile()
     {
        try
        {
            System.out.println("********* Start testGetDefaultQuoteRiskProfile  **********************");

            quoteRiskManagmentDefaultProfile = userTradingParameters.getDefaultQuoteRiskProfile();
            ReflectiveStructBuilder.printStruct(quoteRiskManagmentDefaultProfile, "testGetDefaultQuoteRiskProfile");

            System.out.println("*******************testGetDefaultQuoteRiskProfile completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testGetDefaultQuoteRiskProfile");
            e.printStackTrace();
        }
     }

    /**
     * Test <code>setQuoteRiskProfile()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testSetQuoteRiskProfile()
     {
        if(classKey == 0)
            classKey = userQuoteRiskManagmentProfileStruct.quoteRiskProfiles[0].classKey;

        QuoteRiskManagementProfileStruct quoteRiskManagementProfileStruct =UnitTestHelper.createQRMProfileStruct(classKey, true);

        try
        {
            System.out.println("*******************Start testSetQuoteRiskProfile**********************");
            System.out.println("Change QRM profile for classKey <<" + classKey + ">>");

            userTradingParameters.setQuoteRiskProfile(quoteRiskManagementProfileStruct);
            System.out.println("*******************testSetQuoteRiskProfile completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testSetQuoteRiskProfile");
            e.printStackTrace();
        }
     }

    /**
     * Test <code>getQuoteRiskProfileByClass(()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testGetQuoteRiskProfileByClass()
     {
        if(classKey == 0)
            classKey = userQuoteRiskManagmentProfileStruct.quoteRiskProfiles[0].classKey;

        try
        {
            System.out.println("*******************Start testGetQuoteRiskProfileByClass**********************");
            System.out.println("Get QRM profile for classKey <<" + classKey + ">>");

            QuoteRiskManagementProfileStruct quoteRiskManagementProfile = userTradingParameters.getQuoteRiskManagementProfileByClass(classKey);
            ReflectiveStructBuilder.printStruct(quoteRiskManagementProfile, "testGetQuoteRiskProfileByClass");

            System.out.println("*******************testGetQuoteRiskProfileByClass completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testGetQuoteRiskProfileByClass");
            e.printStackTrace();
        }
     }



    /**
     * Test <code>setQuoteRiskProfile()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testSetDefaultQuoteRiskProfile()
     {
        boolean defaultProfileStatus = quoteRiskManagmentDefaultProfile.quoteRiskManagementEnabled;
        if(defaultProfileStatus)
            defaultProfileStatus = false;
        else
            defaultProfileStatus = true;

        QuoteRiskManagementProfileStruct quoteRiskManagmentDefaultProfile = UnitTestHelper.createQRMProfileStruct(0, defaultProfileStatus);

        try
        {
            System.out.println("*******************Start testSetDefaultQuoteRiskProfile *********************");

            userTradingParameters.setQuoteRiskProfile(quoteRiskManagmentDefaultProfile);
            System.out.println("*******************testSetDefaultQuoteRiskProfile completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testSetDefaultQuoteRiskProfile");
            e.printStackTrace();
        }
     }

    /**
     * Test <code>removeQuoteRiskProfile()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testRemoveQuoteRiskProfile()
     {
        try
        {
            System.out.println("*******************Start testRemoveQuoteRiskProfile **********************");
            System.out.println("Remove QRM profile for classKey <<" + classKey + ">>");

            userTradingParameters.removeQuoteRiskProfile(classKey);
            System.out.println("*******************testRemoveQuoteRiskProfile completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testRemoveQuoteRiskProfile");
            e.printStackTrace();
        }
     }

     /**
     * Test <code>removeAllQuoteRiskProfiles()/<code> method of CAS UserTradingParameters service
     *
     * @author Mike Pyatetsky
     *
     */
     public void testRemoveAllQuoteRiskProfile()
     {
        try
        {
            System.out.println("*******************Start testRemoveAllQuoteRiskProfile **********************");

            userTradingParameters.removeAllQuoteRiskProfiles();
            System.out.println("*******************testRemoveAllQuoteRiskProfile completed **********************");

        } catch(Exception e) {
            System.out.println("Exception in testRemoveAllQuoteRiskProfile");
            e.printStackTrace();
        }
     }

     public void logout()
     {
         try
         {
            session.logout();
         } catch (Exception e){
            e.printStackTrace();
         }
     }


  }//EOF
