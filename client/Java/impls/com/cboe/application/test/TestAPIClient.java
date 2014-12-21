package com.cboe.application.test;
import com.cboe.exceptions.*;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.domain.util.*;
import com.cboe.application.cas.TestUserAccessV2Factory;
import com.cboe.application.cas.TestCallback;
import com.cboe.application.cas.TestUserAccessV3Factory;
import com.cboe.application.shared.RemoteConnection;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.callback.UserSessionAdminConsumerDelegate;
import com.cboe.util.event.EventChannelAdapterFactory;
import java.util.Properties;
import java.io.FileInputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

/**
 * @author Vaziranc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestAPIClient {
    
    public static final String SYSTEM_SECTION = "System";
    public static final String SETUP_SETTINGS = "Setup";
    public static final String USER = "User";
    public static final String PASSWORD = "Password";
    public static final String LOGIN_MODE = "LoginOperationMode";
    public static final String LOGIN_SESSION = "SessionLoginType";
    public static final String SESSION_NAME = "SessionName";
    public static final String PRODUCT_TYPE = "ProductType";
    public static final String QUE_ACTION = "QueueAction";
    public static final String CLASS_SYMBOL = "ClassSymbol";
    public static final String CLASS_KEY = "ClassKey";
    public static final String PRODUCT_KEY = "ProductKey";
    
    public static final String GMD = "gmd";
    public static final String PROPERTY_FILE = "TestAPIClient.properties";

    public static RemoteConnection connection;
    public static SessionManagerStructV2 sessionManagerV2;
    public static SessionManagerStructV2 userSessionManager;

    public static UserSessionManager session;
    public static UserSessionManagerV2 sessionV2;
    public static UserSessionManagerV3 sessionV3;
    public static TestCallback callback;
    public static int[]  groups = {1};
    private static POA poaReference;
    private static UserAccessV2 userAccessV2;
    private static UserAccessV3 userAccessV3;
    private static Properties properties;

    private static boolean loggedIn;
    private static String classSymbol;
    private static String sessionName;
    private static int classKey;
    private static int productKey;
    private static short productType;

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
    protected static void initORBConnection(String[] args)
    {
        if ( connection == null )
        {
            connection = RemoteConnectionFactory.create(args);
            EventChannelAdapterFactory.find().setDynamicChannels(true);
        }
    }

    protected static UserAccessV2 getUserAccessV2()
    {
        if(userAccessV2 == null)
        {
            userAccessV2 = TestUserAccessV2Factory.find();
        }
        return userAccessV2;
    }

   protected static UserAccessV3 getUserAccessV3()
    {
        if(userAccessV3 == null)
        {
            userAccessV3 = TestUserAccessV3Factory.find();
        }
        return userAccessV3;
    }
    
    public static TestCallback getCallback() {
        if (callback == null) {
            callback = new TestCallback();
        }
        return callback;
    }

    protected static CMIUserSessionAdmin getUserSessionAdmin()
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

    protected static void logon() throws AuthenticationException, DataValidationException, SystemException, CommunicationException, AuthorizationException, NotFoundException
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
        sessionV3 = getUserAccessV3().logon(userLogonStruct, sessionType, getUserSessionAdmin(), gmd);
        //userSessionManager = getUserAccessV2().logon(userLogonStruct, sessionType, getUserSessionAdmin(), gmd);
        //sessionV2 = userSessionManager.sessionManagerV2;
        //session =  userSessionManager.sessionManager;
        sessionV2 = sessionV3;
        session =  sessionV3;
        loggedIn = true;
    }

    protected static Properties loadProperties(String fileName)
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
     * @return
     */
    public static Properties getProperties() {
        if (properties == null)
        {
            properties = loadProperties(PROPERTY_FILE);            
        }
        return properties;
    }

    /**
     * @param properties
     */
    public static void setProperties(Properties properties) {
        TestAPIClient.properties = properties;
    }

}
