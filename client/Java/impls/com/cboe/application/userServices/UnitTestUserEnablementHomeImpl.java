/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Mar 6, 2002
 * Time: 2:58:51 PM
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
import com.cboe.util.*;
import com.cboe.domain.util.UserStructBuilder;
import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.application.shared.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.application.UserEnablement;;

/**
 * A unit tester for the UserEnablementHomeImpl
 * All public methods of  UserEnablementHomeImpl are tested
 *
 * @author Emily Huang
 */
public class UnitTestUserEnablementHomeImpl extends test.framework.TestCase
{
    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static boolean remote = true;
    protected static TestCallback callbackConsumer;

    private static UserEnablementStruct[] userEnablements;
    private static UserSessionEnablementStruct[] userSessionEnablements;
    private short[] productTypes = {};
    private static String userId1 = "CCC";
    private static String acronym1 = "CCC";
    private static String userId2 = "DDD";
    private static String acronym2 = "DDD";
    private static String exchange = "CBOE";

    public UserEnablementHomeImpl enablementHome;



    /**
     * UnitTestUserEnablementHomeImpl constructor comment.
     * @param name java.lang.String
     */


    public UnitTestUserEnablementHomeImpl(String name)
    {
        super(name);
        enablementHome = (UserEnablementHomeImpl)ServicesHelper.getUserEnablementHome();
    }
    /**
     * Runs the unit test.
     */
    public static void main(String args[])
    {
        System.out.println("UnitTestHelper.initFFEnv()");
        UnitTestHelper.initFFEnv();
        String[] testArgs = {UnitTestUserEnablementHomeImpl.class.getName()};
        test.ui.TestRunner.main(testArgs);

    }
    /**
     * Initialize testing, only needs to be performed once.
     *
     * @author Emily Huang
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
        suite.addTest(new UnitTestUserEnablementHomeImpl("testcreate"));
        suite.addTest(new UnitTestUserEnablementHomeImpl("testremove"));
        suite.addTest(new UnitTestUserEnablementHomeImpl("testfind"));
        return suite;
    }

    public void testcreate()
    {
        System.out.println("----------------testCreate---------------");
        UserEnablement enablement;
        enablement = enablementHome.create(userId1, exchange, acronym1);

        System.out.println(" create(userId) with userId = " + userId1 + enablement.toString() );
        enablementHome.create(userId2, exchange, acronym2);
        System.out.println(" create(userId) with userId = " + userId2 );
    }

    public void testfind()
    {
        if ( enablementHome.find(userId1, exchange, acronym1) != null )
        {
            System.out.println( userId1 + " is found ");
        }
        else
        {
            System.out.println( userId1 + " is not found ");
        }

        if (enablementHome.find(userId2, exchange, acronym2) != null )
        {
            System.out.println( userId2 + " is found ");
        }
        else
        {
            System.out.println( userId2 + " is not found ");
        }
    }

    public void testremove()
    {
        enablementHome.remove(userId1, exchange, acronym1);
        System.out.println(" remove(userId) with userId = " + userId1 );
    }
    /**
     *
     * @author Emily Huang
     */
    public void tearDown()
    {

    }


}//EOF
