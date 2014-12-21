// $Workfile$ com.cboe.application.marketData.UnitTestSystemMarketQuery.java
// $Revision$
// Last Modification on:  $Date$ $Modtime$// $Author$
/* $Log$
*   Initial Version         4/5/99      fengc
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.


package com.cboe.application.marketData;

import java.util.*;

import com.cboe.application.shared.*;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.delegates.callback.*;
import com.cboe.application.cas.*;
import com.cboe.domain.util.*;

import com.cboe.application.test.*;

import com.cboe.infrastructureServices.foundationFramework.*;

/**
* <b>Description</b>
* <p>
*    Implemetation of the quote interface
* </p>
*/
public class UnitTestSystemMarketQuery extends junit.framework.TestCase
{
    ////////////////// member variables /////////////////////////////////
    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static CMICurrentMarketConsumer marketConsumer;
    protected static CMINBBOConsumer theNBBOConsumer;
    protected static CMIRecapConsumer recapConsumer;
    protected static CMITickerConsumer tickerConsumer;
    protected static CMIClassStatusConsumer theClassConsumer;
    protected static CMIProductStatusConsumer theProductConsumer;
    protected static TestCallback callbackConsumer;

    protected static int productKey = 3;

    protected static int[] productKeys = null;

    protected static int classKey = 1;
    protected static int[] classKeys = null;

    protected static SessionProfileUserStruct validSessionProfileUser;
    protected static boolean remote = true;
    protected final static String SESSION_NAME="W_AM1";

    /**
     * ProductQueryManagerImpl constructor.
     */
    public UnitTestSystemMarketQuery(String name)
    {
        super(name);
    }// end of constructor

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

            CurrentMarketConsumerDelegate marketListener = new CurrentMarketConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object marketObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(marketListener);
            marketConsumer = CMICurrentMarketConsumerHelper.narrow(marketObject);

            NBBOConsumerDelegate nbboListener = new NBBOConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object nbboObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(nbboListener);
            theNBBOConsumer = CMINBBOConsumerHelper.narrow(nbboObject);

            RecapConsumerDelegate recapListener = new RecapConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object recapObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(recapListener);
            recapConsumer = CMIRecapConsumerHelper.narrow(recapObject);

            TickerConsumerDelegate tickerListener = new TickerConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object tickerObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(tickerListener);
            tickerConsumer = CMITickerConsumerHelper.narrow(tickerObject);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
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

            String[] testArgs = {UnitTestSystemMarketQuery.class.getName()};
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
     * Fixme ...
     *
     * @author Connie Liang
     */
    public void tearDown()
    {
        // nothing to do - just need to override parent's teardown
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
     * Returns a set of unit tests.
     *
     * @return suite of unit tests
     *
     * @author Connie Liang
     */
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();

        suite.addTest(new UnitTestSystemMarketQuery("testGetMarketDataHistoryByTime"));
        suite.addTest(new UnitTestSystemMarketQuery("testGetBookDepth"));

        return suite;
    }

   protected static void initStructs()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      /////// user struct
      validSessionProfileUser = session.getValidSessionProfileUser();
      SessionClassStruct[] classes = null;

      short[] types = {ProductTypes.OPTION};
      classes = session.getTradingSession().getClassesForSession(SESSION_NAME,ProductTypes.OPTION,theClassConsumer);

        // init class key
        if (classes.length > 0 )
        {
            classKey = classes[0].classStruct.classKey;
            SessionProductStruct[] tempProduct = session.getTradingSession().getProductsForSession(SESSION_NAME, classKey, theProductConsumer);

            if ( tempProduct.length > 1)
            {
                productKey = tempProduct[0].productStruct.productKeys.productKey;
            }
        }
        //////// product keys
        productKeys = new int[1];
        productKeys[0]= productKey;

        ////////class keys

        classKeys = new int[1];
        classKeys[0] = classKey;
    }

    /////////////// IDL exported methods ////////////////////////////////////
   public void testGetBookDepth()
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, NotFoundException
   {
      BookDepthStruct bookDepth = session.getMarketQuery().getBookDepth(SESSION_NAME, productKey);
      assertTrue("testNullValues BookDepthStruct", false==ReflectiveStructTester.testNullStruct(bookDepth));
   }

   public void testGetMarketDataHistoryByTime()
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
   {
      DateTimeStruct startTime = new DateWrapper().toDateTimeStruct();

      MarketDataHistoryStruct marketDataHistoryStruct = session.getMarketQuery().getMarketDataHistoryByTime(SESSION_NAME, productKey, startTime, QueryDirections.QUERY_FORWARD);
      assertTrue("testNullValues MarketDataHistoryStruct", false==ReflectiveStructTester.testNullStruct(marketDataHistoryStruct));
   }

//////////////////////////////////////////////
    private void logMsg(String caller, String msg)
    {
      System.out.println(caller + " " + msg);
    }

}// end of class UnitTestSystemMarketQuery
