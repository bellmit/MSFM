package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class UserAccess implements CasAccess, EngineAccess
{
    // Ask the CAS to turn a USER_ACCESS string into an IOR string

    private static final String USER_ACCESS_V1 = "/UserAccess.ior";
    private static final String USER_ACCESS_INTERMARKET =
                                                 "/IntermarketUserAccess.ior";
    private static final String USER_ACCESS_V2 = "/UserAccessV2.ior";
    private static final String USER_ACCESS_V3 = "/UserAccessV3.ior";
    private static final String USER_ACCESS_V4 = "/UserAccessV4.ior";
    private static final String USER_ACCESS_TMS = "/UserAccessTMS.ior";
    private static final String USER_ACCESS_V5 = "/UserAccessV5.ior";
    private static final String USER_ACCESS_V6 = "/UserAccessV6.ior";
    private static final String USER_ACCESS_V7 = "/UserAccessV7.ior";
    private static final String USER_ACCESS_V8 = "/UserAccessV8.ior";
    private static final String USER_ACCESS_V9 = "/UserAccessV9.ior";

    // userAccess.logon() returns a userSessionManager

    private com.cboe.idl.cmi.UserSessionManager     userSessionManagerV1;
    private com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager
                                                userSessionManagerIntermarket;
    private com.cboe.idl.cmiV2.UserSessionManagerV2 userSessionManagerV2;
    private com.cboe.idl.cmiV3.UserSessionManagerV3 userSessionManagerV3;
    private com.cboe.idl.cmiV4.UserSessionManagerV4 userSessionManagerV4;
    private com.cboe.idl.cmiTradeMaintenanceService.TMSUserSessionManager
                                                    userSessionManagerTMS;
    private com.cboe.idl.cmiV5.UserSessionManagerV5 userSessionManagerV5;
    private com.cboe.idl.cmiV6.UserSessionManagerV6 userSessionManagerV6;
    private com.cboe.idl.cmiV7.UserSessionManagerV7 userSessionManagerV7;
    private com.cboe.idl.cmiV8.UserSessionManagerV8 userSessionManagerV8;
    private com.cboe.idl.cmiV9.UserSessionManagerV9 userSessionManagerV9;

    // Our own useful variables

    private ORB orb;
    private POA rootPoa;
    private String casHost;
    private int casPort;
    private Struct structMaker;

    private UserLogonStruct userLogonStruct;
    private UserSessionAdmin userSessionAdmin;

    private com.cboe.idl.cmi.ProductQuery productQueryV1;

    private UserSessionManagerV1 scriptUserSessionManagerV1;
    private UserSessionManagerV3 scriptUserSessionManagerV3;
    private UserSessionManagerV4 scriptUserSessionManagerV4;
    private TMSUserSessionManager scriptTMSUserSessionManager;
    private UserSessionManagerV5 scriptUserSessionManagerV5;
    private UserSessionManagerV6 scriptUserSessionManagerV6;
    private UserSessionManagerV7 scriptUserSessionManagerV7;
    private UserSessionManagerV8 scriptUserSessionManagerV8;
    private UserSessionManagerV9 scriptUserSessionManagerV9;
    private AdministratorV1 scriptAdministratorV1;
    private FloorTradeMaintenanceServiceV6 scriptFloorTradeMaintenanceServiceV6;
    private IntermarketQueryV1 scriptIntermarketQueryV1;
    private MarketQueryV1 scriptMarketQueryV1;
    private MarketQueryV2 scriptMarketQueryV2;
    private MarketQueryV3 scriptMarketQueryV3;
    private MarketQueryV4 scriptMarketQueryV4;
    private NBBOAgentV1 scriptNBBOAgentV1;
    private OrderEntryV1 scriptOrderEntryV1;
    private OrderEntryV3 scriptOrderEntryV3;
    private OrderEntryV5 scriptOrderEntryV5;
    private OrderEntryV7 scriptOrderEntryV7;
    private OrderEntryV9 scriptOrderEntryV9;
    private OrderQueryV1 scriptOrderQueryV1;
    private OrderQueryV2 scriptOrderQueryV2;
    private OrderQueryV3 scriptOrderQueryV3;
    private OrderQueryV6 scriptOrderQueryV6;
    private ProductDefinitionV1 scriptProductDefinitionV1;
    private ProductQueryV1 scriptProductQueryV1;
    private QuoteV1 scriptQuoteV1;
    private QuoteV2 scriptQuoteV2;
    private QuoteV3 scriptQuoteV3;
    private QuoteV5 scriptQuoteV5;
    private QuoteV7 scriptQuoteV7;
    private TradeMaintenanceServiceV1 scriptTradeMaintenanceServiceV1;
    private TradingClassStatusQueryV8 scriptTradingClassStatusQueryV8;
    private TradingSessionV1 scriptTradingSessionV1;
    private UserHistoryV1 scriptUserHistoryV1;
    private UserPreferenceQueryV1 scriptUserPreferenceQueryV1;
    private UserTradingParametersV1 scriptUserTradingParametersV1;
    private UserTradingParametersV5 scriptUserTradingParametersV5;


    private static final int INDEX_FIRST_PARAMETER = 2;


    /** Connect to HTTP server at host:port and get an IOR.
     * @param host Name or IP address.
     * @param port Port number.
     * @param item Indicator of UserAccess object to obtain.
     * @return Inter-Operable Reference of UserAccess object.
     **/
    private String getIOR(String host, int port, String item)
    {
        String ior = null;
        try
        {
            URL url = new URL("http", host, port, item);
            URLConnection conn = url.openConnection();

            StringBuilder headers = new StringBuilder();
            int nHeaderFields = 0;
            for (;;)
            {
                String s = conn.getHeaderField(nHeaderFields);
                if (s == null)
                {
            /*for*/ break;
                }
                headers.append(s).append('\n');
                ++nHeaderFields;
            }

            BufferedReader in = new BufferedReader(
                new InputStreamReader( (InputStream)conn.getContent() )
            );

            ior = in.readLine();
            Log.message("getIOR host:" + host + " port:" + port
                + " item:" + item + " ==> " + ior);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
        return ior;
    }


    UserAccess(ORB o, POA root_poa, String host, int port)
    {
        orb = o;
        casHost = host;
        casPort = port;
        rootPoa = root_poa;
        objectStore = new HashMap<String, Object>();
        structMaker = new Struct(this);
    }

    /** Interpret and execute a script command.
     * @param command Parsed command line.
     **/
    public void dispatchCommand(String[] command)
    {
        String cmiServer = command[0];
        if (cmiServer.equalsIgnoreCase("struct"))
        {
            // struct structType name = key value [key value]...
            // example; struct UserLoginStruct uls = userId ABC password ABC
            Object o = structMaker.makeStruct(command);
            if (o != null)
            {
                objectStore.put(command[2], o);
            }
        }
        else if (cmiServer.startsWith("UserAccess"))
        {
            doCommand(command);
        }
        else if (cmiServer.equalsIgnoreCase("Administrator"))
        {
            if (scriptAdministratorV1 == null)
            {
                Log.message("No Administrator object");
            }
            else
            {
                scriptAdministratorV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("FloorTradeMaintenanceServiceV6"))
        {
            if (scriptFloorTradeMaintenanceServiceV6 == null)
            {
                Log.message("No FloorTradeMaintenanceServiceV6 object");
            }
            else
            {
                scriptFloorTradeMaintenanceServiceV6.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("IntermarketManualHandling"))
        {
            // Not a copy/paste error, this is implemented in NBBOAgentV1.
            if (scriptNBBOAgentV1 == null)
            {
                Log.message("No NBBOAgent object");
            }
            else
            {
                scriptNBBOAgentV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("IntermarketQuery"))
        {
            if (scriptIntermarketQueryV1 == null)
            {
                Log.message("No IntermarketQuery object");
            }
            else
            {
                scriptIntermarketQueryV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("MarketQuery"))
        {
            if (scriptMarketQueryV1 == null)
            {
                Log.message("No MarketQuery object");
            }
            else
            {
                scriptMarketQueryV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("MarketQueryV2"))
        {
            if (scriptMarketQueryV2 == null)
            {
                Log.message("No MarketQueryV2 object");
            }
            else
            {
                scriptMarketQueryV2.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("MarketQueryV3"))
        {
            if (scriptMarketQueryV3 == null)
            {
                Log.message("No MarketQueryV3 object");
            }
            else
            {
                scriptMarketQueryV3.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("MarketQueryV4"))
        {
            if (scriptMarketQueryV4 == null)
            {
                Log.message("No MarketQueryV4 object");
            }
            else
            {
                scriptMarketQueryV4.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("NBBOAgent"))
        {
            if (scriptNBBOAgentV1 == null)
            {
                Log.message("No NBBOAgent object");
            }
            else
            {
                scriptNBBOAgentV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderEntry"))
        {
            if (scriptOrderEntryV1 == null)
            {
                Log.message("No OrderEntry object");
            }
            else
            {
                scriptOrderEntryV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderEntryV3"))
        {
            if (scriptOrderEntryV3 == null)
            {
                Log.message("No OrderEntryV3 object");
            }
            else
            {
                scriptOrderEntryV3.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderEntryV5"))
        {
            if (scriptOrderEntryV5 == null)
            {
                Log.message("No OrderEntryV5 object");
            }
            else
            {
                scriptOrderEntryV5.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderEntryV7"))
        {
            if (scriptOrderEntryV7 == null)
            {
                Log.message("No OrderEntryV7 object");
            }
            else
            {
                scriptOrderEntryV7.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderEntryV9"))
        {
            if (scriptOrderEntryV9 == null)
            {
                Log.message("No OrderEntryV9 object");
            }
            else
            {
                scriptOrderEntryV9.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderQuery"))
        {
            if (scriptOrderQueryV1 == null)
            {
                Log.message("No OrderQuery object");
            }
            else
            {
                scriptOrderQueryV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderQueryV2"))
        {
            if (scriptOrderQueryV2 == null)
            {
                Log.message("No OrderQueryV2 object");
            }
            else
            {
                scriptOrderQueryV2.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderQueryV3"))
        {
            if (scriptOrderQueryV3 == null)
            {
                Log.message("No OrderQueryV3 object");
            }
            else
            {
                scriptOrderQueryV3.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("OrderQueryV6"))
        {
            if (scriptOrderQueryV6 == null)
            {
                Log.message("No OrderQueryV6 object");
            }
            else
            {
                scriptOrderQueryV6.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("ProductDefinition"))
        {
            if (scriptProductDefinitionV1 == null)
            {
                Log.message("No ProductDefinition object");
            }
            else
            {
                scriptProductDefinitionV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("ProductQuery"))
        {
            if (scriptProductQueryV1 == null)
            {
                Log.message("No ProductQuery object");
            }
            else
            {
                scriptProductQueryV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("Quote"))
        {
            if (scriptQuoteV1 == null)
            {
                Log.message("No Quote object");
            }
            else
            {
                scriptQuoteV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("QuoteV2"))
        {
            if (scriptQuoteV2 == null)
            {
                Log.message("No QuoteV2 object");
            }
            else
            {
                scriptQuoteV2.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("QuoteV3"))
        {
            if (scriptQuoteV3 == null)
            {
                Log.message("No QuoteV3 object");
            }
            else
            {
                scriptQuoteV3.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("QuoteV5"))
        {
            if (scriptQuoteV5 == null)
            {
                Log.message("No QuoteV5 object");
            }
            else
            {
                scriptQuoteV5.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("QuoteV7"))
        {
            if (scriptQuoteV7 == null)
            {
                Log.message("No QuoteV7 object");
            }
            else
            {
                scriptQuoteV7.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("TradeMaintenanceService"))
        {
            if (scriptTradeMaintenanceServiceV1 == null)
            {
                Log.message("No TradeMaintenanceService object");
            }
            else
            {
                scriptTradeMaintenanceServiceV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("TradingClassStatusQueryV8"))
        {
            if (scriptTradingClassStatusQueryV8 == null)
            {
                Log.message("No TradingClassStatusQueryV8 object");
            }
            else
            {
                scriptTradingClassStatusQueryV8.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("TradingSession"))
        {
            if (scriptTradingSessionV1 == null)
            {
                Log.message("No TradingSession object");
            }
            else
            {
                scriptTradingSessionV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserHistory"))
        {
            if (scriptUserHistoryV1 == null)
            {
                Log.message("No UserHistory object");
            }
            else
            {
                scriptUserHistoryV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserPreferenceQuery"))
        {
            if (scriptUserPreferenceQueryV1 == null)
            {
                Log.message("No UserPreferenceQuery object");
            }
            else
            {
                scriptUserPreferenceQueryV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserSessionManager"))
        {
            if (scriptUserSessionManagerV1 == null)
            {
                Log.message("No UserSessionManager object");
            }
            else
            {
                scriptUserSessionManagerV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserSessionManagerV3"))
        {
            if (scriptUserSessionManagerV3 == null)
            {
                Log.message("No UserSessionManagerV3 object");
            }
            else
            {
                scriptUserSessionManagerV3.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserSessionManagerV4"))
        {
            if (scriptUserSessionManagerV4 == null)
            {
                Log.message("No UserSessionManagerV4 object");
            }
            else
            {
                scriptUserSessionManagerV4.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("TMSUserSessionManager"))
        {
            if (scriptTMSUserSessionManager == null)
            {
                Log.message("No TMSUserSessionManager object");
            }
            else
            {
                scriptTMSUserSessionManager.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserSessionManagerV5"))
        {
            if (scriptUserSessionManagerV5 == null)
            {
                Log.message("No UserSessionManagerV5 object");
            }
            else
            {
                scriptUserSessionManagerV5.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserSessionManagerV6"))
        {
            if (scriptUserSessionManagerV6 == null)
            {
                Log.message("No UserSessionManagerV6 object");
            }
            else
            {
                scriptUserSessionManagerV6.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserSessionManagerV7"))
        {
            if (scriptUserSessionManagerV7 == null)
            {
                Log.message("No UserSessionManagerV7 object");
            }
            else
            {
                scriptUserSessionManagerV7.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserSessionManagerV8"))
        {
            if (scriptUserSessionManagerV8 == null)
            {
                Log.message("No UserSessionManagerV8 object");
            }
            else
            {
                scriptUserSessionManagerV8.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserSessionManagerV9"))
        {
            if (scriptUserSessionManagerV9 == null)
            {
                Log.message("No UserSessionManagerV9 object");
            }
            else
            {
                scriptUserSessionManagerV9.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserTradingParameters"))
        {
            if (scriptUserTradingParametersV1 == null)
            {
                Log.message("No UserTradingParameters object");
            }
            else
            {
                scriptUserTradingParametersV1.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("UserTradingParametersV5"))
        {
            if (scriptUserTradingParametersV5 == null)
            {
                Log.message("No UserTradingParametersV5 object");
            }
            else
            {
                scriptUserTradingParametersV5.doCommand(command);
            }
        }
        else if (cmiServer.equalsIgnoreCase("wait"))
        {
            doWait(command);
        }
        else
        {
            Log.message("Unknown command");
        }
    }


    /** Log on to CAS using Version 1 interface. Set up all services
     * available to a Version 1 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV1(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType,boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V1);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmi.UserAccess userAccessV1 =
                    com.cboe.idl.cmi.UserAccessHelper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerV1 = userAccessV1.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesV1();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV1()
    {
        try
        {
            com.cboe.idl.cmi.Administrator administratorV1 = userSessionManagerV1.getAdministrator();
            com.cboe.idl.cmi.MarketQuery marketQueryV1 = userSessionManagerV1.getMarketQuery();
            com.cboe.idl.cmi.OrderEntry orderEntryV1 = userSessionManagerV1.getOrderEntry();
            com.cboe.idl.cmi.OrderQuery orderQueryV1 = userSessionManagerV1.getOrderQuery();
            com.cboe.idl.cmi.ProductDefinition productDefinitionV1 = userSessionManagerV1.getProductDefinition();
            productQueryV1 = userSessionManagerV1.getProductQuery();
            com.cboe.idl.cmi.Quote quoteV1 = userSessionManagerV1.getQuote();
            com.cboe.idl.cmi.TradingSession tradingSessionV1 = userSessionManagerV1.getTradingSession();
            com.cboe.idl.cmi.UserHistory userHistoryV1 = userSessionManagerV1.getUserHistory();
            com.cboe.idl.cmi.UserPreferenceQuery userPreferenceQueryV1 = userSessionManagerV1.getUserPreferenceQuery();
            com.cboe.idl.cmi.UserTradingParameters userTradingParametersV1 = userSessionManagerV1.getUserTradingParameters();

            scriptAdministratorV1 = new AdministratorV1(this, administratorV1);
            scriptMarketQueryV1 = new MarketQueryV1(this, marketQueryV1);
            scriptOrderEntryV1 = new OrderEntryV1(this, orderEntryV1);
            scriptOrderQueryV1 = new OrderQueryV1(this, orderQueryV1);
            scriptProductDefinitionV1 = new ProductDefinitionV1(this, productDefinitionV1);
            scriptProductQueryV1 = new ProductQueryV1(this, productQueryV1);
            scriptQuoteV1 = new QuoteV1(this, quoteV1);
            scriptTradingSessionV1 = new TradingSessionV1(this, tradingSessionV1);
            scriptUserHistoryV1 = new UserHistoryV1(this, userHistoryV1);
            scriptUserPreferenceQueryV1 = new UserPreferenceQueryV1(this, userPreferenceQueryV1);
            scriptUserTradingParametersV1 = new UserTradingParametersV1(this, userTradingParametersV1);
            scriptUserSessionManagerV1 = new UserSessionManagerV1(this, userSessionManagerV1);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void logonIntermarket(
        String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_INTERMARKET);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiIntermarket.IntermarketUserAccess
                    userAccessIntermarket = com.cboe.idl.cmiIntermarket
                            .IntermarketUserAccessHelper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            com.cboe.idl.cmiIntermarket.IntermarketSessionManagerStruct sm =
                userAccessIntermarket.logon(userLogonStruct,
                    sessionType, userSessionAdmin._this(), gmdTextMessaging);
            userSessionManagerV1 = sm.sessionManager;
            userSessionManagerIntermarket = sm.imSessionManager;

            getServicesV1();
            getServicesIntermarket();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesIntermarket()
    {
        try
        {
            com.cboe.idl.cmiIntermarket.IntermarketQuery intermarketQueryV1 =
                    userSessionManagerIntermarket.getIntermarketQuery();
            com.cboe.idl.cmiIntermarket.NBBOAgent nbboAgentV1 =
                    userSessionManagerIntermarket.getNBBOAgent();

            scriptIntermarketQueryV1 =
                    new IntermarketQueryV1(this, intermarketQueryV1);
            scriptNBBOAgentV1 = new NBBOAgentV1(this, this, nbboAgentV1);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    /** Log on to CAS using Version 2 interface. Set up all services
     * available to a Version 2 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV2(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V2);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiV2.UserAccessV2 userAccessV2 =
                    com.cboe.idl.cmiV2.UserAccessV2Helper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            com.cboe.idl.cmiV2.SessionManagerStructV2 sm =
                userAccessV2.logon(userLogonStruct,
                    sessionType, userSessionAdmin._this(), gmdTextMessaging);
            userSessionManagerV1 = sm.sessionManager;
            userSessionManagerV2 = sm.sessionManagerV2;

            getServicesV2();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV2()
    {
        getServicesV1();
        try
        {
            com.cboe.idl.cmiV2.OrderQuery orderQueryV2 =
                    userSessionManagerV2.getOrderQueryV2();
            com.cboe.idl.cmiV2.Quote quoteV2 =
                    userSessionManagerV2.getQuoteV2();
            com.cboe.idl.cmiV2.MarketQuery marketQueryV2 =
                    userSessionManagerV2.getMarketQueryV2();

            scriptOrderQueryV2 = new OrderQueryV2(this, orderQueryV2);            
            scriptQuoteV2 = new QuoteV2(this, quoteV2);
            scriptMarketQueryV2 = new MarketQueryV2(this, marketQueryV2);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    /** Log on to CAS using Version 3 interface. Set up all services
     * available to a Version 3 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV3(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V3);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiV3.UserAccessV3 userAccessV3 =
                    com.cboe.idl.cmiV3.UserAccessV3Helper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerV3 = userAccessV3.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesV3();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV3()
    {
        userSessionManagerV1 = userSessionManagerV3;
        userSessionManagerV2 = userSessionManagerV3;
        getServicesV2();
        try
        {
            com.cboe.idl.cmiV3.MarketQuery marketQueryV3 =
                    userSessionManagerV3.getMarketQueryV3();
            com.cboe.idl.cmiV3.OrderEntry orderEntryV3 =
                    userSessionManagerV3.getOrderEntryV3();
            com.cboe.idl.cmiV3.OrderQuery orderQueryV3 =
                    userSessionManagerV3.getOrderQueryV3();
            com.cboe.idl.cmiV3.Quote quoteV3 =
                    userSessionManagerV3.getQuoteV3();

            scriptMarketQueryV3 = new MarketQueryV3(this, marketQueryV3);
            scriptOrderEntryV3 = new OrderEntryV3(this, orderEntryV3);
            scriptOrderQueryV3 = new OrderQueryV3(this, orderQueryV3);
            scriptQuoteV3 = new QuoteV3(this, quoteV3);
            scriptUserSessionManagerV3 =
                    new UserSessionManagerV3(this, userSessionManagerV3);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    /** Log on to CAS using Version 4 interface. Set up all services
     * available to a Version 4 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV4(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V4);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiV4.UserAccessV4 userAccessV4 =
                    com.cboe.idl.cmiV4.UserAccessV4Helper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerV4 = userAccessV4.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesV4();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV4()
    {
        userSessionManagerV3 = userSessionManagerV4;
        getServicesV3();

        try
        {
            com.cboe.idl.cmiV4.MarketQuery marketQueryV4 =
                    userSessionManagerV4.getMarketQueryV4();

            scriptMarketQueryV4 = new MarketQueryV4(this, marketQueryV4);
            scriptUserSessionManagerV4 =
                    new UserSessionManagerV4(this, userSessionManagerV4);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void logonTMS(String host, int port,
            UserLogonStruct userLogonStruct, short sessionType,
            boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_TMS);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiTradeMaintenanceService.UserAccessTMS
                    userAccessTMS = com.cboe.idl.cmiTradeMaintenanceService
                            .UserAccessTMSHelper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerTMS = userAccessTMS.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesTMS();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesTMS()
    {
        userSessionManagerV4 = userSessionManagerTMS;
        getServicesV4();

        try
        {
            com.cboe.idl.cmiTradeMaintenanceService.TradeMaintenanceService
                    tradeMaintenanceServiceV1 =
                    userSessionManagerTMS.getTradeMaintenanceService();

            scriptTradeMaintenanceServiceV1 = new TradeMaintenanceServiceV1(
                    this, tradeMaintenanceServiceV1);
            scriptTMSUserSessionManager = new TMSUserSessionManager(this,
                    userSessionManagerTMS);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    /** Log on to CAS using Version 5 interface. Set up all services
     * available to a Version 5 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV5(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V5);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiV5.UserAccessV5 userAccessV5 =
                    com.cboe.idl.cmiV5.UserAccessV5Helper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerV5 = userAccessV5.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesV5();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV5()
    {
        userSessionManagerTMS = userSessionManagerV5;
        getServicesTMS();

        try
        {
            com.cboe.idl.cmiV5.OrderEntry orderEntryV5 =
                    userSessionManagerV5.getOrderEntryV5();
            com.cboe.idl.cmiV5.Quote quoteV5 =
                    userSessionManagerV5.getQuoteV5();
            com.cboe.idl.cmiV5.UserTradingParameters userTradingParametersV5 =
                    userSessionManagerV5.getUserTradingParametersV5();

            scriptOrderEntryV5 = new OrderEntryV5(this, orderEntryV5);
            scriptUserTradingParametersV5 =
                    new UserTradingParametersV5(this, userTradingParametersV5);
            scriptQuoteV5 = new QuoteV5(this, quoteV5);
            scriptUserSessionManagerV5 =
                    new UserSessionManagerV5(this, userSessionManagerV5);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    /** Log on to CAS using Version 6 interface. Set up all services
     * available to a Version 6 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV6(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V6);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiV6.UserAccessV6 userAccessV6 =
                    com.cboe.idl.cmiV6.UserAccessV6Helper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerV6 = userAccessV6.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesV6();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV6()
    {
        userSessionManagerV5 = userSessionManagerV6;
        getServicesV5();

        try
        {
            com.cboe.idl.cmiV6.OrderQuery orderQueryV6 =
                    userSessionManagerV6.getOrderQueryV6();
            com.cboe.idl.cmiV6.FloorTradeMaintenanceService
                    floorTradeMaintenanceServiceV6 =
                    userSessionManagerV6.getFloorTradeMaintenanceService();

            scriptOrderQueryV6 = new OrderQueryV6(this, orderQueryV6);
            scriptFloorTradeMaintenanceServiceV6 =
                    new FloorTradeMaintenanceServiceV6(
                            this, floorTradeMaintenanceServiceV6);
            scriptUserSessionManagerV6 =
                    new UserSessionManagerV6(this, userSessionManagerV6);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    /** Log on to CAS using Version 7 interface. Set up all services
     * available to a Version 7 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV7(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V7);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiV7.UserAccessV7 userAccessV7 =
                    com.cboe.idl.cmiV7.UserAccessV7Helper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerV7 = userAccessV7.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesV7();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV7()
    {
        userSessionManagerV6 = userSessionManagerV7;
        getServicesV6();

        try
        {
            com.cboe.idl.cmiV7.OrderEntry orderEntryV7 =
                    userSessionManagerV7.getOrderEntryV7();
            com.cboe.idl.cmiV7.Quote quoteV7 =
                    userSessionManagerV7.getQuoteV7();

            scriptOrderEntryV7 = new OrderEntryV7(this, orderEntryV7);
            scriptQuoteV7 = new QuoteV7(this, quoteV7);
            scriptUserSessionManagerV7 =
                    new UserSessionManagerV7(this, userSessionManagerV7);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    /** Log on to CAS using Version 8 interface. Set up all services
     * available to a Version 8 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV8(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V8);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiV8.UserAccessV8 userAccessV8 =
                    com.cboe.idl.cmiV8.UserAccessV8Helper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerV8 = userAccessV8.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesV8();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV8()
    {
        userSessionManagerV7 = userSessionManagerV8;
        getServicesV7();

        try
        {
            com.cboe.idl.cmiV8.TradingClassStatusQuery
                    tradingClassStatusQueryV8 =
                    userSessionManagerV8.getTradingClassStatusQuery();

            scriptTradingClassStatusQueryV8 = new TradingClassStatusQueryV8(
                    this, tradingClassStatusQueryV8);
            scriptUserSessionManagerV8 =
                    new UserSessionManagerV8(this, userSessionManagerV8);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    /** Log on to CAS using Version 9 interface. Set up all services
     * available to a Version 9 login.
     * @param host CAS Host name or IP address.
     * @param port Port number that CAS is listening to.
     * @param userLogonStruct Detail of login request.
     * @param sessionType From cmiUser.LoginSessionMode
     * @param gmdTextMessaging Want Guaranteed Message Delivery.
     **/
    private void logonV9(String host, int port, UserLogonStruct userLogonStruct,
        short sessionType, boolean gmdTextMessaging)
    {
        try
        {
            this.userLogonStruct = userLogonStruct;

            String ior = getIOR(host, port, USER_ACCESS_V9);
            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            com.cboe.idl.cmiV9.UserAccessV9 userAccessV9 =
                    com.cboe.idl.cmiV9.UserAccessV9Helper.narrow(objRef);

            userSessionAdmin = new UserSessionAdmin(this);
            associateWithOrb(userSessionAdmin);

            userSessionManagerV9 = userAccessV9.logon(userLogonStruct,
                sessionType, userSessionAdmin._this(), gmdTextMessaging);

            // get all service objects from CAS

            getServicesV9();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void getServicesV9()
    {
        userSessionManagerV8 = userSessionManagerV9;
        getServicesV8();

        try
        {
            com.cboe.idl.cmiV9.OrderEntry orderEntryV9 =
                    userSessionManagerV9.getOrderEntryV9();

            scriptOrderEntryV9 = new OrderEntryV9(this, orderEntryV9);
            scriptUserSessionManagerV9 =
                    new UserSessionManagerV9(this, userSessionManagerV9);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

// EngineAccess interface

    /** After creating a CORBA Servant object, associate it with an ORB.
     * @param servant The CORBA Servant object.
     */
    public void associateWithOrb(Servant servant)
    {
        try
        {
            rootPoa.activate_object(servant);
        }
        catch (ServantAlreadyActive saa)
        {
            Log.throwable(saa);
        }
        catch (WrongPolicy wp)
        {
            Log.throwable(wp);
        }
    }

    /** Get the named object from the object store.
     * @param objName Name of the object to get.
     * @return Object from the store, or null if not found.
     */
    public Object getObjectFromStore(String objName)
    {
        return (objName == null) ? null : objectStore.get(objName);
    }

    /** Extract values from a list of strings.
     * @param parmName List of names to identify values.
     * @param command List of name value name value ...
     * @param startIndex Index into command of first name in list.
     * @return List of values corresponding to items in parmName[], or null on
     *    error. If a name in parmName[] does not appear in command[], the
     *    corresponding returned value will be null.
     */
    public String[] getParameters(String parmName[], String command[], int startIndex)
    {
        String value[] = new String[parmName.length];
        int commandIndex = startIndex;

        while (commandIndex < command.length)
        {
            if (commandIndex == command.length-1)
            {
                Log.message("No value supplied for parameter:" + command[commandIndex]);
                return null;
            }
            boolean found = false;
            for (int nameIndex = 0; nameIndex < parmName.length && !found; ++ nameIndex)
            {
                if (command[commandIndex].equalsIgnoreCase(parmName[nameIndex]))
                {
                    value[nameIndex] = command[commandIndex+1];
                    found = true;
                }
            }
            if (found)
            {
                // advance over name and value, ready for next pair
                commandIndex += 2;
            }
            else
            {
                Log.message("Unknown parameter:" + command[commandIndex]);
                return null;
            }
        }
        return value;
    }

// CasAccess interface

    public void reauthenticate()
    {
        try
        {
            if (userSessionManagerV1 != null)
            {
                userSessionManagerV1.authenticate(userLogonStruct);
            }
            else if (userSessionManagerIntermarket != null)
            {
                userSessionManagerV1.authenticate(userLogonStruct);
            }
            else if (userSessionManagerV2 != null)
            {
                userSessionManagerV1.authenticate(userLogonStruct);
            }
            else if (userSessionManagerV3 != null)
            {
                userSessionManagerV3.authenticate(userLogonStruct);
            }
            else if (userSessionManagerV4 != null)
            {
                userSessionManagerV4.authenticate(userLogonStruct);
            }
            else if (userSessionManagerTMS != null)
            {
                userSessionManagerTMS.authenticate(userLogonStruct);
            }
            else if (userSessionManagerV5 != null)
            {
                userSessionManagerV5.authenticate(userLogonStruct);
            }
            else if (userSessionManagerV6 != null)
            {
                userSessionManagerV6.authenticate(userLogonStruct);
            }
            else if (userSessionManagerV7 != null)
            {
                userSessionManagerV7.authenticate(userLogonStruct);
            }
            else if (userSessionManagerV8 != null)
            {
                userSessionManagerV8.authenticate(userLogonStruct);
            }
            else
            {
                Log.message("No userSessionManager available");
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }

    }

    private HashMap<Integer, Integer> mapClassToProduct =
            new HashMap<Integer, Integer>();

    public int productKeyToClassKey(int productKey) throws Throwable
    {
        // If we looked this up before, we don't need to query the CAS.
        Integer classKey = mapClassToProduct.get(productKey);
        if (classKey != null)
        {
            return classKey;
        }
        ProductStruct p = productQueryV1.getProductByKey(productKey);
        mapClassToProduct.put(productKey, p.productKeys.classKey);
        return p.productKeys.classKey;
    }

////////// Internal commands //////////

    private void doWait(String command[])
    {
        if (command.length != 2)
        {
            Log.message("Need 1 argument, milliseconds to wait");
            return;
        }
        try
        {
            int msec = Integer.parseInt(command[1]);
            Thread.sleep(msec);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

////////// Executing command line for UserAccessVn //////////

    private HashMap<String, Object> objectStore;

    /** Execute a command on a UserAccess object.
     * @param command Words from command line: UserAccessVn function args...
     **/
    public void doCommand(String command[])
    {
        // For all versions of UserAccess there is only one function, logon.
        // Command line is
        // 0: UserAccessVn
        // 1: logon
        // 2: logonStruct
        // 3: sessionType
        // 4: gmdTextMessaging

        if (command.length < 2)
        {
            Log.message("Command line must have at least object, function");
            return;
        }

        if (command[1].equalsIgnoreCase("logon"))
        {
            doLogon(command);
        }
        else
        {
            Log.message("Unknown function for " + command[0]);
        }
    }

    private void doLogon(String command[])
    {
        String names[] = { "logonStruct", "sessionType", "gmdTextMessage" };
        String values[] = getParameters(names, command, INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            // getParameters reported an error, leave now.
            return;
        }

        String s = values[0];
        if (s == null)
        {
            Log.message("Missing logonStruct");
            return;
        }
        Object o = objectStore.get(s);
        if (o == null)
        {
            Log.message("Can't find UserLogonStruct:" + s);
            return;
        }
        UserLogonStruct userLogonStruct;
        if (o instanceof UserLogonStruct)
        {
            userLogonStruct = (UserLogonStruct) o;
        }
        else
        {
            Log.message("Object is not UserLogonStruct:" + s);
            return;
        }

        s = values[1];
        if (s == null)
        {
            Log.message("Missing sessionType");
            return;
        }
        if (s.length() != 1)
        {
            Log.message("Invalid sessionType:" + s);
            return;
        }
        short sessionType = Short.valueOf(s);

        s = values[2];
        if (s == null)
        {
            Log.message("Missing gmdTextMessage");
            return;
        }
        boolean gmdTextMessage = CommandLine.booleanValue(s);

        s = command[0];
        if (s.equals("UserAccessV1"))
        {
            logonV1(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessIntermarket"))
        {
            logonIntermarket(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessV2"))
        {
            logonV2(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessV3"))
        {
            logonV3(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessV4"))
        {
            logonV4(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessTMS"))
        {
            logonTMS(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessV5"))
        {
            logonV5(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessV6"))
        {
            logonV6(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessV7"))
        {
            logonV7(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessV8"))
        {
            logonV8(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
        else if (s.equals("UserAccessV9"))
        {
            logonV9(casHost, casPort, userLogonStruct, sessionType, gmdTextMessage);
        }
    }

}
