// $Workfile$ com.cboe.application.quote.UnitTestUserQuote.java
// $Revision$
// Last Modification on:  $Date$ $Modtime$// $Author$
/* $Log$
*   Initial Version         3/16/99      fengc
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.


package com.cboe.application.quote;

import java.util.*;
import com.cboe.application.shared.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.application.cas.*;
import com.cboe.application.test.*;
import com.cboe.delegates.callback.*;
import com.cboe.domain.util.TimeServiceWrapper;

import com.cboe.infrastructureServices.foundationFramework.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

/**
* <b>Description</b>
* <p>
*    Implemetation of the quote interface
* </p>
*/
public class UnitTestUserQuote extends junit.framework.TestCase
{
    ////////////////// member variables /////////////////////////////////

    protected static RemoteConnection connection;
    protected static UserSessionManager session;
    protected static CMIUserSessionAdmin userSessionListener;
    protected static CMIClassStatusConsumer theClassConsumer;
    protected static CMIProductStatusConsumer theProductConsumer;
    protected static CMIQuoteStatusConsumer   theQuoteStatusConsumer;

    protected static TestCallback callbackConsumer;

    private static QuoteEntryStruct[] quoteEntry1;
    private static QuoteEntryStruct[] quoteEntry2;

    private static int      productKey = 3; // default
    private static int      productKey2 = 6;

    private static int[]    productKeys;

    private static int      classKey = 1;   // default
    private static int[]    classKeys;

    private static SessionProfileUserStruct validUserStruct;
    private static ProductStruct[] products;

    private static String logonUserName = "sbtUser1";
    private static String logonPassword = "";

    protected static boolean remote = true;

    private static final String SESSION_NAME="W_DAY";

    /**
     * ProductQueryManagerImpl constructor.
     */
    public UnitTestUserQuote(String name)
    {
        super(name);
    }// end of constructor

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

            QuoteStatusConsumerDelegate quoteListener = new QuoteStatusConsumerDelegate(callbackConsumer);
            org.omg.CORBA.Object quoteObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(quoteListener);
            theQuoteStatusConsumer = CMIQuoteStatusConsumerHelper.narrow(quoteObject);
        }
        catch (Exception e)
        {
            System.out.println ("CORBA object not found, servant not active");
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
            String[] testArgs = {UnitTestUserQuote.class.getName()};
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
     * Returns a set of unit tests.
     *
     * @return suite of unit tests
     *
     * @author Connie Liang
     */
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();

        suite.addTest(new UnitTestUserQuote("testAcceptQuote"));
        suite.addTest(new UnitTestUserQuote("testGetQuote"));
        suite.addTest(new UnitTestUserQuote("testCancelQuotesByClass"));
        suite.addTest(new UnitTestUserQuote("testCancelAllQuotes"));
        suite.addTest(new UnitTestUserQuote("testAcceptQuotesForClass"));
        return suite;
    }

    private static String getLoginUserName()
    {
        return logonUserName;
    }

    protected static void initStructs()
             throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        /////// user struct
        validUserStruct = session.getValidSessionProfileUser();
        ClassStruct[] classes = null;

        short[] types = {ProductTypes.OPTION};
        classes = session.getProductQuery().getProductClasses(ProductTypes.OPTION);

        // init class key
        if (classes.length > 0 )
        {
            classKey = classes[0].classKey;

            ProductStruct[] tempProduct = session.getProductQuery().getProductsByClass(classKey);

            if ( tempProduct.length > 1)
            {
                productKey = tempProduct[0].productKeys.productKey;
                productKey2 = tempProduct[1].productKeys.productKey;
            }
        }

        ////////class keys
        classKeys = new int[1];
        classKeys[0] = classKey;

        //////// product keys
        productKeys = new int[1];

        productKeys[0]= productKey;
        quoteEntry1 = new QuoteEntryStruct[1];

        QuoteEntryStruct aStruct1 = UnitTestHelper.CreateNewQuoteEntryStruct(SESSION_NAME, productKey, 40,40);
        QuoteEntryStruct aStruct2 = UnitTestHelper.CreateNewQuoteEntryStruct(SESSION_NAME, productKey, 50, 50);
        QuoteEntryStruct aStruct3 = UnitTestHelper.CreateNewQuoteEntryStruct(SESSION_NAME, productKey2, 30, 30);

        quoteEntry1[0] = aStruct1;

        quoteEntry2 = new QuoteEntryStruct[2];

        quoteEntry2[0] = aStruct2;
        quoteEntry2[1] = aStruct3;
    }

    /////////////// IDL exported methods ////////////////////////////////////

    /**
     * accepts the quote entry.
     *
     * @param quotes quote entries to be accepted
     * @return none
     *
     * @author Connie Feng
     */
    public void testAcceptQuote()
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        DateTimeStruct startTime = TimeServiceWrapper.toDateTimeStruct();

       logMsg("testAcceptQuote 1", ""+quoteEntry1[0].productKey);

        session.getQuote().acceptQuote(quoteEntry1[0]);

/*
        ActivityHistoryStruct[] activites = session.getQuote().getQuoteActivity(classKey, startTime, QueryDirections.QUERY_FORWARD);
        ReflectiveStructTester.printStruct(activites, "QuoteHistory Forward: " + activites.length);

        DateTimeStruct reverseStartTime = TimeServiceWrapper.toDateTimeStruct();
        activites = session.getQuote().getQuoteActivity(classKey, reverseStartTime, QueryDirections.QUERY_BACKWARD);
        ReflectiveStructTester.printStruct(activites, "QuoteHistory Backward: " + activites.length);

        ReflectiveStructTester.printStruct(activites, "QuoteHistory: " + activites.length);
        logMsg("testAcceptQuote 2", ""+ quoteEntry2[0].productKey);

        session.getQuote().acceptQuote(quoteEntry2[0]);
        activites = session.getQuote().getQuoteActivity(classKey, startTime, QueryDirections.QUERY_FORWARD);
        ReflectiveStructTester.printStruct(activites, "QuoteHistory: " + activites.length);

        activites = session.getQuote().getQuoteActivity(classKey, reverseStartTime, QueryDirections.QUERY_BACKWARD);
        ReflectiveStructTester.printStruct(activites, "QuoteHistory Backward: " + activites.length);
*/
    }// end of acceptQuote

   /**
     * accepts the quote entry.
     *
     * @param quotes quote entries to be accepted
     * @return none
     *
     * @author Connie Feng
     */
    public void testAcceptQuotesForClass()
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        logMsg("testAcceptQuoteForClass ", ""+classKey);

        ClassQuoteResultStruct[] classQuotes= session.getQuote().acceptQuotesForClass(classKey, quoteEntry2);
        assertTrue("TestClassQuoteResultStruct", false==ReflectiveStructTester.testNullStruct(classQuotes));
    }// end of acceptQuote

    /**
     * gets the quote given the product key.
     *
     * @param productKey the product key
     * @param clientListener the client call back reference
     * @return QuoteStruct the quote information
     *
     * @author Connie Feng
     */
    public void testGetQuote( )
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        logMsg("PREtestGetQuote", ""+productKey);

        QuoteDetailStruct theStruct = session.getQuote().getQuote(SESSION_NAME, productKey);

        logMsg("POSTtestGetQuote", "ClassKey: "+ theStruct.productKeys.classKey
                                    + "ProductKey: " + theStruct.productKeys.productKey
                                    + "MemberKey: " + theStruct.quote.userId );

    }// end of getQuote

    /**
     * cancels the quote for the given product key.
     *
     * @param productKey the product keys
     * @return none
     *
     * @author Connie Feng
     */
    public void testCancelQuote()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException
    {
        logMsg("PREtestCancelQuote for product", "" +productKeys[0] );
        session.getQuote().cancelQuote(SESSION_NAME, productKeys[0]);
        logMsg("POSTtestCancelQuote for product", "" +productKeys[0] );

    }// end of cancelQuote

    /**
     * cancels all quote for the user.
     *
     * @return none
     *
     * @author Connie Feng
     */
    public void testCancelAllQuotes()
           throws SystemException, CommunicationException, AuthorizationException, NotAcceptedException, TransactionFailedException
    {
        logMsg("PREtestCancelAllQuotes", "" );
        session.getQuote().cancelAllQuotes(SESSION_NAME);
        logMsg("POSTtestCancelAllQuotes", "" );
    }// end of cancelAllQuotes

    /**
     * cancels the quote for given classes.
     *
     * @param classKeys the classes keys
     * @return none
     *
     * @author Connie Feng
     */
    public void testCancelQuotesByClass()
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException
    {
        logMsg("PREtestCancelQuotesByClass for class", ""+classKeys[0] );

        session.getQuote().cancelQuotesByClass(SESSION_NAME, classKeys[0]);

        logMsg("POSTtestCancelQuotesByClass for class", ""+classKeys[0] );
    }// end of cancelQuotesByClass

    private void logMsg(String caller, String msg)
    {
      System.out.println(caller + " " + msg);
    }


}// end of class UserQuoteImpl
