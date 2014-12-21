/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Mar 5, 2002
 * Time: 12:37:30 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */


package com.cboe.application.userServices;

import com.cboe.idl.cmiUser.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.user.UserEnablementStruct;
import com.cboe.idl.user.UserSessionEnablementStruct;
import com.cboe.idl.property.PropertyStruct;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.application.shared.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.property.BasicPropertyParser;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

/**
 * A unit tester for the user enablement.
 *
 * @author Emily Huang
 */
public class UnitTestUserEnablementImpl extends test.framework.TestCase
{
    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static boolean remote = false;
    protected static TestCallback callbackConsumer;

    private static SessionProfileUserStruct validUserStruct;

    private static UserEnablementStruct[] userEnablements;
    private static UserSessionEnablementStruct[] userSessionEnablements;
    private short[] productTypes = {};
    public UserEnablementHomeImpl enablementHome;
    public UserEnablementImpl enablementImpl;
    private static String userId1 = "CCC";
    private static String exchange1 = "CBOE";
    private static String acronym1 = "CCC";
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

    /**
     * UnitTestUserEnablementImpl constructor comment.
     * @param name java.lang.String
     */


    public UnitTestUserEnablementImpl(String name)
    {
        super(name);
        enablementHome = (UserEnablementHomeImpl)ServicesHelper.getUserEnablementHome();
        enablementImpl = (UserEnablementImpl)enablementHome.create(userId1, exchange1, acronym1);
    }
    protected static void initUserSession()
    {
        try {
            if ( session == null )
            {
                UserLogonStruct logonStruct = new UserLogonStruct(userId1, "", "2.0", LoginSessionModes.STAND_ALONE_TEST);
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
        initPOA();
        try {
            UserSessionAdminConsumerDelegate sessionListener = new UserSessionAdminConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object orbObject = getPOA().servant_to_reference(sessionListener);
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
        try
        {
            initConnection(args);
            initEnv();
            initializeCallbacks();
            initUserSession();
            initStructs();

            String[] testArgs = {UnitTestUserEnablementImpl.class.getName()};
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
     * @author Emily Huang
     */
    public static test.framework.Test suite()
    {
        test.framework.TestSuite suite = new test.framework.TestSuite();
        suite.addTest(new UnitTestUserEnablementImpl("testVerfiyUserEnablement"));
        suite.addTest(new UnitTestUserEnablementImpl("testAcceptUserEnablementUpdate"));
        return suite;
    }

    public void testVerfiyUserEnablement()
    {
        try 
        {
            try
            {
                ServicesHelper.getUserEnablementService("CCC", "CBOE", "CCC").verifyUserEnablement("W_AM1", 1,1);
                System.out.println("testVerifyUserEnablement  CCC W_AM1 1 1   Authorization Passed");
            }
            catch (AuthorizationException ae)
            {
                System.out.println("testVerifyUserEnablement  CCC W_AM1 1 1   Authorization Failed");
            }
            try
            {
                ServicesHelper.getUserEnablementService("CCC", "CBOE", "CCC").verifyUserEnablement("W_AM1", 1,2);
                System.out.println("testVerifyUserEnablement  CCC W_AM1 1 2   Authorization Passed");
            }
            catch (AuthorizationException ae)
            {
                System.out.println("testVerifyUserEnablement  CCC W_AM1 1 2   Authorization Failed");
            }
            try
            {
                ServicesHelper.getUserEnablementService("CCC", "CBOE", "CCC").verifyUserEnablement("W_MAIN", 1,1);
                System.out.println("testVerifyUserEnablement  CCC W_MAIN 1 1   Authorization Passed");
            }
            catch (AuthorizationException ae)
            {
                System.out.println("testVerifyUserEnablement  CCC W_MAIN 1 1   Authorization Failed");
            }
            try
            {
                ServicesHelper.getUserEnablementService("CCC", "CBOE", "CCC").verifyUserEnablement("W_MAIN", 1,2);
                System.out.println("testVerifyUserEnablement  CCC W_MAIN 1 2   Authorization Passed");
            }
            catch (AuthorizationException ae)
            {
                System.out.println("testVerifyUserEnablement  CCC W_MAIN 1 2   Authorization Failed");
            }
            try
            {
                ServicesHelper.getUserEnablementService("MAC", "CBOE", "MAC").verifyUserEnablement("W_MAIN", 1,1);
                System.out.println("testVerifyUserEnablement  MAC W_MAIN 1 1   Authorization Passed");
            }
            catch (AuthorizationException ae)
            {
                System.out.println("testVerifyUserEnablement  MAC W_MAIN 1 1   Authorization Failed");
            }
            // Now, stress test it some
            try
            {
                for (int i=0; i < 100000 ; i++)
                {
                    ServicesHelper.getUserEnablementService("CCC", "CBOE", "CCC").verifyUserEnablement("W_MAIN", 1,1);
                }
                System.out.println("testVerifyUserEnablement  CCC W_MAIN 1 1   Authorization Passed");
            }
            catch (AuthorizationException ae)
            {
                System.out.println("testVerifyUserEnablement  CCC W_MAIN 1 1   Authorization Failed");
            }
        } 
        catch ( Exception e)
        {
            e.printStackTrace();
        }

    }

    public void testAcceptUserEnablementUpdate()
    {
        try 
        {
            PropertyGroupStruct struct = new PropertyGroupStruct();
            struct.category = "userenablement";
//            struct.propertyKey = "CCC";
            struct.propertyKey = BasicPropertyParser.buildCompoundString(new String[]{exchange1,acronym1});
            PropertyStruct[] propertyStruct = new PropertyStruct[2];
            propertyStruct[0] = new PropertyStruct();
            propertyStruct[0].name = "W_AM1\u00011\u00011";
            propertyStruct[0].value = "true\u0001userenablement";
            propertyStruct[1] = new PropertyStruct();
            propertyStruct[1].name = "W_MAIN\u00010\u00011";
            propertyStruct[1].value = "true\u0001userenablement";
            struct.preferenceSequence = propertyStruct;

//            UserEnablementStruct eStruct = new UserEnablementStruct();
//            eStruct.testClassesOnly = false;
            ServicesHelper.getUserEnablementService("CCC", "CBOE", "CCC").acceptUserEnablementUpdate(struct);
        } 
        catch ( Exception e)
        {
            e.printStackTrace();
        }

    }

    public void testChannelUpdate()
    {
        enablementImpl.channelUpdate(null);
    }
    /**
     *
     * @author Emily Huang
     */
    public void tearDown()
    {

    }


}//EOF
