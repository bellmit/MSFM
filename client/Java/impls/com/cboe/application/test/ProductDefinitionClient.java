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

public class ProductDefinitionClient
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

        session.getProductDefinition().acceptStrategy(sessionName, null);
        //session.getProductDefinition().buildStrategyRequestByName()
        //session.getProductDefinition().buildStrategyRequestByProductKey(productType, productKey, ) ;

    }
	catch (Exception e)
	{
		System.out.println("caught " + e);
		e.printStackTrace();
	}

	System.out.println("done");
	System.exit(1);
}



        private static QuoteEntryStruct makeAQuote()
        {
            currentPrice  = currentPrice + 0.5;
            QuoteEntryStruct newQuote = (QuoteEntryStruct) ReflectiveStructBuilder.newStruct(QuoteEntryStruct.class);
            newQuote.sessionName = sessionName;
            newQuote.productKey = productKey;
            newQuote.bidQuantity = 10;
            newQuote.bidPrice = PriceFactory.create(currentPrice).toStruct();
            newQuote.askQuantity = 10;
            newQuote.bidPrice = PriceFactory.create(currentPrice + 0.1).toStruct();
            return newQuote;

        }

        private static QuoteEntryStruct[] makeBlockQuotes()
        {
            QuoteEntryStruct newQuote = (QuoteEntryStruct) ReflectiveStructBuilder.newStruct(QuoteEntryStruct.class);
            newQuote.sessionName = sessionName;
            newQuote.productKey = 0;
            newQuote.bidQuantity = 10;
            newQuote.bidPrice = PriceFactory.create(currentPrice).toStruct();
            newQuote.askQuantity = 10;
            newQuote.askPrice = PriceFactory.create(currentPrice + 0.1 ).toStruct();
            QuoteEntryStruct[] blockQuotes = new QuoteEntryStruct[1];
            blockQuotes[0] = newQuote;
            return blockQuotes;

    }

    private static void acceptQuote(QuoteEntryStruct quote)
    {
        System.out.println("calling AcceptQuote");
        try {
            sessionV2.getQuoteV2().acceptQuote(quote);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptQuote done");
    }

    private static void acceptQuotesForClass(int classKey, QuoteEntryStruct[] quotes)
    {
        System.out.println("calling acceptQuotesForClass");
        try {
            sessionV2.getQuoteV2().acceptQuotesForClass(classKey, quotes);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptQuotesForClass done");
    }
    private static void acceptQuotesForClassV2(int classKey, QuoteEntryStruct[] quotes)
    {
        System.out.println("calling acceptQuotesForClass");
        try {
        sessionV2.getQuoteV2().acceptQuotesForClassV2(classKey, quotes);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("acceptQuotesForClass done");
    }

    private static void cancelAllQuotes(String sessionName)
    {
        System.out.println("calling cancelAllQuotes");
        try {
        sessionV2.getQuoteV2().cancelAllQuotes(sessionName);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("cancelAllQuotes done");
    }

    private static void cancelQuote(String sessionName, int productKey)
    {
        System.out.println("calling cancelQuote");
        try {
        sessionV2.getQuoteV2().cancelQuote(sessionName, productKey);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("cancelQuote done");
    }

    private static void cancelQuotesByClass(String sessionName, int classKey)
    {
        System.out.println("calling cancelQuotesByClass");
        try {
        sessionV2.getQuoteV2().cancelQuotesByClass(sessionName, classKey);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("cancelQuotesByClass done");
    }

    private static void getQuote(String sessionName, int productKey)
    {
        System.out.println("calling getQuote");
        try {
        sessionV2.getQuoteV2().getQuote(sessionName, productKey);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getQuote done");
    }

    private static void subscribeQuoteLockedNotification(boolean publishOnSubscribe, CMILockedQuoteStatusConsumer clientListener, boolean gmdCallback)
    {
        System.out.println("calling subscribeQuoteLockedNotification");
        try {
            sessionV2.getQuoteV2().subscribeQuoteLockedNotification(publishOnSubscribe, clientListener, true);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteLockedNotification done");
    }
    private static void subscribeQuoteLockedNotificationForClass(int classKey, boolean publishOnSubscirbe, CMILockedQuoteStatusConsumer clientListener, boolean gmdCallback)

    {
        System.out.println("calling subscribeQuoteLockedNotificationForClass");
        try {
            sessionV2.getQuoteV2().subscribeQuoteLockedNotificationForClass(classKey, publishOnSubscirbe, clientListener, gmdCallback );
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteLockedNotificationForClass done");
    }

    private static void subscribeQuoteStatus(CMIQuoteStatusConsumer clientListener, boolean gmdCallback)

    {
        System.out.println("calling subscribeQuoteStatus");
        try {
            sessionV2.getQuoteV2().subscribeQuoteStatus(clientListener, gmdCallback);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteStatus done");
    }
    private static void subscribeQuoteStatusForClassV2(int classKey,
                                             boolean publishOnSubscribe,
                                             boolean includeBookedStatus,
                                             com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener,
                                             boolean gmdCallback)

    {
        System.out.println("calling subscribeQuoteStatusForClassV2");
        try {
            sessionV2.getQuoteV2().subscribeQuoteStatusForClassV2(classKey, publishOnSubscribe, includeBookedStatus, clientListener, gmdCallback);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteStatusForClassV2 done");
    }

    private static void subscribeQuoteStatusForFirm(CMIQuoteStatusConsumer clientListener, boolean gmdCallback)

    {
        System.out.println("calling subscribeQuoteStatusForFirm");
        try {
            sessionV2.getQuoteV2().subscribeQuoteStatusForFirm(clientListener, gmdCallback);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteStatusForFirm done");
    }

    private static void subscribeQuoteStatusForFirmForClassV2(int classKey,
                                                    com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener,
                                                    boolean gmdCallback)

    {
        System.out.println("calling subscribeQuoteStatusForFirmForClassV2");
        try {
            sessionV2.getQuoteV2().subscribeQuoteStatusForFirmForClassV2(classKey, clientListener, gmdCallback);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteStatusForFirmForClassV2 done");
    }

    private static void subscribeQuoteStatusForFirmV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)

    {
        System.out.println("calling subscribeQuoteStatusForFirmV2");
        try {
            sessionV2.getQuoteV2().subscribeQuoteStatusForFirmV2(clientListener, gmdCallback);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteStatusForFirmV2 done");
    }

    private static void subscribeQuoteStatusForFirmWithoutPublish(
                                                    CMIQuoteStatusConsumer clientListener,
                                                    boolean gmdCallback)

    {
        System.out.println("calling subscribeQuoteStatusForFirmWithoutPublish");
        try {
            sessionV2.getQuoteV2().subscribeQuoteStatusForFirmWithoutPublish( clientListener, gmdCallback);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteStatusForFirmWithoutPublish done");
    }

    private static void subscribeQuoteStatusV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener,
                                       boolean publishOnSubscribe,
                                       boolean includeBookedStatus,
                                       boolean gmdCallback)
    {
        System.out.println("calling subscribeQuoteStatusV2");
        try {
            sessionV2.getQuoteV2().subscribeQuoteStatusV2(clientListener, publishOnSubscribe, includeBookedStatus, gmdCallback);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeQuoteStatusV2 done");
    }


     private static void subscribeQuoteStatusWithoutPublish(CMIQuoteStatusConsumer clientListener,
                                       boolean gmdCallback)
    {
            try {
            sessionV2.getQuoteV2().subscribeQuoteStatusWithoutPublish(clientListener, gmdCallback);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void subscribeRFQ(String sessionName, int classKey, CMIRFQConsumer clientListener)
    {
        try {
            sessionV2.getQuoteV2().subscribeRFQ(sessionName, classKey, clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void subscribeRFQV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRFQConsumer clientListener)
    {
        try {
            sessionV2.getQuoteV2().subscribeRFQV2(sessionName, classKey, clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void unSubscribeQuoteStatus(CMIQuoteStatusConsumer clientListener)

    {
        try {
            sessionV2.getQuoteV2().unsubscribeQuoteStatus(clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private static void unsubscribeQuoteStatusForClassV2(int classKey,
                                             com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener)
    {
        try {
            sessionV2.getQuoteV2().unsubscribeQuoteStatusForClassV2(classKey,clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void unsubscribeQuoteStatusForFirm(CMIQuoteStatusConsumer clientListener)
    {
        try {
            sessionV2.getQuoteV2().unsubscribeQuoteStatusForFirm(clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void unsubscribeQuoteStatusForFirmForClassV2(int classKey,
                                                    com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener)

    {
        try {
            sessionV2.getQuoteV2().unsubscribeQuoteStatusForFirmForClassV2(classKey, clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void unsubscribeQuoteStatusForFirmV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener)

    {
        try {
            sessionV2.getQuoteV2().unsubscribeQuoteStatusForFirmV2(clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void unsubscribeQuoteStatusV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener )
    {
        try {
            sessionV2.getQuoteV2().unsubscribeQuoteStatusV2(clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private static void unsubscribeRFQ(String sessionName, int classKey, CMIRFQConsumer clientListener)
    {
        try {
            sessionV2.getQuoteV2().unsubscribeRFQ(sessionName, classKey, clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void unsubscribeRFQV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRFQConsumer clientListener)
    {
        try {
            sessionV2.getQuoteV2().unsubscribeRFQV2(sessionName, classKey, clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static RFQStruct makeRFQ()
    {
        RFQStruct newRFQ = (RFQStruct) ReflectiveStructBuilder.newStruct(RFQStruct.class);
        newRFQ.sessionName = sessionName;
		newRFQ.productKeys.productKey = productKey;
		newRFQ.productKeys.classKey = classKey;
		newRFQ.productKeys.productType = productType;
		newRFQ.quantity = 10;
		newRFQ.timeToLive = 30;
        return newRFQ;
    }


}
