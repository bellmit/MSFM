package com.cboe.application.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import com.cboe.application.shared.UnitTestHelper;
import com.cboe.application.tradingClassStatus.TradingClassStatusUtil;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.StructBuilder;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.idl.cmiV8.TradingClassStatusQuery;
import com.cboe.idl.cmiV8.UserAccessV8;
import com.cboe.idl.cmiV8.UserAccessV8Helper;
import com.cboe.idl.cmiV8.UserSessionManagerV8;

/**
 * Copyright 1999-2009 by the Chicago Board Options Exchange ("CBOE"), as an
 * unpublished work. The information contained in this software program
 * constitutes confidential and/or trade secret information belonging to CBOE.
 * <p/>
 * 
 * @author Arun Ramachandran
 */
public class TradingClassStatusQueryClient
{
    static ORB orb;
    static POA rootPOA;

    static final int EXIT_SUCCESS = 0;
    static final int EXIT_ERROR = 1;

    UserLogonStruct userLogonStruct;
    UserSessionManagerV8 userSessionManager;
   
    // Properties for tailoring CAS specification
    private static final String PROP_CAS_IP = "Test.CasIp";
    private static final String PROP_CAS_PORT = "Test.CasPort";
    private static final String PROP_LOGIN_MODE = "Test.LoginMode";
    private static final String PROP_USER_ID = "Test.UserId";
    private static final String PROP_USER_PASSWORD = "Test.UserPassword";
    //private static final String PROP_TRADING_SESSION = "Test.TradingSession";
    private static final String PROP_TRADING_SESSION = "W_MAIN";
    private static final String PROP_CLASS_NAME = "Test.ClassName";
    private static final String PROP_PRODUCT_TYPE = "Test.ProductType";
    private static final String PROP_PRODUCT_KEYS = "Test.ProductKey";
    private static final String PROP_MARKET_QUERY_TYPE = "Test.MarketQueryType";
    private static final String PROP_EXECUTION_DURATION = "Test.executionDuration";
    private static final String PROP_LOG_FILE_LOCATION = "Test.logFileLocation";
    private static final String PROP_FILE = "Test.properties";
    private static final String PROP_DEFAULT_RUN = "Test.DefaultRun";
    private static final String PROP_CREATE_PROP_FILE = "Test.CreatePropertyFile";
    private static final String PROP_LOGFILE_FORMAT = "Test.logFileFormat";
    private static final String PROP_SUBSCRIBE_RECAP = "Test.subscribeRecap";
    private static final String PROP_SUBSCRIBE_TICKER = "Test.subscribeTicker";
    private static final String DEFAULT_PROPERTY_FILE = "marketdatatest.properties";

    // For creating URL to get initial IOR from CAS
    
    //private static final String IP_LOCALHOST = "170.137.239.28";	//<--- atgcas1
    //private static final String IP_LOCALHOST = "170.137.225.168"; //<--- atgcas4
    //private static final String IP_LOCALHOST = "170.137.224.44" ;	//<--- dev3cas
    //private static final String IP_LOCALHOST = "170.137.225.22" ;	//<--- dev15cas
    //private static final String IP_LOCALHOST = "170.137.225.14" ;	//<--- dev15cas2
    //private static final String IP_LOCALHOST = "170.137.239.65" ; //<--- tstcas1
    private static final String IP_LOCALHOST = "170.137.224.36";
    private static final int CAS_LOCATOR_PORT = 8003;
    private static final String IOR_REFERENCENAME_V2 = "/UserAccessV8.ior";
    // Parameters for logon
    private static final short SESSION_TYPE = LoginSessionTypes.PRIMARY;
    private static final boolean GMD_MESSAGING = true;
    private static String CURRENT_DIR = "/./";
    private static String TEMP_DIR = "/tmp/";

    private static String hostIp;
    private static int locatorPort;
    private static char sessionMode;
    private static String sessionName;
    private static String classNames;
    private static short productType;
    private static String userId;
    private static String password;
    private static String productKeys;
    private static int marketQueryType;
    private static String logDir;
    private static String LOG="TradingClassStatus";
    private BufferedWriter logger = null;
	private int executionDuration;
    protected final static String EOL = System.getProperty("line.separator");
    protected static String DELIM = "\n";
    final static SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss:S yyyy/MM/dd");
    
    /**
     * UserSessionAdminCallBack
     */
    public class UserSessionAdminCallback extends com.cboe.idl.cmiCallback.CMIUserSessionAdminPOA
    {
        public HeartBeatStruct acceptHeartBeat(HeartBeatStruct heartbeat)
        {
            System.out.println("...Heartbeat...");
            return heartbeat;
        }

        public void acceptLogout(String reason)
        {
            fail(new RuntimeException("CAS called acceptLogout(\"" + reason + "\")"));
        }

        public void acceptTextMessage(MessageStruct message)
        {
            logBoth("CAS sent message. messageKey:" + message.messageKey + " timeStamp:" + message.timeStamp + " sender:" + message.sender + " replyRequested:" + message.replyRequested + " messageText:" + message.messageText,LOG);
            
        }

        public void acceptAuthenticationNotice()
        {
            try
            {
                userSessionManager.authenticate(userLogonStruct);
            }
            catch (Exception e)
            {
                fail(e);
            }
        }

        public void acceptCallbackRemoval(CallbackInformationStruct callbackInfo, String reason, int errorCode)
        {
            String msg = "CAS called acceptCallbackRemoval reason:" + reason + " errorCode:" + errorCode + " interface:" + callbackInfo.subscriptionInterface + " operation:" + callbackInfo.subscriptionOperation + " value:" + callbackInfo.subscriptionValue;
            fail(new RuntimeException(msg));
        }


    };

    /**
     * TradingSessionStatusConsumerCallback
     */
    public class TradingClassStatusQueryConsumerCallback extends com.cboe.idl.cmiCallbackV5.CMITradingClassStatusQueryConsumerPOA   {
     
		public void acceptTradingClassStatusUpdateforClasses(
                                int[] listOfClasses, short status) {
			for (int i = 0; i < listOfClasses.length; i++) {
				System.out.println("STATUS FOR CLASS :<"+listOfClasses[i]+"> IS :<"+status+">");
				log("STATUS FOR CLASS :<"+listOfClasses[i]+"> IS :<"+status+">");
			}
		}

		public void acceptTradingClassStatusUpdateforProductGroups(
                                String[] listOfProductGroups, short status) {
			for (int i = 0; i < listOfProductGroups.length; i++) {
				System.out.println("STATUS FOR GROUPS :<"+listOfProductGroups[i]+"> IS :<"+status+">");
                                log("STATUS FOR GROUPS :<"+listOfProductGroups[i]+"> IS :<"+status+">");
			}
		}

    };
    
    /**
     * Logs the output and exits with an error code.
     *
     * @param e
     */
    void fail(Exception e)
    {
        logBoth(e);
        System.exit(EXIT_ERROR);
    }

    /**
     * login to cas for user access v2
     */
    void login()
    {
        UserSessionAdminCallback userSessionAdminCallback = new UserSessionAdminCallback();
        try
        {
            rootPOA.activate_object(userSessionAdminCallback);
        }
        catch (Exception e)
        {
            fail(e);
        }
        //Activate Trading class status call back....
    	TradingClassStatusQueryConsumerCallback tcsqCallback = new TradingClassStatusQueryConsumerCallback();
    	try
        {
            rootPOA.activate_object(tcsqCallback);
        }
        catch (Exception e)
        {
            fail(e);
        }
        String userAccessIOR = null;
        try
        {
            URL url = new URL("http", hostIp, locatorPort, IOR_REFERENCENAME_V2);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader((InputStream) connection.getContent()));
            userAccessIOR = in.readLine();
        }
        catch (Exception e)
        {
            fail(e);
        }

        UserAccessV8 userAccessV8 = UserAccessV8Helper.narrow(orb.string_to_object(userAccessIOR));


        userLogonStruct = new UserLogonStruct();
        userLogonStruct.userId = userId;
        userLogonStruct.password = userId;
        userLogonStruct.version = Version.CMI_VERSION;
        userLogonStruct.loginMode = sessionMode;

        try
        {
            userSessionManager = userAccessV8.logon(userLogonStruct, SESSION_TYPE, userSessionAdminCallback._this(), GMD_MESSAGING);
        }
        catch (Exception e)
        {
            fail(e);
        }
        try{
        	TradingClassStatusQuery tcsq = userSessionManager.getTradingClassStatusQuery();
        	System.out.println("");
        	String[] productGroups = tcsq.getProductGroups();
        	StringBuilder sb = new StringBuilder();
        	ArrayList<int[]> subClasses = new ArrayList<int[]>();
        	for (int i = 0; i < productGroups.length; i++) {
				String stringGroups = productGroups[i];
				sb.append("Product_GROUP :<"+stringGroups+">\n");
				int[] classKeys = tcsq.getClassesForProductGroup(stringGroups);
				subClasses.add(classKeys);
				for (int j = 0; j < classKeys.length; j++) {
					int k = classKeys[j];
					sb.append(k).append("\n");
				}
				sb.append("\n"+"\n");
			}
        	for (Iterator iterator = subClasses.iterator(); iterator.hasNext();) {
				int[] is = (int[]) iterator.next();
				tcsq.subscribeTradingClassStatusForClasses(PROP_TRADING_SESSION, subClasses.get(0), tcsqCallback._this());
				System.out.println("Subscribed for following CLasses :<"+TradingClassStatusUtil.intArrayToString(is)+">");
				
			}
        	
        	log(sb.toString());
        	log("Calling subscribeTradingClassStatusForGroups");
        	//tcsq.subscribeTradingClassStatusForProductGroup(PROP_TRADING_SESSION, productGroups,  tcsqCallback._this());
        	sb.setLength(0);
        }catch(Throwable t){
        	t.printStackTrace();
        }
        
         
    }
    
    private static int getRandomOrderId()
    {
        Random r     = new Random();
        int    max   = 9999;
        int    min   = 1000;
        int    range = max - min + 1;
        int    orderId;

        orderId = r.nextInt(range) + min;
        return orderId;
    }
    
    private static OrderEntryStruct getBuyOrder()
    {
        ExchangeFirmStruct firm = StructBuilder.buildExchangeFirmStruct("CBOE", "671");
        OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",getRandomOrderId(),"FRM","20101219");
        OrderEntryStruct order = UnitTestHelper.createOrderEntryStruct(
            "ONE_MAIN",
            667202863,
            PROP_USER_ID,
            inputOrderId);        order.account = PROP_USER_ID;
        order.price = PriceFactory.create(2.0).toStruct();
        order.originalQuantity = 10;
        order.contingency.price = PriceFactory.create( 0 ).toStruct();
        order.branchSequenceNumber = 10;
	    order.side = Sides.BUY;
	    return order;
    }
    
    private  OrderIdStruct acceptOrder(OrderEntryStruct orderEntryStruct)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException,
			AlreadyExistsException {
		System.out.println("calling acceptOrder");
		try {

			return userSessionManager.getOrderEntry().acceptOrder(orderEntryStruct);
		} catch (DataValidationException de) {
			System.out.println(de.details);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("acceptOrder done");
		return new OrderIdStruct();

	}

    /**
     * logout from cas
     */
    void logout()
    {
        try
        {
            if (userSessionManager != null)
            {
                userSessionManager.logout();
            }
        }
        catch (Exception e)
        {
            fail(e);
        }
    }


    public void run() throws Exception
    {
       login();
       acceptOrder(getBuyOrder());
       sleep();
       logout();
       System.out.print("Logs are available at " + new File(LOG).getAbsoluteFile() +  EOL);
    }

    private void sleep() {
		try {
			Thread.sleep(3000000);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
     * Generates a defualt property file string for the application.
     *
     * @return
     */
    private static String getDefaultProperties()
    {

        return PROP_CAS_IP+ "=localhost"+EOL+
                PROP_CAS_PORT + "=8003"+EOL +
                comment(PROP_LOGIN_MODE) + "=>Production|StandAloneTest|NetworkTest"+EOL+
                PROP_LOGIN_MODE + "=NetworkTest"+EOL + EOL+
                PROP_USER_ID + "=X01"+EOL + EOL +
                comment(PROP_USER_PASSWORD) + "=> This is optional. Default password is same as user id."+EOL+
                PROP_USER_PASSWORD+ "=X01"+EOL + EOL+
                PROP_TRADING_SESSION + "=W_MAIN"+EOL + EOL +
                comment(PROP_PRODUCT_TYPE) + "=> Equity|Future|Option"+EOL +
                PROP_PRODUCT_TYPE +  "=OPTIONS"+EOL + EOL +
                comment(PROP_MARKET_QUERY_TYPE)+ "=> V4 => MarketQueryV4, V3=> MarketQueryV3,V2=> MarketQueryV2"+EOL +
                PROP_MARKET_QUERY_TYPE+ "=V4"+EOL + EOL +
                comment(PROP_EXECUTION_DURATION) + "=>execution time in seconds"+EOL+
                PROP_EXECUTION_DURATION + "=15"+EOL + EOL+
                comment(PROP_CLASS_NAME) + "=>Comma seperated class symbols"+EOL +
                PROP_CLASS_NAME + "=A,AAPL"+EOL +
                comment(PROP_PRODUCT_KEYS)+ "=>comma seperated product keys."+EOL+
                comment(PROP_PRODUCT_KEYS)+ "=525225315,525225316"+EOL + EOL +
                comment(PROP_LOG_FILE_LOCATION)+ "=>log File location. For current directory leave empty "+EOL +
                PROP_LOG_FILE_LOCATION + "=" + TEMP_DIR + EOL +
                comment(PROP_LOGFILE_FORMAT) + "=>singleline,multiline"+EOL+
                PROP_LOGFILE_FORMAT + "=singleline"+EOL +
                PROP_SUBSCRIBE_RECAP + "=N"+EOL+
                PROP_SUBSCRIBE_TICKER + "=N"+EOL
                ;
    }

    private static String comment(String str)
    {
        return "#"+ str;
    }

    /**
     * Generates default propery file for the application.
     *
     * @return
     */
    private static boolean createDefaultPropertyFile()
    {
        boolean result = true;
        BufferedWriter writer = null;
        String fileContent = getDefaultProperties();
        File f = new File(TEMP_DIR + DEFAULT_PROPERTY_FILE);
        boolean fileCreated = false;
        System.out.println("Creating the default property file.");
        try
        {
            if (f.exists() || f.createNewFile())
            {
                FileWriter fstream = new FileWriter(f);
                writer = new BufferedWriter(fstream);
                writer.write(fileContent);
                writer.flush();
                writer.close();
                fileCreated = true;
                System.setProperty(PROP_FILE,f.getAbsolutePath());
            }

        }
        catch (IOException e)
        {
        }
        if (!fileCreated)
        {
            f = new File(CURRENT_DIR + DEFAULT_PROPERTY_FILE);
            try
            {
                if (f.exists() || f.createNewFile())
                {
                    FileWriter fstream = new FileWriter(f);
                    writer = new BufferedWriter(fstream);
                    writer.write(fileContent);
                    writer.flush();
                }
            }
            catch (IOException e)
            {
                System.out.println("Can not create " + f.getAbsoluteFile() + ".\nPlease grant appopriate permissions to /tmp directory or current directory.");
                System.exit(EXIT_ERROR);
            }
        }


        System.out.println("Default propery file created at : " + f.getAbsoluteFile() + "");
        return result;
    }

    /**
     * Reads property file or default properties and creates application control variables.
     */
    void getArgs()
    {

        Properties props = new Properties();
        if (System.getProperty(PROP_DEFAULT_RUN) != null && System.getProperty(PROP_DEFAULT_RUN).equalsIgnoreCase("true"))
        {
            createDefaultPropertyFile();
        }
        else if (System.getProperty(PROP_CREATE_PROP_FILE) != null)
        {

            createDefaultPropertyFile();
            System.exit(EXIT_SUCCESS);
        }

        if (System.getProperty(PROP_FILE) != null)
        {
            String fileName = System.getProperty(PROP_FILE);

            try
            {
                if (fileName != null)
                {
                    File f = new File(fileName);
                    FileInputStream in = new FileInputStream(fileName);
                    props.load(in);
                }
                else
                {
                    System.out.println("Error:Execution propery file not specified. To create a sample property file execute MarketDataTest -sample ");
                    System.out.println("Usage:MarketDataTest /tmp/MarketDataTest.properties");
                    System.out.println("Usage:MarketDataTest -sample");
                }
            }
            catch (Exception e)
            {
                System.out.println("Property file " + fileName + " Not found");
                e.printStackTrace();
                System.exit(EXIT_ERROR);
            }


        }


        String s;
        s = props.getProperty(PROP_CAS_IP);
        hostIp = (s == null) ? IP_LOCALHOST : s;

        s = props.getProperty(PROP_CAS_PORT);
        locatorPort = (s == null) ? CAS_LOCATOR_PORT : Integer.parseInt(s);

        s = props.getProperty(PROP_LOGIN_MODE, "NETWORK_TEST");
        if (s == null || s.startsWith("N") || s.startsWith("n"))
        {
            sessionMode = LoginSessionModes.NETWORK_TEST;
        }
        else if (s.startsWith("p") || s.startsWith("P"))
        {
            sessionMode = LoginSessionModes.PRODUCTION;
        }
        else // only option left is standalone
        {
            sessionMode = LoginSessionModes.STAND_ALONE_TEST;
        }

        sessionName = props.getProperty(PROP_TRADING_SESSION, "W_MAIN");
        
        
        userId = props.getProperty(PROP_USER_ID);
        userId = userId == null ? "X01" : userId;
        password = props.getProperty(PROP_USER_PASSWORD);
        password = (password == null) ? userId : password;


        s = props.getProperty(PROP_EXECUTION_DURATION);
        executionDuration = (s == null) ? 15 : Integer.parseInt(s);
        s = props.getProperty(PROP_LOG_FILE_LOCATION);
        logDir = (s == null) ? TEMP_DIR : s;
        SimpleDateFormat f = new SimpleDateFormat("yyMMdd_HHmmss");
        String logExtenstion =  f.format(new Date());
        LOG = logDir + "/"+ LOG + logExtenstion + ".log";
        

        s = props.getProperty(PROP_LOGFILE_FORMAT);
        DELIM  = (s == null) ? "\n" : s.toUpperCase().startsWith("M")?"\n":",";

        
        StringBuffer msg = new StringBuffer();
        msg.append("User Id\t\t\t:" + userId + EOL);
        msg.append("Cas host\t\t:" + hostIp + EOL);
        msg.append("Cas port\t\t:" + locatorPort + EOL);
        msg.append("Session Mode\t\t:" + ((sessionMode == '2') ? "NETWORK TEST" : (sessionMode == '3') ? "PRODUCTION" : "STANDALONE TEST") + EOL);
        msg.append("Sesstion Name\t\t:" + sessionName + EOL);
        msg.append("MarketQuery Type\t:V" + marketQueryType + EOL);
        msg.append("Product Type\t\t:" + productType + EOL);

        if (productKeys == null && classNames == null)
        {
            msg.append("Subscribing classes\t:" + "ANY TWO RANDOM" + EOL);
        }
        if (classNames != null)
        {
            msg.append("Subscribing classes\t:" + classNames + EOL);
        }
        if (productKeys != null && marketQueryType != 4)
        {
            msg.append("Subscribing Products\t:" + productKeys + EOL);
        }
        msg.append("Log file location\t:" + new File(LOG).getAbsoluteFile() + EOL);
        msg.append("Log file delimiter\t:" + (DELIM.equals("\n")?"NEW LINE(\\n)": "COMMA(,)") + EOL);
        logBoth(msg.toString());

    }

    public synchronized void log(String s)
    {
        log(s,LOG);

    }

    /**
     * Logs the output to output file
     *
     * @param s String to write
     */
    public synchronized void log(String s, String logFile)
    {
        if(logger==null)
        {
            File mFile = new File(LOG);
            try
            {
                if (mFile.exists() || mFile.createNewFile())
                {
                    logger = new BufferedWriter(new FileWriter(mFile));
                    

                }
            }
            catch (IOException e)
            {
                System.out.println("Can not create " + mFile.getAbsoluteFile() + ". Please grant correct write permissions Or configure log file path using property -D" + PROP_LOG_FILE_LOCATION + "=<writable file directory>.");
                e.printStackTrace();
                System.exit(EXIT_ERROR);
            }

        }


        try
        {
            logger.write(s);
            logger.flush();
            
        }catch (IOException e){
            if (e.getMessage() != null && e.getMessage().contains("Stream closed"))  {
                System.out.println("WARNING: All log data has not written to the log file. Subscription time can be increased using -DTest.executionDuration=<time in ms> property." + EOL);
                System.exit(EXIT_SUCCESS);
            }
            else
            {
                fail(e);
            }

        }


    }

    /**
     * Logs the output to output file as well as on System.out.
     *
     * @param s
     */
    public  void logBoth(String s, String logFile)
    {
        System.out.print(s);
        log(s,logFile);
    }
    public  void logBoth(String s)
    {
        logBoth(s,LOG);
    }
    public  void logBoth(Exception e)
    {
        logBoth(e,LOG);
    }

    /**
     * Logs the exception stack trace to log file as well as System.out
     *
     * @param e
     */
    public  void logBoth(Exception e,String logFile)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        log(sw.toString(),logFile);
        e.printStackTrace();

    }


    /**
     * main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {

        TradingClassStatusQueryClient tester = new TradingClassStatusQueryClient();
        try
        {
            orb = ORB.init(args, null);
            System.out.println("ARGS:"+TradingClassStatusUtil.arrayToString(args));
            rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            tester.getArgs();

            tester.run();
            System.out.println("CORBA_NON_EXISTENT"+rootPOA._non_existent());
        }
        catch (Exception e)
        {
            tester.fail(e);
        }
        if (tester.logger != null)
        {
            tester.logger.flush();
            tester.logger.close();
        }
        System.exit(EXIT_SUCCESS);
    } // main


}
