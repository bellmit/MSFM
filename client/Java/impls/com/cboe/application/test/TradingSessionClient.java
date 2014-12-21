package com.cboe.application.test;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiIntermarket.IntermarketSessionManagerStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.domain.util.*;
import com.cboe.application.cas.TestUserAccessV2Factory;
import com.cboe.application.cas.TestCallback;
import com.cboe.application.shared.RemoteConnection;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.callback.UserSessionAdminConsumerDelegate;
import com.cboe.delegates.callback.QuoteStatusConsumerDelegate;
import com.cboe.delegates.callback.RFQConsumerDelegate;
import com.cboe.util.event.EventChannelAdapterFactory;
import java.util.Properties;
import java.io.FileInputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

public class TradingSessionClient
{

    public static final String SYSTEM_SECTION = "System";
    public static final String SETUP_SETTINGS = "Setup";
    public static final String USER = "User";
    public static final String PASSWORD = "Password";
    public static final String LOGIN_MODE = "LoginOperationMode";
    public static final String LOGIN_SESSION = "SessionLoginType";
    public static final String SESSION_NAME = "SessionName";
    public static final String PRODUCT_TYPE = "ProductType";
    public static final String CLASS_SYMBOL = "ClassSymbol";
    public static final String GMD = "gmd";
    public static final String PROPERTY_FILE = "PropertyFile";

    public static RemoteConnection connection;
    public static SessionManagerStructV2 sessionManagerV2;
    public static SessionManagerStructV2 userSessionManager;
    public static UserSessionManager session;
    public static UserSessionManagerV2 sessionV2;
    public static IntermarketSessionManagerStruct imSessionStruct;
    public static ProductTypeStruct[] productTypes;
    public static SessionProductStruct[] products;
    public static SessionClassStruct[] classes;
    public static TradingSessionStruct[] sessions;
    public static TestCallback callback;
    public static int[]  groups = {1};
    private static POA poaReference;
    private static UserAccessV2 userAccessV2;
    private static Properties properties;

    private static boolean loggedIn;
    private static String classSymbol;
    private static String sessionName;
    private static int classKey;
    private static int productKey;
    private static short productType;

    private static double currentPrice;

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

    /** initializes the ORB connection object */
    public static void initORBConnection(String[] args)
    {
        if ( connection == null )
        {
            connection = RemoteConnectionFactory.create(args);
            EventChannelAdapterFactory.find().setDynamicChannels(true);
        }
    }

    private static UserAccessV2 getUserAccessV2()
    {
        if(userAccessV2 == null)
        {
            userAccessV2 = TestUserAccessV2Factory.find();
        }
        return userAccessV2;
    }

    public static TestCallback getCallback() {
        if (callback == null) {
            callback = new TestCallback();
        }
        return callback;
    }

    private static CMIUserSessionAdmin getUserSessionAdmin()
    {

        TestCallback callbackConsumer = getCallback();
        UserSessionAdminConsumerDelegate clientListener = new UserSessionAdminConsumerDelegate(callbackConsumer);
        try {
        org.omg.CORBA.Object orbObject = getPOA().servant_to_reference(clientListener);
            CMIUserSessionAdmin cmiCallback = CMIUserSessionAdminHelper.narrow(orbObject);
            return cmiCallback;
        } catch ( Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private static CMIQuoteStatusConsumer getQuoteConsumer()
    {
        QuoteStatusConsumerDelegate delegate= new QuoteStatusConsumerDelegate(getCallback());
        org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
        CMIQuoteStatusConsumer theQuoteConsumer = CMIQuoteStatusConsumerHelper.narrow (corbaObject);
        return  theQuoteConsumer;
    }

    private static com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer getQuoteV2Consumer()
    {
        com.cboe.idl.cmiCallbackV2.POA_CMIQuoteStatusConsumer_tie delegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMIQuoteStatusConsumer_tie(getCallback());
        org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateV2);
        com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer quoteStatusV2Consumer = com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerHelper.narrow (corbaObject);
        return quoteStatusV2Consumer;
    }

    private static CMILockedQuoteStatusConsumer getLockedQuoteV2Consumer()
    {
        com.cboe.idl.cmiCallbackV2.POA_CMILockedQuoteStatusConsumer_tie myDelegate= new com.cboe.idl.cmiCallbackV2.POA_CMILockedQuoteStatusConsumer_tie(getCallback());
        org.omg.CORBA.Object myObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (myDelegate);
        CMILockedQuoteStatusConsumer theLockConsumer = com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumerHelper.narrow(myObject);
        return theLockConsumer;
    }

    private static CMIRFQConsumer getRFQConsumer()
    {
          RFQConsumerDelegate delegateRFQ= new RFQConsumerDelegate(getCallback());
               org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateRFQ);
               CMIRFQConsumer rfqConsumer = CMIRFQConsumerHelper.narrow (corbaObject);
               return rfqConsumer;
    }

    private static com.cboe.idl.cmiCallbackV2.CMIRFQConsumer getRFQV2Consumer()
    {
        com.cboe.idl.cmiCallbackV2.POA_CMIRFQConsumer_tie delegateRFQV2= new com.cboe.idl.cmiCallbackV2.POA_CMIRFQConsumer_tie(getCallback());
               org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateRFQV2);
               com.cboe.idl.cmiCallbackV2.CMIRFQConsumer rfqV2Consumer = com.cboe.idl.cmiCallbackV2.CMIRFQConsumerHelper.narrow (corbaObject);
    return rfqV2Consumer;
    }

     private static void logon() throws AuthenticationException, DataValidationException, SystemException, CommunicationException, AuthorizationException, NotFoundException
     {
         UserLogonStruct userLogonStruct = new UserLogonStruct(
                 properties.getProperty(USER).toString(),
                 properties.getProperty(PASSWORD).toString(),
                 Version.CMI_VERSION,
                 properties.getProperty(LOGIN_MODE).toString().charAt(0));
         short sessionType = Short.parseShort(properties.getProperty(LOGIN_SESSION).toString());
         boolean gmd = false;
         if ( Short.parseShort(properties.getProperty(GMD).toString()) == 1)
            gmd = true;
         else
            gmd = false;
         userSessionManager = getUserAccessV2().logon(userLogonStruct, sessionType, getUserSessionAdmin(), gmd);
         sessionV2 = userSessionManager.sessionManagerV2;
         session =  userSessionManager.sessionManager;
         loggedIn = true;
     }

     private static Properties loadProperties(String fileName)
     {
         try{
             properties = new Properties();
             properties.load(new FileInputStream(fileName));
         }
         catch (Exception ex)
         {
             System.out.println("Exception in specified ini file " + fileName + ". Skipping file.");
             System.out.println(ex);
             ex.printStackTrace();
             System.exit(1);
         }
         return properties;
     }
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(String[] args)
{

    if ( System.getProperties().getProperty(PROPERTY_FILE) != null)
    {
        loadProperties(System.getProperties().getProperty(PROPERTY_FILE));
    }
    else
    {
        properties = System.getProperties();
    }

	try
	{

        initORBConnection(args);
        initPOA();
        logon();

        sessionName = properties.getProperty(SESSION_NAME).toString();
        classSymbol = properties.getProperty(CLASS_SYMBOL).toString();
        productType = Short.parseShort(properties.getProperty(PRODUCT_TYPE).toString());
        classKey = session.getProductQuery().getClassBySymbol(productType, classSymbol).classKey;
        productKey = session.getProductQuery().getProductsByClass(classKey)[0].productKeys.productKey;

        session.getTradingSession().getClassBySessionForKey(sessionName, classKey);
        session.getTradingSession().getClassBySessionForSymbol(sessionName, productType, classSymbol );
        session.getTradingSession().getProductBySessionForKey(sessionName, productKey);
        session.getTradingSession().getProductBySessionForName(sessionName, null);
        session.getTradingSession().getProductTypesForSession(sessionName);
        session.getTradingSession().getStrategiesByComponent(classKey, sessionName);
        session.getTradingSession().getStrategyBySessionForKey(sessionName, productKey);

    }
	catch (Exception e)
	{
		System.out.println("caught " + e);
		e.printStackTrace();
	}

	System.out.println("done");
	System.exit(1);
}



}
