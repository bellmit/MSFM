package com.cboe.application.test;

import com.cboe.idl.cmi.UserAccess;
import com.cboe.idl.cmi.UserAccessHelper;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStateStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserAccessV3Helper;
import com.cboe.idl.cmiOrder.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class LoginTest
{
    static ORB orb;
    static POA rootPOA;

    static final int EXIT_SUCCESS = 0;
    static final int EXIT_ERROR = 1;

    UserLogonStruct userLogonStruct;
    UserAccessV3 userAccess;
    UserSessionManagerV3 userSessionManager;

    private static final int SLEEP_TIME = 30000; // 30 seconds

    // Properties for tailoring CAS specification
    private static final String PROP_CAS_IP = "Test.CasIp";
    private static final String PROP_CAS_PORT = "Test.CasPort";
    private static final String PROP_LOGIN_MODE = "Test.LoginMode";
    private static final String PROP_TRADING_SESSION = "Test.TradingSession";
    private static final String PROP_CLASS_NAME = "Test.ClassName";
    private static final String PROP_PRODUCT_TYPE = "Test.ProductType";

    // For creating URL to get initial IOR from CAS
    private static final String IP_LOCALHOST = "127.0.0.1";
    private static final int CAS_LOCATOR_PORT = 8003;
    private static final String IOR_REFERENCENAME = "/UserAccessV3.ior";

    private static String hostIp;
    private static int locatorPort;
    private static char sessionMode;

    // Parameters for logon
    private static final short SESSION_TYPE = LoginSessionTypes.PRIMARY;
    private static final boolean GMD_MESSAGING = true;

    // For subscribing to market data
    private static String sessionName;
    private static String className;
    private static short productType;

    public class UserSessionAdminCallback
        extends com.cboe.idl.cmiCallback.CMIUserSessionAdminPOA
    {        
        public HeartBeatStruct acceptHeartBeat(HeartBeatStruct heartbeat)
        {
            return heartbeat;
        }

        public void acceptLogout(String reason)
        {
            fail("CAS called acceptLogout(\"" + reason + "\")");
        }

        public void acceptTextMessage(MessageStruct message)
        {
            System.out.println("CAS sent message. messageKey:" + message.messageKey
                + " timeStamp:" + toString(message.timeStamp)
                + " sender:" + message.sender
                + " replyRequested:" + message.replyRequested
                + " messageText:" + message.messageText);
        }

        public void acceptAuthenticationNotice()
        {
            try
            {
                userSessionManager.authenticate(userLogonStruct);
            }
            catch (com.cboe.exceptions.SystemException e)
            {
                System.out.println(e);
                System.exit(e.details.error);
            }
            catch (com.cboe.exceptions.CommunicationException e)
            {
                System.out.println(e);
                System.exit(e.details.error);
            }
            catch (com.cboe.exceptions.AuthorizationException e)
            {
                System.out.println(e);
                System.exit(e.details.error);
            }
            catch (com.cboe.exceptions.AuthenticationException e)
            {
                System.out.println(e);
                System.exit(e.details.error);
            }
            catch (com.cboe.exceptions.DataValidationException e)
            {
                System.out.println(e);
                System.exit(e.details.error);
            }
        }

        public void acceptCallbackRemoval(CallbackInformationStruct callbackInfo, String reason, int errorCode)
        {
            System.out.println("CAS called acceptCallbackRemoval reason:" + reason
                + " errorCode:" + errorCode
                + " interface:" + callbackInfo.subscriptionInterface
                + " operation:" + callbackInfo.subscriptionOperation
                + " value:" + callbackInfo.subscriptionValue
                + " IOR:" + callbackInfo.ior);
        }

        String toString(DateTimeStruct dateTime)
        {
            StringBuilder result = new StringBuilder();
            result.append(dateTime.date.year).append('-')
                  .append(dateTime.date.month).append('-')
                  .append(dateTime.date.day).append('_')
                  .append(dateTime.time.hour).append(':');
            if (dateTime.time.minute < 10)
            {
                result.append('0');
            }
            result.append(dateTime.time.minute).append(':');
            if (dateTime.time.second < 10)
            {
                result.append('0');
            }
            result.append(dateTime.time.second).append('.');
            if (dateTime.time.fraction < 10)
            {
                result.append('0');
            }
            result.append(dateTime.time.fraction);
            return result.toString();
        }

    } // UserSessionAdminCallback

    public class TradingSessionStatusConsumerCallback
        extends com.cboe.idl.cmiCallback.CMITradingSessionStatusConsumerPOA
    {
        public void acceptTradingSessionState(TradingSessionStateStruct state)
        {
            // do nothing
        }
    } // TradingSessionStatusConsumerCallback

    public class CurrentMarketConsumerCallback
        extends com.cboe.idl.cmiCallback.CMICurrentMarketConsumerPOA
    {
        public void acceptCurrentMarket(CurrentMarketStruct[] currentMarket)
        {
            // do nothing
        }
    } // CurrentMarketConsumerCallback

    public class OrderStatusConsumerCallback
        extends com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerPOA
    {
        public void acceptOrderStatus(OrderDetailStruct[] orderDetailStructs, int i)
        {
            System.out.println("In Accept Order Status");
            for(OrderDetailStruct order : orderDetailStructs)
            {
                System.out.println("Order user id : " + order.orderStruct.userId);
            }
        }

        public void acceptOrderFilledReport(OrderFilledReportStruct orderFilledReportStruct, int i)
        {
            System.out.println("In Accept Order Fill Report");
            System.out.println("Filled Order user id : " + orderFilledReportStruct.filledOrder.orderStruct.userId);
        }

        public void acceptOrderCanceledReport(OrderCancelReportStruct orderCancelReportStruct, int i) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void acceptOrderBustReport(OrderBustReportStruct orderBustReportStruct, int i) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void acceptOrderBustReinstateReport(OrderBustReinstateReportStruct orderBustReinstateReportStruct, int i) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void acceptNewOrder(OrderDetailStruct orderDetailStruct, int i) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
    
    static void fail(String s)
    {
        System.out.println(s);
        System.exit(EXIT_ERROR);
    }

    static void fail(Exception e)
    {
        System.out.println("Exception message:" + e.getMessage());
        e.printStackTrace();
        System.exit(EXIT_ERROR);
    }

    void login(String userName)
    {
        UserSessionAdminCallback userSessionAdminCallback =
                new UserSessionAdminCallback();
        try
        {
            rootPOA.activate_object(userSessionAdminCallback);
        }
        catch (Exception e)
        {
            fail(e);
        }

        String userAccessIOR = null;
        try
        {
            URL url = new URL("http", hostIp, locatorPort, IOR_REFERENCENAME);
            URLConnection conn = url.openConnection();

            // Ignore http header lines, get content line
            BufferedReader in = new BufferedReader(new InputStreamReader((InputStream)conn.getContent()));
            userAccessIOR = in.readLine();
        }
        catch (Exception e)
        {
            fail(e);
        }
        userAccess = UserAccessV3Helper.narrow(orb.string_to_object(userAccessIOR));

        userLogonStruct = new UserLogonStruct();
        userLogonStruct.userId = userName;
        userLogonStruct.password = userName;
        userLogonStruct.version = Version.CMI_VERSION;
        userLogonStruct.loginMode = sessionMode;

        try
        {
            userSessionManager = userAccess.logon(userLogonStruct, SESSION_TYPE, userSessionAdminCallback._this(), GMD_MESSAGING);
        }
        catch (Exception e)
        {
            fail(e);
        }
    }

    void logout()
    {
        try {
            userSessionManager.logout();
        }
        catch (Exception e)
        {
            fail(e);
        }
    }

    void getSessions()
    {
        TradingSessionStatusConsumerCallback tradingSessionStatusConsumerCallback =
                new TradingSessionStatusConsumerCallback();
        try
        {
            rootPOA.activate_object(tradingSessionStatusConsumerCallback);
        }
        catch (Exception e)
        {
            fail(e);
        }

        TradingSessionStruct[] tradingSessions = null;
        try
        {
            tradingSessions = userSessionManager.getTradingSession().getCurrentTradingSessions(tradingSessionStatusConsumerCallback._this());
        }
        catch (Exception e)
        {
            fail(e);
        }

        // If session specified, make sure it's valid
        if (sessionName != null)
        {
            for (TradingSessionStruct ts : tradingSessions)
            {
                if (ts.sessionName.equals(sessionName))
                {
                    // Specified session is valid!
                    return;
                }
            }
        }
        fail("No such session: " + PROP_TRADING_SESSION + "=" + sessionName);
    }

    void subscribeMarketData()
    {
        if (sessionName == null || className == null)
        {
            // Without session and class, we won't try to subscribe.
            return;
        }

        try
        {
            CurrentMarketConsumerCallback currentMarketConsumerCallback = new CurrentMarketConsumerCallback();
            rootPOA.activate_object(currentMarketConsumerCallback);
            ClassStruct classStruct = userSessionManager.getProductQuery().getClassBySymbol(productType, className);
            userSessionManager.getMarketQuery().subscribeCurrentMarketForClass(sessionName, classStruct.classKey, currentMarketConsumerCallback._this());
        }
        catch (Exception e)
        {
            fail(e);
        }
    }

    void subscribeOrderStatusForFirmV2()
    {
        System.out.println("In subscribeOrderStatusForFirmV2");
	if (sessionName == null)
        {
            // Without session and class, we won't try to subscribe.
            return;
        }
	System.out.println("Subscribing for orderStatusForFirmV2");
	
        try
        {
            OrderStatusConsumerCallback orderStatusConsumerCallback = new OrderStatusConsumerCallback();
            rootPOA.activate_object(orderStatusConsumerCallback);
            //ClassStruct classStruct = userSessionManager.getProductQuery().getClassBySymbol(productType, className);
            userSessionManager.getOrderQueryV2().subscribeOrderStatusForFirmV2(orderStatusConsumerCallback._this(), true, true);
        }
        catch (Exception e)
        {
            fail(e);
        }
    }
    
    public void run(String userName)
    {
        login(userName);
        getSessions();
        subscribeMarketData();
	System.out.println("run: calling subscribeOrderStatusForFirmV2()");
	subscribeOrderStatusForFirmV2();
        try
        {
            Thread.sleep(SLEEP_TIME);
        }
        catch (InterruptedException ie)
        {
            System.out.println("Interrupted while sleeping for " + SLEEP_TIME + " milliseconds");
        }
        logout();
        System.out.println("Login test succeeded");
    }

    static void getArgs()
    {
        String s;

        s = System.getProperty(PROP_CAS_IP);
        hostIp = (s == null) ? IP_LOCALHOST : s;

        s = System.getProperty(PROP_CAS_PORT);
        locatorPort = (s == null) ? CAS_LOCATOR_PORT : Integer.parseInt(s);

        s = System.getProperty(PROP_LOGIN_MODE);
        if (s == null || s.startsWith("p") || s.startsWith("P"))
        {
            sessionMode = LoginSessionModes.PRODUCTION;
        }
        else if (s.startsWith("n") || s.startsWith("N"))
        {
            sessionMode = LoginSessionModes.NETWORK_TEST;
        }
        else // only option left is standalone
        {
            sessionMode = LoginSessionModes.STAND_ALONE_TEST;
        }

        sessionName = System.getProperty(PROP_TRADING_SESSION);

        className = System.getProperty(PROP_CLASS_NAME);

        s = System.getProperty(PROP_PRODUCT_TYPE);
        if (s == null || s.startsWith("o") || s.startsWith("O"))
        {
            productType = ProductTypes.OPTION;
        }
        else if (s.startsWith("e") || s.startsWith("E"))
        {
            productType = ProductTypes.EQUITY;
        }
        else // only other type we support is Future
        {
            productType = ProductTypes.FUTURE;
        }
    }

    public static void main(String[] args)
    {
        orb = org.omg.CORBA.ORB.init(args, null);
        getArgs();

        if (args.length != 1)
        {
            fail("Usage: LoginTest username");
        }
        String userName = args[0];

        try
        {
            rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            LoginTest tester = new LoginTest();
            tester.run(userName);
        }
        catch (Exception e)
        {
            fail(e);
        }
        System.exit(EXIT_SUCCESS);
    } // main
}
