/*
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Nov 4, 2002
 * Time: 9:40:25 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.intermarketPresentation.api;

import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.intermarketPresentation.api.IntermarketAPI;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketHeldOrderAPI;
import com.cboe.interfaces.presentation.api.MarketMakerAPI;

import com.cboe.presentation.api.APIFactoryImpl;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.api.IntermarketAPIHome;
import com.cboe.presentation.api.TestCASCallback;
import com.cboe.presentation.api.UserAccessFactory;

import com.cboe.application.shared.RemoteConnection;
import com.cboe.application.shared.RemoteConnectionFactory;

public class TestIntermarketTranslator {
    private static RemoteConnection connection;
    private static UserSessionManager session;
    private static final String USER_ID="CCC";
    private static final String PASSWORD ="CCC";
    private static final String INTERMARKET_IOR_FILE ="/IntermarketUserAccess.ior";
    private static IntermarketAPI intermarketAPI;
    private static IntermarketHeldOrderAPI heldOrderAPI;
    private static String testSession = "ONE_MAIN";
    private static int testClass = 2359783;  //DELL
    private static int testProduct = 2359789;
    private static TestIMTranslatorEventChannel channelListener;

    private static void initIntermarketSession()
    {
        try {
            if ( intermarketAPI == null )
            {
                IntermarketAPIHome.create(IntermarketAPIHomeFactoryImpl.class);
                UserLogonStruct logonStruct = new UserLogonStruct(USER_ID, PASSWORD, "2.0", LoginSessionModes.STAND_ALONE_TEST);
                TestCASCallback casCallback = new TestCASCallback();
                System.out.println("Logging onto CAS");
                MarketMakerAPI mmAPI = UserAccessFactory.marketMakerLogon(logonStruct, LoginSessionTypes.PRIMARY, casCallback);
                System.out.println("-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-");
                System.out.println("User Information");
                System.out.println("-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-");
                System.out.println("-= Fullname = " + mmAPI.getValidUser().getFullName());
                System.out.println("-= Firm = " + mmAPI.getValidUser().getFirm());
                System.out.println("-= Role = " + mmAPI.getValidUser().getRole().getName());
                System.out.println("-= Userid = " + mmAPI.getValidUser().getUserId());
                System.out.println("-= Username = " + mmAPI.getValidUser().getFullName());
                System.out.println("-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-");
                intermarketAPI = IntermarketAPIHome.findIntermarketAPI();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initConnection(String[] args)
    {
        if ( connection == null )
        {
            connection = RemoteConnectionFactory.create(args);
            System.out.println("Connection established " + connection);
        }
    }

    private static void testGetIntermarketByProductForSession()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        CurrentIntermarketStruct currentIntermarketStruct = intermarketAPI.getIntermarketByProductForSession(testProduct, testSession);
    }

    private static void testGetIntermarketByClassForSession()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        CurrentIntermarketStruct[] intermarketStructs = intermarketAPI.getIntermarketByClassForSession(testClass, testSession);
    }

 /*   private static void testGetAllQuoteFadeProfilesForSession()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionQuoteFadeProfileStruct profileStruct = intermarketAPI.getAllQuoteFadeProfilesForSession(testSession);
    }

    private static void testGetQuoteFadeProfileByClass()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        QuoteFadeProfileStruct profileStruct = intermarketAPI.getQuoteFadeProfileByClass(testClass, testSession);
    }

    private static void testSetQuoteFadeEnabledStatus()
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException
    {
        boolean status = true;
        intermarketAPI.setQuoteFadeEnabledStatus(testSession, status);
    }

    private static void testGetQuoteFadeEnabledStatus()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        boolean status = intermarketAPI.getQuoteFadeEnabledStatus(testSession);
    }

    private static void testGetDefaultQuoteFadeProfile()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        QuoteFadeProfileStruct profileStruct = intermarketAPI.getDefaultQuoteFadeProfile(testSession);
    }

    private static void testSetQuoteFadeProfileForSession()
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException
    {
        SessionQuoteFadeProfileStruct sessionQuoteFadingParameters = null;
        intermarketAPI.setQuoteFadeProfileForSession(sessionQuoteFadingParameters);
    }

    private static void testRemoveQuoteFadeProfile()
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException
    {
        intermarketAPI.removeQuoteFadeProfile(testClass, testSession);
    }

    private static void removeAllQuoteFadeProfilesForSession()
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException
    {
        intermarketAPI.removeAllQuoteFadeProfilesForSession(testSession);
    }
 */
    private static void testRegister()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        boolean forceOverride = false;
        heldOrderAPI = intermarketAPI.register(testClass, testSession, forceOverride, channelListener, channelListener );
    }

    private static void testUnRegister()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        intermarketAPI.unregister(testClass, testSession, channelListener, channelListener);
    }

    private static void testRerouteHeldOrder()
        throws SystemException,CommunicationException,AuthorizationException,
              DataValidationException,TransactionFailedException,NotAcceptedException
    {
        OrderIdStruct heldOrderId = null;
        HeldOrderDetailStruct[] heldOrders = heldOrderAPI.getHeldOrdersByClassForSession(testSession, testClass);
        if (heldOrders.length > 0)
        {
            heldOrderId = heldOrders[0].heldOrder.order.orderId;
        }
        else
        {
            return;
        }
        boolean nbboProtectionFlag = true;
        heldOrderAPI.acceptHeldOrderReroute(heldOrderId,testSession,testProduct,nbboProtectionFlag);
    }

    private static void testRerouteHeldOrderByClass()
        throws SystemException,CommunicationException,AuthorizationException,
              DataValidationException,TransactionFailedException,NotAcceptedException
    {
        boolean nbboProtectionFlag = true;
        heldOrderAPI.acceptHeldOrderByClassReroute(testClass,testSession,nbboProtectionFlag);
    }

    private static void testAcceptCancelResponse()
        throws SystemException,CommunicationException,AuthorizationException,
              NotAcceptedException,TransactionFailedException,DataValidationException
    {
        OrderIdStruct orderId = null;
        CboeIdStruct cancelRequestId = null;
        heldOrderAPI.acceptCancelResponse(orderId,cancelRequestId,testSession,testProduct);
    }

    private static void testAcceptFillHeldOrder()
        throws SystemException,CommunicationException,AuthorizationException,
              NotAcceptedException,TransactionFailedException,DataValidationException
    {
        OrderIdStruct heldOrderId = null;
        OrderEntryStruct nbboAgentOrder = null;
        heldOrderAPI.acceptHeldOrderFill(heldOrderId,testSession,nbboAgentOrder);
    }

    private static void testGetHeldOrderById()
        throws SystemException,CommunicationException,AuthorizationException,DataValidationException,NotFoundException
    {
        OrderIdStruct heldOrderId = null;
        HeldOrderDetailStruct[] heldOrders = heldOrderAPI.getHeldOrdersByClassForSession(testSession, testClass);
        if (heldOrders.length > 0)
        {
            heldOrderId = heldOrders[0].heldOrder.order.orderId;
        }
        else
        {
            System.out.println("no order id exists to get held order.");
            return;
        }
        HeldOrderDetailStruct heldOrder = heldOrderAPI.getHeldOrderById(testSession, testProduct, heldOrderId);
    }

    private static void testGetHeldOrdersByClassForSession()
    {
        HeldOrderDetailStruct[] heldOrders = heldOrderAPI.getHeldOrdersByClassForSession(testSession, testClass);
        for (int i = 0; i<heldOrders.length; i++)
        {
           com.cboe.application.test.ReflectiveStructTester.printStruct(heldOrders[i], "HeldOrderDetailStruct["+i+"]:");
        }
    }

    public static void main(String[] args)
    {
        boolean     printStructOn = false;
        int         OrderId;
        APIHome.create(APIFactoryImpl.class);
        com.cboe.presentation.common.logging.GUILoggerHome.create(com.cboe.presentation.common.logging.MockGUILoggerImpl.class);
        System.setProperty("INTERMARKET_IOR_FILE",INTERMARKET_IOR_FILE);
        initConnection(args);
        System.out.println("Connecting to CAS");
        initIntermarketSession();
        channelListener = new TestIMTranslatorEventChannel();
        try {
           testRegister();
           System.out.println("Done with registrattion.  sessionName:"+testSession+" classKey:"+testClass);
           try {

	          Thread.currentThread().sleep(30000) ;

	        } catch ( java.lang.InterruptedException e ) {
	            System.out.println(e);
           }

           testUnRegister();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
