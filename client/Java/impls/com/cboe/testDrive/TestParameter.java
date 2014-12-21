package com.cboe.testDrive;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.callback.ClassStatusConsumerDelegate;
import com.cboe.delegates.callback.ProductStatusConsumerDelegate;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import sun.misc.Signal;
import sun.misc.SignalHandler;
import com.cboe.idl.cmiCallback.*;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiV2.UserAccessV2Helper;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV3.*;

import java.io.*;
import java.util.*;

/**
 * 07/11/07
 * Add new test tool for block quote cancel. 
 * @author krueyay
 *
 */
public class TestParameter  {
    public int numOfTests;
    public ArrayList productKeys;
    public ArrayList separateProductKeys;
    public Hashtable prodKeysByClass;
    public String memberKey;
    public int msgRate;
    public int msgInterval;
    public int stepUp;
    public long stepUpInterval;
    public long cancelInterval;
    public int reportEvery;
    public boolean randomKeys;
    public boolean randomBlockKeys;
    public boolean incQty;
    public String account = "";
    public String clearingFirmKey = "";
    public ArrayList userNames ;
    public String host;
    public int port;
    public CASMeter casMeter;
    public UserSessionManager userSessionManager;
    public String branch;
    public int loginsPerUser;
    public boolean includeUnderlying;
    public static boolean fileWatcherOption = false;
    public String productKeyFile;
    public boolean includeUserInitiatedStatus;
    public boolean threadPerClass;

    public double minQuoteBidPrice;
    public double maxQuoteBidPrice;
    public double quoteWidth;
    public double minOrderBidPrice;
    public double maxOrderBidPrice;
    public double minOrderAskPrice;
    public double maxOrderAskPrice;
    public double priceIncrement;
    public int orderQuantity;
    public int quoteQuantity;
    public boolean threadDone;
    public char mode;
    public boolean gmdText;
    public boolean gmdOrder;
    public boolean gmdQuote;
    public boolean strategyProduct;
    public boolean specifyOrderOriginType;
    public char orderOriginType;
    public short sessionMode;
    public short productType;
    public short queueAction;
    public short orderContingencyType;
    public String optionalData;
    public boolean underlyingOnly;
    // for DN testing
    public int timePeriod;
    public ArrayList classKeys;
    public String sessionName;
    public String quoteCancelSessionName;

    //orders
    public int sequenceSeed;
    public boolean limitProdRange;
    //public boolean isProductFile =false;
    public String testType = "Q";
    //block quotes
    public int numPerSeq;
    public boolean subscribeWithPublish;
    public String callbackVersion;
    public ArrayList productKeyFiles;
    public boolean UseSeperateProdKeys;
    public boolean cancelByClass;
    public String TSBreakdownFile;
    public int prodsPerHybridClass;
    public int hybridClassPerUser;
    public char orderTimeInForce;
    public int ctrlcCount = 0;
    public int numOfSplits = 0;
    public boolean fixQuantity = false;
    public int askQuantity = 10;
    public int bidQuantity = 10;
    public boolean waitOnGap = false;
    public char side = 'R';
    public int fixPriceSide;
    public int blockQuoteCancelQuantity = 0;
    public int quoteToQuoteCancelRatio = 0;
    public int quoteCancelType = 0;
    public int numOfQuoteCancelEntries = 0;
    

    // list of all the V4 MarketData logins
    private static CASTestMDV4Login[] v4TestLogins;

    //For Hybrid TS Class breakdown

    public static TestParameter instance;

    //int quoteKey = 1;
    //int quoteAdjustment = 5;
    //int quoteQuantity = 11;
    //int productOffset = 0;
    public TestParameter(){}

    public static synchronized TestParameter getInstance()
    {
        if (instance == null)
        {
            instance = new TestParameter();
        }
        return instance;
    }

    public ArrayList getClassAndProductKeys(int classPosition)
    {
        Object classkeys[] = prodKeysByClass.keySet().toArray();
        Object classKey = new Integer(0);
        if (classkeys != null)
        {
             classKey = classkeys[classPosition];
        }
        System.out.println("ClassKey => " + classKey);
        return (ArrayList)prodKeysByClass.get(classKey);
    }

    public ArrayList getClassAndProductKeys(String prodKeyFile)
    {
        ArrayList prodKeys = new ArrayList();

        FileReader aFR;
        int tmpClassKey;
        int tmpProductKey;
        String tmpSessionName;

        prodKeysByClass = new Hashtable();
        ArrayList tmpProductKeys;

        try {
            aFR = new FileReader(prodKeyFile);
            BufferedReader br = new BufferedReader(aFR);
            String s;
            while (( s = br.readLine()) != null)
            {
                tmpClassKey = 0;
                tmpProductKey = 0;
                tmpSessionName = "";
                StringTokenizer parser = new StringTokenizer(s, ",");
                Integer tmp = new Integer(0);
                Integer tmpKey = new Integer(0);

                if (parser.hasMoreTokens())
                {
                    tmp = new Integer(parser.nextToken());
                    tmpClassKey = tmp.intValue();
                }
                if (parser.hasMoreTokens())
                {
                    tmp = new Integer(parser.nextToken());
                    tmpProductKey = tmp.intValue();
                }
                if (parser.hasMoreTokens())
                {
                    tmpSessionName = parser.nextToken();
                }
                if (tmpProductKey != 0 && tmpClassKey != 0 && !tmpSessionName.equals(""))
                {
                    ProdClass tmpProdClass = new ProdClass(tmpClassKey, tmpProductKey, tmpSessionName);
                    prodKeys.add(tmpProdClass);
                    tmpKey = new Integer(tmpClassKey);

                    if ( ! prodKeysByClass.containsKey(tmpKey))
                    {
                        prodKeysByClass.put(tmpKey, new ArrayList());
                    }
                    ((ArrayList)(prodKeysByClass.get(tmpKey))).add(tmpProdClass);
                }
                else
                {
                    System.out.println("Bad product/class read from file...");
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error obtaining product keys. " + e);
            return null;
        }
        System.out.println("file : " + prodKeyFile + "Number of Product Keys (" + prodKeys.size() + ")");
        return prodKeys;
    }

    private void getProductKeyFiles(Properties props)
    {
        String files = props.getProperty("ProductKeyFiles", productKeyFile);
        ArrayList prodKeyFiles = new ArrayList();

        StringTokenizer parser = new StringTokenizer( files, "," );
        while ( parser.hasMoreTokens() )
        {
            prodKeyFiles.add( parser.nextToken() );
        }
        productKeyFiles = prodKeyFiles;
    }

    private void separateProdsByUser(int numberOfUsers)
    {
        separateProductKeys = new ArrayList();
        ArrayList prodKeysByUser[] = new ArrayList[userNames.size()];
        for (int y = 0; y < userNames.size(); y ++)
        {
            prodKeysByUser[y] = new ArrayList();
        }

        Enumeration en = prodKeysByClass.keys();

        int count = 0;
        Integer classKey = new Integer("0");

        while (en.hasMoreElements())
        {
            classKey = (Integer)(en.nextElement());
            prodKeysByUser[count].addAll((ArrayList)prodKeysByClass.get(classKey));
            count++;

            if (count == userNames.size())
            {
                count = 0;
            }
        }

        for (int i=0; i<prodKeysByUser.length; i++)
        {
            separateProductKeys.add(prodKeysByUser[i]);
        }

        PrintWriter writer = null;

        try {
             writer =  new PrintWriter(new FileWriter("SeparateClassKeys.out"));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Separation ............. " + separateProductKeys.size());
    }

    private void separateProdsForUser(int numOfClasses)
    {
        separateProductKeys = new ArrayList();
        ArrayList tmpProdKeys = new ArrayList();

        Enumeration en = prodKeysByClass.keys();
        int count = 0;
        Integer classKey = new Integer("0");

        while (en.hasMoreElements())
        {
            classKey = (Integer)(en.nextElement());
            tmpProdKeys.addAll((ArrayList)prodKeysByClass.get(classKey));
            count++;

            if (count%numOfClasses == 0)
            {
                System.out.println("# of classes " + numOfClasses);
                separateProductKeys.add(tmpProdKeys);
                tmpProdKeys = new ArrayList();
            }
        }
        System.out.println("Separation ............. " + separateProductKeys.size());
    }

    private void populateProdKeysFromFiles(Properties props)
    {
        getProductKeyFiles(props);

        separateProductKeys = new ArrayList();
        ArrayList tmpProdKeys = new ArrayList();
        String currentFile = "";

        for (int i=0; i < productKeyFiles.size(); i++)
        {
            currentFile = (String)productKeyFiles.get(i);
            tmpProdKeys = getClassAndProductKeys(currentFile);
            separateProductKeys.add(tmpProdKeys);
            System.out.println("Populated productKeys from product key file: " + currentFile + ".");
        }
    }

    public ArrayList getProductKeysByUserThread(int tid)
    {
        if (tid >= separateProductKeys.size())
        {
            tid = 0;
        }
        System.out.println("Getting products for thread: " + tid);
        return (ArrayList) separateProductKeys.get(tid);
    }

    public ArrayList getUserNames(Properties props)
    {
        String names = props.getProperty("UserNames", "CCC");
        ArrayList users = new ArrayList();

        StringTokenizer parser = new StringTokenizer( names, "," );
        while ( parser.hasMoreTokens() )
        {
            users.add( parser.nextToken() );
        }
        return users;
    }

    public ArrayList getClassKeys(Properties props)
    {
        String keys = props.getProperty("ClassKeys", "0");
        ArrayList classKeys = new ArrayList();
        StringTokenizer parser = new StringTokenizer( keys, "," );
        while ( parser.hasMoreTokens() )
        {
            Integer temp = new Integer(parser.nextToken());
            System.out.println("classKey:"+temp.intValue());
            classKeys.add(temp);
        }
        return classKeys;
    }

    public void getParameters(Properties props)
    {
        //Properties props = initializeProperties(FileName);
        //common parameters
        try {
            numOfTests = Integer.parseInt( props.getProperty( "NumberToSend", "1000" ) );
            System.out.println("Start of " + numOfTests  +  " performance test sequence... ");
            loginsPerUser = Integer.parseInt(props.getProperty("LoginsPerUser", "1"));
            if(numOfTests <= 0)
            {
                System.out.println("\n***************************************************************************************************************************");
                System.out.println(" Warning: NumberToSend is set to "+numOfTests+" -- Using an unlimited NumberToSend is currently only supported by tests '-B' and '-QM'");
                System.out.println("***************************************************************************************************************************\n");
            }

            memberKey = props.getProperty("memberKey", "AIW");
            msgRate = Integer.parseInt( props.getProperty( "MSGRATE", "0" ) );
            msgInterval = Integer.parseInt( props.getProperty( "MSGINT", "0" ) );
            stepUp = Integer.parseInt( props.getProperty( "STEPUP", "0" ) );
            stepUpInterval = Long.parseLong( props.getProperty( "STEUPINT", "120000" ) );
            cancelInterval = Long.parseLong(props.getProperty( "CANCELINT", "1000"));
            reportEvery = Integer.parseInt( props.getProperty( "ReportEvery", "0" ) );

            randomKeys = (Boolean.valueOf( props.getProperty( "RandomProductKeys", "false"))).booleanValue();
            randomBlockKeys = (Boolean.valueOf( props.getProperty( "RandomBlockProductKeys", "false"))).booleanValue();
            incQty = (Boolean.valueOf( props.getProperty( "ModifyQuantity", "false"))).booleanValue();
            System.out.println( "MessageRate(" + msgRate + ") per (" + msgInterval + ") seconds = " + ((float)msgRate/(float)msgInterval) + " mps" );
            host =  props.getProperty( "Host", "0"); //args[0];
            port =  Integer.parseInt(props.getProperty( "Port", "0" )); //args[1];
            branch = props.getProperty("Branch", "AAA");
            casMeter = CASMeter.create(props.getProperty("OutputFile", "CASPerfData.txt"), numOfTests);
            //per user parameters
            //UserName = props.getProperty("UserName", "CCC");
            productKeyFile = props.getProperty("ProductKeyFile", "classAndProductKeys.txt");
            productKeys = getClassAndProductKeys( productKeyFile );

            minQuoteBidPrice = Double.parseDouble(props.getProperty("MinQuoteBidPrice", "0"));
            maxQuoteBidPrice = Double.parseDouble(props.getProperty("MaxQuoteBidPrice", "" + minQuoteBidPrice));
            quoteWidth = Double.parseDouble(props.getProperty("QuoteWidth", "0"));
            minOrderBidPrice = Double.parseDouble(props.getProperty("MinOrderBidPrice", "0"));
            maxOrderBidPrice = Double.parseDouble(props.getProperty("MaxOrderBidPrice", "" + minOrderBidPrice));
            minOrderAskPrice = Double.parseDouble(props.getProperty("MinOrderAskPrice", "" + (minOrderBidPrice + quoteWidth)));
            maxOrderAskPrice = Double.parseDouble(props.getProperty("MaxOrderAskPrice", "" + minOrderAskPrice));
            priceIncrement = Double.parseDouble(props.getProperty("PriceIncrement", "0"));
            orderQuantity = Integer.parseInt(props.getProperty("OrderQuantity", "10"));
            quoteQuantity = Integer.parseInt(props.getProperty("QuoteQuantity", "10"));

            mode = props.getProperty("Mode", "1").charAt(0);
            gmdText = (Boolean.valueOf( props.getProperty( "GMDTextMessaging", "true"))).booleanValue();
            gmdOrder = (Boolean.valueOf( props.getProperty( "GMDOrderStatus", "true"))).booleanValue();
            gmdQuote = (Boolean.valueOf( props.getProperty( "GMDQuoteStatus", "true"))).booleanValue();
            strategyProduct = (Boolean.valueOf( props.getProperty( "StrategyProduct", "false"))).booleanValue();
            sessionMode = Short.parseShort(props.getProperty("LoginMode", "1"));
            productType = Short.parseShort(props.getProperty("ProductType", "7"));
            optionalData = props.getProperty("OptionalData", "");
            specifyOrderOriginType = (Boolean.valueOf(props.getProperty("SpecifyOrderOriginType", "false"))).booleanValue();
            orderOriginType = props.getProperty("OrderOriginType", "C").charAt(0);
            orderTimeInForce = props.getProperty("TimeInForce", "D").charAt(0);
            side = props.getProperty("Side", "R").charAt(0);
            quoteCancelSessionName = props.getProperty("QCancelSessionName", "ONE_MAIN");
            subscribeWithPublish = (Boolean.valueOf( props.getProperty( "SubWithPub", "false"))).booleanValue();
            queueAction = Short.parseShort(props.getProperty("QueueAction", "2"));
            orderContingencyType = Short.parseShort(props.getProperty("OrderContingencyType", "1"));
            underlyingOnly = (Boolean.valueOf( props.getProperty( "UnderlyingOnly", "false"))).booleanValue();
            threadPerClass = (Boolean.valueOf( props.getProperty("ThreadPerClass", "false"))).booleanValue();
            callbackVersion = props.getProperty("callbackVersion", "V2");
            includeUserInitiatedStatus = (Boolean.valueOf( props.getProperty( "includeUserInitiatedStatus", "false"))).booleanValue();
            UseSeperateProdKeys = (Boolean.valueOf( props.getProperty( "SeperateProdKeys", "false"))).booleanValue();
            cancelByClass = (Boolean.valueOf( props.getProperty( "CancelByClass", "true"))).booleanValue();
            TSBreakdownFile = props.getProperty("TSBreakdownFile", "TSBreakdown.txt");
            prodsPerHybridClass = Integer.parseInt(props.getProperty("ProdsPerHybridClass", "10"));
            hybridClassPerUser = Integer.parseInt(props.getProperty("HybridClassPerUser", "0"));
            numOfSplits = Integer.parseInt(props.getProperty("NumOfSplits", "0"));
            fixQuantity = (Boolean.valueOf( props.getProperty( "FixQuantity", "false"))).booleanValue();
            askQuantity = Integer.parseInt(props.getProperty("AskQuantity", "10"));
            bidQuantity = Integer.parseInt(props.getProperty("BidQuantity", "10"));
            waitOnGap = (Boolean.valueOf( props.getProperty( "WaitOnGap", "false"))).booleanValue();
            fixPriceSide = Integer.parseInt(props.getProperty("FixPriceSide", "0"));
            //add new property for number of cancel quote per block
            blockQuoteCancelQuantity = Integer.parseInt(props.getProperty("BlockQuoteCancelQuantity", "0"));
            quoteToQuoteCancelRatio = Integer.parseInt(props.getProperty("QuoteToQuoteCancelRatio", "0"));
            quoteCancelType = Integer.parseInt(props.getProperty("QuoteCancelType", "1"));
            numOfQuoteCancelEntries = Integer.parseInt(props.getProperty("QuoteCancelType", "0"));
            
            userNames = getUserNames(props);

            System.out.println("UseSeperateProdKeys >>>>>>>>> " + UseSeperateProdKeys);

/*            if (UseSeperateProdKeys)
            {
                populateProdKeysFromFiles(props);
            }    */

            if (UseSeperateProdKeys)
            {
                if (hybridClassPerUser != 0)
                {
                    separateProdsForUser(hybridClassPerUser);
                }
                else
                {
                    separateProdsByUser(userNames.size());
                }
            }

            if ( reportEvery == 0 )
            {
                reportEvery = productKeys.size();
            }

            // DN testing
            classKeys = getClassKeys(props);
            timePeriod = Integer.parseInt( props.getProperty( "TimePeriod", "0") );
            sessionName=props.getProperty("SessionName","W_AM1");
            //orders
            sequenceSeed = Integer.parseInt(props.getProperty("SeqSeed", "0"));
            limitProdRange = (Boolean.valueOf(props.getProperty("LimitProdRange", "false"))).booleanValue();
            //block orders
            numPerSeq = Integer.parseInt( props.getProperty( "NumberPerSequence", "50"));
            includeUnderlying = (Boolean.valueOf( props.getProperty( "IncludeUnderlying", "false"))).booleanValue();
        }
        catch (java.io.IOException  t)
        {
            System.out.println("error getting parameters" );
        }
    }


    protected static InputStream getTestSpecification(String fileName)
    {
        InputStream stream = null;
        File f = new File(fileName);
        if(f.exists())
        {
            try
            {
                stream = new FileInputStream(f);
                //File absPath = new File(f.getAbsolutePath());
                //File newFile = new File(absPath.getParent(), absPath.getName());
                //getControlCenter().getPropertiesFile().addElement(newFile);
            } catch (Throwable t) {}
        }
        return stream;
    }

    public static Properties initializeProperties(String fileName)
    {
        Properties properties = new Properties();
        InputStream stream = null;
        stream = getTestSpecification(fileName);
        if(stream == null)
        {
            System.out.println("Specified ini file " + fileName + " not found. Skipping file.");
        }
        else
        {
            try
            {
                properties.load(stream);
                // tc.addTest(properties);
            }
            catch (Exception ex)
            {
                System.out.println("Exception in specified ini file " + fileName + ". Skipping file.");
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        return properties;
    }

    public static void testQuotes(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "Q";
            testParm.getParameters(props);
//            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2[] sessionManagerStructs = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                sessionManagerStructs[t] = sessionManagerStructV2;
//                userSessionManagers[t] = sessionManagerStructV2.sessionManager;
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestQuote[] testQuotes = new CASTestQuote[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testQuotes[t] = new CASTestQuote(testParm, sessionManagerStructs[t], CASMeter.create("quotetest" + t + ".out", testParm.numOfTests));
            }

            executionMode();
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                 testQuotes[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    private static void executionMode() {
        if(fileWatcherOption)
        {
            String commandFile = "Startup";
            System.out.println("Waiting for command file " + commandFile + " to be created before proceed");
            while (waitToProceed(commandFile)){
            }
            System.out.println("continuing " + commandFile + " file exists");
        }
    else
    {
        try {
                System.out.print("Press <enter> to start");
                System.in.read();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean waitToProceed(String command) {
        File cmdFile = new File (command);
        try {
            FileReader exist = new FileReader (cmdFile);
        }
        catch (FileNotFoundException e)
        {
//            System.out.println ("Command file <"+ command + "> not found.");
            return true;
        }
        return false;
    }

    public static void seedQuotes(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "Q";
            testParm.getParameters(props);

            SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(0).toString(), (String)testParm.userNames.get(0),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
            //testParm.casMeter
            System.out.println("user name = " +  (String)testParm.userNames.get(0));

            CASTestQuoteSeed testQuote = new CASTestQuoteSeed(testParm, sessionManagerStructV2, CASMeter.create("quotetest.out", testParm.numOfTests));

            System.out.print("Press <enter> to start");
            System.in.read();

            testQuote.start();
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    public static void testDN(String iniFile)
    {
        CASMeter underlyingRecapMeter = new CASMeter();
        CASMeter underlyingTickerMeter = new CASMeter();
        CASMeter currentMarketMeter = new CASMeter();
        CASMeter recapMeter = new CASMeter();
        Properties props = TestParameter.initializeProperties(iniFile);
        TestParameter testParm = TestParameter.getInstance();
        testParm.getParameters(props);
        ArrayList classKeys = testParm.classKeys;
        UserSessionManagerV2 userSessionManagerV2;
        UserSessionManager userSessionManager;
        SessionManagerStructV2 sessionManagerStructV2;

        try
        {
            sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(0).toString(), (String)testParm.userNames.get(0), testParm.host, testParm.port
                    , testParm.mode, testParm.gmdText, testParm.sessionMode);
            userSessionManagerV2  = sessionManagerStructV2.sessionManagerV2;
            userSessionManager = sessionManagerStructV2.sessionManager;
            System.out.println("user name = " +  (String)testParm.userNames.get(0));
        }
        catch (Exception e)
        {
            e.printStackTrace() ;
            return ;
        }
        while(true)
        {
            try
            {
            System.out.println("starting subscribing for Current Market, Recap, Ticker");
            System.out.println("classKeys size:"+classKeys.size());
            recapMeter = CASMeter.create("Recap.out", classKeys.size());
            currentMarketMeter = CASMeter.create("CurrentMarket.out", classKeys.size());

            underlyingRecapMeter = CASMeter.create("UnderlyingRecap.out", classKeys.size());
            underlyingTickerMeter = CASMeter.create("UnderlyingTicker.out", classKeys.size());
            for (int t =0 ; t<classKeys.size(); t++)
            {
                Integer key = (Integer)(classKeys.get(t));
                int classKey = key.intValue();

                com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketCallback =
                        TestCallbackFactory.getV2CurrentMarketConsumer(new CurrentMarketCallbackDV2(currentMarketMeter,t));

                com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapCallback =
                        TestCallbackFactory.getV2RecapConsumer(new RecapCallbackDV2(recapMeter,t));

                userSessionManagerV2.getMarketQueryV2().subscribeCurrentMarketForClassV2(testParm.sessionName,classKey,currentMarketCallback,testParm.queueAction);
                System.out.println("Subscribed current market for " + classKey);
                userSessionManagerV2.getMarketQueryV2().subscribeRecapForClassV2(testParm.sessionName,classKey,recapCallback, testParm.queueAction);
                System.out.println("Subscribed recap for " + classKey);

                com.cboe.idl.cmiCallbackV2.CMIRecapConsumer underlyingRecapCallback =
                        TestCallbackFactory.getV2RecapConsumer(new RecapCallbackDV2(underlyingRecapMeter,t));

                com.cboe.idl.cmiCallbackV2.CMITickerConsumer underlyingTickerCallback =
                        TestCallbackFactory.getV2TickerConsumer(new TickerCallbackDV2(underlyingTickerMeter,t));

                SessionClassStruct underlyingClass;
                underlyingClass = userSessionManager.getTradingSession().getClassBySessionForKey(testParm.sessionName,
                        classKey);

                userSessionManagerV2.getMarketQueryV2().subscribeRecapForProductV2(underlyingClass.underlyingSessionName,
                        underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingRecapCallback,testParm.queueAction);
                System.out.println("Subscribed underlying recap for " + underlyingClass.underlyingSessionName + ":"
                    + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);

                userSessionManagerV2.getMarketQueryV2().subscribeTickerForClassV2(underlyingClass.underlyingSessionName,
                        underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingTickerCallback, testParm.queueAction);
                System.out.println("Subscribed underlying ticker for " + underlyingClass.underlyingSessionName + ":"
                    + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);
            }

            int sleep = testParm.timePeriod * 1000;
            System.out.println("Going to sleep" + testParm.timePeriod+"seconds now...");

            Thread.currentThread().sleep( sleep ) ;
            for (int t =0 ; t<classKeys.size(); t++)
            {
                int classKey = ((Integer)classKeys.get(t)).intValue();

                com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketCallback =
                        TestCallbackFactory.getV2CurrentMarketConsumer(new CurrentMarketCallbackDV2(currentMarketMeter,t));

                com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapCallback =
                        TestCallbackFactory.getV2RecapConsumer(new RecapCallbackDV2(recapMeter,t));

                userSessionManagerV2.getMarketQueryV2().unsubscribeCurrentMarketForClassV2(testParm.sessionName,classKey,currentMarketCallback);
                System.out.println("Unsubscribed current market for " + classKey);
                userSessionManagerV2.getMarketQueryV2().unsubscribeRecapForClassV2(testParm.sessionName,classKey,recapCallback);
                System.out.println("Unsubscribed recap for " + classKey);

                com.cboe.idl.cmiCallbackV2.CMIRecapConsumer underlyingRecapCallback =
                        TestCallbackFactory.getV2RecapConsumer(new RecapCallbackDV2(underlyingRecapMeter,t));

                com.cboe.idl.cmiCallbackV2.CMITickerConsumer underlyingTickerCallback =
                        TestCallbackFactory.getV2TickerConsumer(new TickerCallbackDV2(underlyingTickerMeter,t));

                SessionClassStruct underlyingClass;
                underlyingClass = userSessionManager.getTradingSession().getClassBySessionForKey(testParm.sessionName,
                        classKey);

                userSessionManagerV2.getMarketQueryV2().unsubscribeRecapForProductV2(underlyingClass.underlyingSessionName,
                        underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingRecapCallback);
                System.out.println("Unsubscribed underlying recap for " + underlyingClass.underlyingSessionName + ":"
                    + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);

                userSessionManagerV2.getMarketQueryV2().unsubscribeTickerForClassV2(underlyingClass.underlyingSessionName,
                        underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingTickerCallback);
                System.out.println("Unsubscribed underlying ticker for " + underlyingClass.underlyingSessionName + ":"
                    + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);
            }
           }
           catch (Exception e)
           {
               e.printStackTrace() ;
               return ;
           }
        }
    }

    public static void testMDLogin(String iniFile)
    {
        CASMeter recapMeter = null;
        CASMeter currentMarketMeter = null;
        CASMeter bestBookMeter = null;
        CASMeter tickerMeter = null;
        CASMeter underlyingRecapMeter = null;
        CASMeter underlyingTickerMeter = null;

        int totalClasses = 0;
        PrintWriter writer = null;

        try {
             writer =  new PrintWriter(new FileWriter("mdsummary.out", true));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        writer.println("time,currentmarket,bookdepth,recap,ticker,underlyingrecap,underlyingticker,total");
        writer.flush();

        Properties props = TestParameter.initializeProperties(iniFile);
        TestParameter testParm = TestParameter.getInstance();
        testParm.testType = "L";
        testParm.getParameters(props);
        try
        {
            ArrayList classes = new ArrayList();
            int lastClass = 0;
            ProdClass product = (ProdClass)testParm.productKeys.get(0);
            classes.add(product);
            int currentClass = product.itsClassKey;
            System.out.println("added class = " + classes.size() + " -> " + currentClass);
            for ( int i = 0; i < testParm.productKeys.size(); i++)
            {
                product = (ProdClass)testParm.productKeys.get(i);
                currentClass = product.itsClassKey;
                if (lastClass != currentClass)
                {
                    if (lastClass != 0)
                    {
                        classes.add(product);
                        System.out.println("added class = " + classes.size() + " -> " + currentClass);
                    }
                    lastClass = currentClass;
                }
            }

            SessionManagerStructV2[] sessionManagerStructs = new SessionManagerStructV2[testParm.userNames.size()*testParm.loginsPerUser];
            for (int k = 0; k < testParm.userNames.size(); k++)
            {
                for (int u = 0; u < testParm.loginsPerUser; u++)
                {
                    SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(k).toString(), (String)testParm.userNames.get(k),
                             testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                    //(t + t * (testParm.loginsPerUser - 1) + u)
                    sessionManagerStructs[k + k *(testParm.loginsPerUser - 1) + u] = sessionManagerStructV2;
                    System.out.println("user name = " +  (String)testParm.userNames.get(k) + " : " + u);
                }
            }

            for (int k = 0; k < testParm.userNames.size() * testParm.loginsPerUser; k++)
            {
                recapMeter = MDRateMeter.getMDMeter(MDRateMeter.RECAP, classes.size(), k);
                currentMarketMeter = MDRateMeter.getMDMeter(MDRateMeter.CURRENTMARKET, classes.size(), k);

                underlyingRecapMeter = MDRateMeter.getMDMeter(MDRateMeter.UNDERLYING_RECAP, classes.size(), k);
                underlyingTickerMeter = MDRateMeter.getMDMeter(MDRateMeter.UNDERLYING_TICKER, classes.size(), k);

                totalClasses = classes.size();
                for (int t = 0; t < classes.size(); t++)
                {
                    ProdClass sessionClass = (ProdClass)classes.get(t);

                    if (!testParm.underlyingOnly)
                    {
                        CurrentMarketCallbackDV2 currentMarketClient = new CurrentMarketCallbackDV2(currentMarketMeter,t);
                        com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer marketCallback =
                                TestCallbackFactory.getV2CurrentMarketConsumer(currentMarketClient);

                        RecapCallbackDV2 recapCallbackClient = new RecapCallbackDV2(recapMeter,t);
                        com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapCallback =
                                TestCallbackFactory.getV2RecapConsumer(recapCallbackClient);

                        sessionManagerStructs[k].sessionManagerV2.getMarketQueryV2().subscribeCurrentMarketForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey,marketCallback,testParm.queueAction);
                        System.out.println("Subscribed current market for " + sessionClass.itsClassKey);

                        sessionManagerStructs[k].sessionManagerV2.getMarketQueryV2().subscribeRecapForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey, recapCallback,testParm.queueAction);
                        System.out.println("Subscribed recap for " + sessionClass.itsClassKey);
                    }

                    if (testParm.includeUnderlying)
                    {
                        try {
                            RecapCallbackDV2 underlyingRecapClient = new RecapCallbackDV2(underlyingRecapMeter,t);
                            TickerCallbackDV2  underlyingTickerClient = new TickerCallbackDV2(underlyingTickerMeter,t);

                            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer underlyingRecapCallback =
                                    TestCallbackFactory.getV2RecapConsumer(underlyingRecapClient);

                            com.cboe.idl.cmiCallbackV2.CMITickerConsumer underlyingTickerCallback =
                                    TestCallbackFactory.getV2TickerConsumer(underlyingTickerClient);

                            SessionClassStruct underlyingClass;
                            underlyingClass = sessionManagerStructs[k].sessionManager.getTradingSession().getClassBySessionForKey(sessionClass.itsSessionName,
                                    sessionClass.itsClassKey);

                            sessionManagerStructs[k].sessionManagerV2.getMarketQueryV2().subscribeRecapForProductV2(underlyingClass.underlyingSessionName,
                                    underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingRecapCallback,testParm.queueAction);

                            System.out.println("Subscribed underlying recap for " + underlyingClass.underlyingSessionName + ":"
                                + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);

                            sessionManagerStructs[k].sessionManagerV2.getMarketQueryV2().subscribeTickerForProductV2(underlyingClass.underlyingSessionName,
                                    underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingTickerCallback,testParm.queueAction);
                            System.out.println("Subscribed underlying ticker for " + underlyingClass.underlyingSessionName + ":"
                                + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                bestBookMeter = MDRateMeter.getMDMeter(MDRateMeter.BESTBOOK, classes.size(), k);
                tickerMeter = MDRateMeter.getMDMeter(MDRateMeter.TICKER, classes.size(), k);


                lastClass = 0;
                int bucket = 0;
                BestBookCallbackDV2 bestBookCallbackDV2 = new BestBookCallbackDV2(bestBookMeter, bucket);
                TickerCallbackDV2 tickerCallbackDV2 = new TickerCallbackDV2(tickerMeter, bucket);
                com.cboe.idl.cmiCallbackV2.CMITickerConsumer tickerCallback = null;
                com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer bestBookCallback = null;

                if (! testParm.underlyingOnly)
                {
                    for (int t = 0; t < testParm.productKeys.size(); t++)
                    {
                        product = (ProdClass)testParm.productKeys.get(t);
                        if ( lastClass != product.itsClassKey )
                        {
                            bestBookCallbackDV2 = new BestBookCallbackDV2(bestBookMeter, bucket);
                            bestBookCallback = TestCallbackFactory.getV2BestBookConsumer(bestBookCallbackDV2);

                            tickerCallbackDV2 = new TickerCallbackDV2(tickerMeter, bucket++);
                            tickerCallback = TestCallbackFactory.getV2TickerConsumer(tickerCallbackDV2);
                        }

                        try
                        {
                            sessionManagerStructs[k].sessionManagerV2.getMarketQueryV2().subscribeBookDepthForProductV2(product.itsSessionName,product.itsProductKey,bestBookCallback,testParm.queueAction);
                            System.out.println("Subscribed book depth for " + product.itsProductKey + " in bucket " + bucket);

                            sessionManagerStructs[k].sessionManagerV2.getMarketQueryV2().subscribeTickerForProductV2(product.itsSessionName,product.itsProductKey,tickerCallback,testParm.queueAction);
                            System.out.println("Subscribed ticker for " + product.itsProductKey + " in bucket " + bucket);
                        }
                        catch (DataValidationException dve)
                        {
                            //Keep going.....
                            System.out.println(dve.details);
                        }
                        lastClass = product.itsClassKey;
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
        try {
            long totalRecap=0;
            long lastRecap=0;
            long totalCurrentMarket=0;
            long lastCurrentMarket=0;
            long totalBestBook=0;
            long lastBestBook=0;
            long totalTicker=0;
            long lastTicker=0;
            long lastUnderlyingRecap=0;
            long totalUnderlyingRecap=0;
            long lastUnderlyingTicker=0;
            long totalUnderlyingTicker=0;

            executionMode();
            System.out.println("Starting test");
            testParm.threadDone = false;
            while (!testParm.threadDone)
            {
/*                System.out.println("---------------------");
                System.out.println(GregorianCalendar.getInstance().getTime());
                totalRecap = 0;
                totalCurrentMarket = 0;
                totalBestBook = 0;
                totalTicker = 0;
                totalUnderlyingRecap = 0;
                totalUnderlyingTicker = 0;
                for (int i = 0; i < totalClasses; i++)
                {
                    totalRecap += recapMeter.aLaps[i].lFillCount;
                    totalCurrentMarket += currentMarketMeter.aLaps[i].lFillCount;
                    totalBestBook += bestBookMeter.aLaps[i].lFillCount;
                    totalTicker += tickerMeter.aLaps[i].lFillCount;
                    totalUnderlyingRecap += underlyingRecapMeter.aLaps[i].lFillCount;
                    totalUnderlyingTicker += underlyingTickerMeter.aLaps[i].lFillCount;
                }
                long diffCM = (totalCurrentMarket - lastCurrentMarket);
                long diffR = (totalRecap - lastRecap);
                long diffB = (totalBestBook - lastBestBook);
                long diffT = (totalTicker - lastTicker);
                long diffUR = (totalUnderlyingRecap - lastUnderlyingRecap);
                long diffUT = (totalUnderlyingTicker - lastUnderlyingTicker);

                float totalRate = (float) ( ((float)(diffCM + diffR + diffB + diffT + diffUR + diffUT))/(float)60 );
                float rateCM = (float) ( ((float)(diffCM))/(float)60 );
                float rateR = (float) ( ((float)(diffR))/(float)60 );
                float rateB = (float) ( ((float)(diffB))/(float)60 );
                float rateT = (float) ( ((float)(diffT))/(float)60 );
                float rateUR = (float) ( ((float)(diffUR))/(float)60 );
                float rateUT = (float) ( ((float)(diffUT))/(float)60 );

                System.out.println("current market = " + diffCM + " :: " + totalCurrentMarket + " :: rate = " + rateCM);
                System.out.println("recap = " + diffR + " :: " + totalRecap + " :: rate = " + rateR);
                System.out.println("book depth = " + diffB + " :: " + totalBestBook + " :: rate = " + rateB);
                System.out.println("ticker = " + diffT + " :: " + totalTicker + " :: rate = " + rateT);
                System.out.println("Underlying Recap = " + diffUR + " :: " + totalUnderlyingRecap + " :: rate = " + rateUR);
                System.out.println("Underlying Ticker = " + diffUT + " :: " + totalUnderlyingTicker + " :: rate = " + rateUT);
                System.out.println("Market data rate = " + totalRate);
                System.out.println("------------------------------");
                writer.println(GregorianCalendar.getInstance().getTime() + "," + rateCM
                        + "," + rateB + "," + rateR + "," + rateT + "," +
                        rateUR + "," + rateUT + "," + totalRate);
                writer.flush();
                lastRecap = totalRecap;
                lastCurrentMarket = totalCurrentMarket;
                lastBestBook = totalBestBook;
                lastTicker = totalTicker;
                lastUnderlyingTicker = totalUnderlyingTicker;
                lastUnderlyingRecap = totalUnderlyingRecap;
*/
                System.out.println("DUMP####################################################################");
                MDRateMeter.dumpCount();
                Thread.sleep(60000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public static void testMarketData(String iniFile)
    {
        CASMeter recapMeter = new CASMeter();
        CASMeter currentMarketMeter = new CASMeter();
        CASMeter bestBookMeter = new CASMeter();
        CASMeter tickerMeter = new CASMeter();
        CASMeter underlyingRecapMeter = new CASMeter();
        CASMeter underlyingTickerMeter = new CASMeter();

        PrintWriter cmWriter = null;
        PrintWriter recapWriter = null;
        PrintWriter underlyingTickerWriter = null;
        PrintWriter underlyingRecapWriter = null;
        PrintWriter bestbookWriter = null;
        PrintWriter tickerWriter = null;
        Vector messageCounters = new Vector(300);

        int totalClasses = 0;
        PrintWriter writer = null;

        try {
             writer =  new PrintWriter(new FileWriter("mdsummary.out"));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        writer.println("time,currentmarket,bookdepth,recap,ticker,underlyingrecap,underlyingticker,total");
        writer.flush();

        Properties props = TestParameter.initializeProperties(iniFile);
        TestParameter testParm = TestParameter.getInstance();
        testParm.testType = "M";
        testParm.getParameters(props);
        try
        {
            ArrayList classes = new ArrayList();
            int lastClass = 0;
            ProdClass product = (ProdClass)testParm.productKeys.get(0);
            classes.add(product);
            int currentClass = product.itsClassKey;
            System.out.println("added class = " + classes.size() + " -> " + currentClass);
            for ( int i = 0; i < testParm.productKeys.size(); i++)
            {
                product = (ProdClass)testParm.productKeys.get(i);
                currentClass = product.itsClassKey;
                if (lastClass != currentClass)
                {
                    if (lastClass != 0)
                    {
                        classes.add(product);
                        System.out.println("added class = " + classes.size() + " -> " + currentClass);
                    }
                    lastClass = currentClass;
                }
            }

            SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(0).toString(), (String)testParm.userNames.get(0), testParm.host, testParm.port
                    , testParm.mode, testParm.gmdText, testParm.sessionMode);

            UserSessionManagerV2 userSessionManagerV2 = sessionManagerStructV2.sessionManagerV2;
            UserSessionManager userSessionManager = sessionManagerStructV2.sessionManager;
            System.out.println("user name = " +  (String)testParm.userNames.get(0));
            System.out.println("Subscribing to Book Depth, Current Market, Recap, Ticker");
            if (testParm.includeUnderlying)
            {
                System.out.println("   and Underlying Recap, Underlying Ticker");
            }

            recapMeter = CASMeter.create("Recap.out", classes.size());
            currentMarketMeter = CASMeter.create("CurrentMarket.out", classes.size());

            underlyingRecapMeter = CASMeter.create("UnderlyingRecap.out", classes.size());
            underlyingTickerMeter = CASMeter.create("UnderlyingTicker.out", classes.size());

            totalClasses = classes.size();
            for (int t = 0; t < classes.size(); t++)
            {
                ProdClass sessionClass = (ProdClass)classes.get(t);

                if (!testParm.underlyingOnly)
                {
                    com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer marketCallback =
                            TestCallbackFactory.getV2CurrentMarketConsumer(new CurrentMarketCallbackDV2(currentMarketMeter,t));

                    com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapCallback =
                            TestCallbackFactory.getV2RecapConsumer(new RecapCallbackDV2(recapMeter,t));

                    userSessionManagerV2.getMarketQueryV2().subscribeCurrentMarketForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey,marketCallback,testParm.queueAction);
                    System.out.println("Subscribed current market for " + sessionClass.itsClassKey);
                    userSessionManagerV2.getMarketQueryV2().subscribeRecapForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey, recapCallback,testParm.queueAction);
                    System.out.println("Subscribed recap for " + sessionClass.itsClassKey);
                }

                if (testParm.includeUnderlying)
                {
                    try {
                        RecapCallbackDV2 underlyingRecapClient = new RecapCallbackDV2(underlyingRecapMeter,t);
                        TickerCallbackDV2  underlyingTickerClient = new TickerCallbackDV2(underlyingTickerMeter,t);

                        com.cboe.idl.cmiCallbackV2.CMIRecapConsumer underlyingRecapCallback =
                                TestCallbackFactory.getV2RecapConsumer(underlyingRecapClient);

                        com.cboe.idl.cmiCallbackV2.CMITickerConsumer underlyingTickerCallback =
                                TestCallbackFactory.getV2TickerConsumer(underlyingTickerClient);

                        SessionClassStruct underlyingClass;
                        underlyingClass = userSessionManager.getTradingSession().getClassBySessionForKey(sessionClass.itsSessionName,
                                sessionClass.itsClassKey);

                        underlyingRecapClient.addMessageCounter(MDCollector.getMarketDataCounter(MDCollector.UNDERLYING_RECAP, underlyingClass.classStruct.classKey));
                        underlyingTickerClient.addMessageCounter(MDCollector.getMarketDataCounter(MDCollector.UNDERLYING_TICKER, underlyingClass.classStruct.classKey));

                        userSessionManagerV2.getMarketQueryV2().subscribeRecapForProductV2(underlyingClass.underlyingSessionName,
                                underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingRecapCallback,testParm.queueAction);

                        System.out.println("Subscribed underlying recap for " + underlyingClass.underlyingSessionName + ":"
                            + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);

                        userSessionManagerV2.getMarketQueryV2().subscribeTickerForProductV2(underlyingClass.underlyingSessionName,
                                underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingTickerCallback,testParm.queueAction);
                        System.out.println("Subscribed underlying ticker for " + underlyingClass.underlyingSessionName + ":"
                            + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            bestBookMeter = CASMeter.create("BestBook.out", classes.size());
            tickerMeter = CASMeter.create("Ticker.oGut", classes.size());


            lastClass = 0;
            int bucket = 0;
            BestBookCallbackDV2 bestBookCallbackDV2 = new BestBookCallbackDV2(bestBookMeter, bucket);
            TickerCallbackDV2 tickerCallbackDV2 = new TickerCallbackDV2(tickerMeter, bucket);
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer tickerCallback = null;
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer bestBookCallback = null;

            if (! testParm.underlyingOnly)
            {
                for (int t = 0; t < testParm.productKeys.size(); t++)
                {
                    product = (ProdClass)testParm.productKeys.get(t);
                    if ( lastClass != product.itsClassKey )
                    {
                        bestBookCallbackDV2 = new BestBookCallbackDV2(bestBookMeter, bucket);
                        bestBookCallback = TestCallbackFactory.getV2BestBookConsumer(bestBookCallbackDV2);

                        tickerCallbackDV2 = new TickerCallbackDV2(tickerMeter, bucket++);
                        tickerCallback = TestCallbackFactory.getV2TickerConsumer(tickerCallbackDV2);
                    }

                    try
                    {
                        userSessionManagerV2.getMarketQueryV2().subscribeBookDepthForProductV2(product.itsSessionName,product.itsProductKey,bestBookCallback,testParm.queueAction);
                        System.out.println("Subscribed book depth for " + product.itsProductKey + " in bucket " + bucket);

                        userSessionManagerV2.getMarketQueryV2().subscribeTickerForProductV2(product.itsSessionName,product.itsProductKey,tickerCallback,testParm.queueAction);
                        System.out.println("Subscribed ticker for " + product.itsProductKey + " in bucket " + bucket);
                    }
                    catch (DataValidationException dve)
                    {
                        //Keep going.....
                        System.out.println(dve.details.message);
                    }
                    lastClass = product.itsClassKey;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
        try {
            long totalRecap=0;
            long lastRecap=0;
            long totalCurrentMarket=0;
            long lastCurrentMarket=0;
            long totalBestBook=0;
            long lastBestBook=0;
            long totalTicker=0;
            long lastTicker=0;
            long lastUnderlyingRecap=0;
            long totalUnderlyingRecap=0;
            long lastUnderlyingTicker=0;
            long totalUnderlyingTicker=0;

            executionMode();
            System.out.println("Starting test");
            testParm.threadDone = false;
            while (!testParm.threadDone)
            {
                System.out.println("---------------------");
                System.out.println(GregorianCalendar.getInstance().getTime());
                totalRecap = 0;
                totalCurrentMarket = 0;
                totalBestBook = 0;
                totalTicker = 0;
                totalUnderlyingRecap = 0;
                totalUnderlyingTicker = 0;
                for (int i = 0; i < totalClasses; i++)
                {
                    totalRecap += recapMeter.getLaps(i).lFillCount;
                    totalCurrentMarket += currentMarketMeter.getLaps(i).lFillCount;
                    totalBestBook += bestBookMeter.getLaps(i).lFillCount;
                    totalTicker += tickerMeter.getLaps(i).lFillCount;
                    totalUnderlyingRecap += underlyingRecapMeter.getLaps(i).lFillCount;
                    totalUnderlyingTicker += underlyingTickerMeter.getLaps(i).lFillCount;
                }
                long diffCM = (totalCurrentMarket - lastCurrentMarket);
                long diffR = (totalRecap - lastRecap);
                long diffB = (totalBestBook - lastBestBook);
                long diffT = (totalTicker - lastTicker);
                long diffUR = (totalUnderlyingRecap - lastUnderlyingRecap);
                long diffUT = (totalUnderlyingTicker - lastUnderlyingTicker);

                float totalRate = (float) ( ((float)(diffCM + diffR + diffB + diffT + diffUR + diffUT))/(float)60 );
                float rateCM = (float) ( ((float)(diffCM))/(float)60 );
                float rateR = (float) ( ((float)(diffR))/(float)60 );
                float rateB = (float) ( ((float)(diffB))/(float)60 );
                float rateT = (float) ( ((float)(diffT))/(float)60 );
                float rateUR = (float) ( ((float)(diffUR))/(float)60 );
                float rateUT = (float) ( ((float)(diffUT))/(float)60 );

                System.out.println("current market = " + diffCM + " :: " + totalCurrentMarket + " :: rate = " + rateCM);
                System.out.println("recap = " + diffR + " :: " + totalRecap + " :: rate = " + rateR);
                System.out.println("book depth = " + diffB + " :: " + totalBestBook + " :: rate = " + rateB);
                System.out.println("ticker = " + diffT + " :: " + totalTicker + " :: rate = " + rateT);
                System.out.println("Underlying Recap = " + diffUR + " :: " + totalUnderlyingRecap + " :: rate = " + rateUR);
                System.out.println("Underlying Ticker = " + diffUT + " :: " + totalUnderlyingTicker + " :: rate = " + rateUT);
                System.out.println("Market data rate = " + totalRate);
                System.out.println("------------------------------");
                writer.println(GregorianCalendar.getInstance().getTime() + "," + rateCM
                        + "," + rateB + "," + rateR + "," + rateT + "," +
                        rateUR + "," + rateUT + "," + totalRate);
                writer.flush();
                lastRecap = totalRecap;
                lastCurrentMarket = totalCurrentMarket;
                lastBestBook = totalBestBook;
                lastTicker = totalTicker;
                lastUnderlyingTicker = totalUnderlyingTicker;
                lastUnderlyingRecap = totalUnderlyingRecap;

                MDCollector.dumpCount();
                Thread.sleep(60000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public static void testMarketDataV3(String iniFile)
    {
        CASMeter recapMeter = new CASMeter();
        CASMeter bestMarketMeter = new CASMeter();
        CASMeter bestPublicMarketMeter = new CASMeter();
        CASMeter bestBookMeter = new CASMeter();
        CASMeter tickerMeter = new CASMeter();
        CASMeter underlyingRecapMeter = new CASMeter();
        CASMeter underlyingTickerMeter = new CASMeter();

//        PrintWriter cmWriter = null;
//        PrintWriter recapWriter = null;
//        PrintWriter underlyingTickerWriter = null;
//        PrintWriter underlyingRecapWriter = null;
//        PrintWriter bestbookWriter = null;
//        PrintWriter tickerWriter = null;
//        Vector messageCounters = new Vector(300);

        int totalClasses = 0;
        PrintWriter writer = null;

        try {
             writer =  new PrintWriter(new FileWriter("mdsummary.out"));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        writer.println("time,bestmarket,publicbest,bookdepth,recap,ticker,underlyingrecap,underlyingticker,total");
        writer.flush();

        Properties props = TestParameter.initializeProperties(iniFile);
        TestParameter testParm = TestParameter.getInstance();
        testParm.testType = "M";
        testParm.getParameters(props);
        try
        {
            ArrayList classes = new ArrayList();
            int lastClass = 0;
            ProdClass product = (ProdClass)testParm.productKeys.get(0);
            classes.add(product);
            int currentClass = product.itsClassKey;
            System.out.println("added class = " + classes.size() + " -> " + currentClass);
            for ( int i = 0; i < testParm.productKeys.size(); i++)
            {
                product = (ProdClass)testParm.productKeys.get(i);
                currentClass = product.itsClassKey;
                if (lastClass != currentClass)
                {
                    if (lastClass != 0)
                    {
                        classes.add(product);
                        System.out.println("added class = " + classes.size() + " -> " + currentClass);
                    }
                    lastClass = currentClass;
                }
            }

            UserSessionManagerV3 sessionManagerV3 = CASLogon.logonToCASV3(testParm.userNames.get(0).toString(), (String)testParm.userNames.get(0), testParm.host, testParm.port
                    , testParm.mode, testParm.gmdText, testParm.sessionMode);

//            UserSessionManagerV2 userSessionManagerV2 = sessionManagerStructV2.sessionManagerV2;
//            UserSessionManager userSessionManager = sessionManagerStructV2.sessionManager;
            System.out.println("user name = " +  (String)testParm.userNames.get(0));
            System.out.println("Subscribing to Book Depth, Current Market, Recap, Ticker");
            if (testParm.includeUnderlying)
            {
                System.out.println("   and Underlying Recap, Underlying Ticker");
            }

            recapMeter = CASMeter.create("Recap.out", classes.size());
            bestMarketMeter = CASMeter.create("BestMarket.out", classes.size());
            bestPublicMarketMeter = CASMeter.create("BestPublic.out", classes.size());

            underlyingRecapMeter = CASMeter.create("UnderlyingRecap.out", classes.size());
            underlyingTickerMeter = CASMeter.create("UnderlyingTicker.out", classes.size());
            bestBookMeter = CASMeter.create("BestBook.out", classes.size());
            tickerMeter = CASMeter.create("Ticker.oGut", classes.size());

            totalClasses = classes.size();
            for (int t = 0; t < classes.size(); t++)
            {
                ProdClass sessionClass = (ProdClass)classes.get(t);

                if (!testParm.underlyingOnly)
                {
                    com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer marketCallback =
                            TestCallbackFactory.getV3CurrentMarketConsumer(new CurrentMarketCallbackDV3(bestMarketMeter, bestPublicMarketMeter,t));

                    com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapCallback =
                            TestCallbackFactory.getV2RecapConsumer(new RecapCallbackDV2(recapMeter,t));

                    com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer bestBookCallback = TestCallbackFactory.getV2BestBookConsumer(new BestBookCallbackDV2(bestBookMeter, t));    //
                    com.cboe.idl.cmiCallbackV2.CMITickerConsumer tickerCallback = TestCallbackFactory.getV2TickerConsumer(new TickerCallbackDV2(tickerMeter, t));            //


                    sessionManagerV3.getMarketQueryV3().subscribeCurrentMarketForClassV3(sessionClass.itsSessionName,sessionClass.itsClassKey,marketCallback,testParm.queueAction);
                    sessionManagerV3.getMarketQueryV3().subscribeBookDepthForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey,bestBookCallback,testParm.queueAction);
                    sessionManagerV3.getMarketQueryV3().subscribeTickerForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey,tickerCallback,testParm.queueAction);


                    System.out.println("Subscribed currentmarket V3 for " + sessionClass.itsClassKey);
                    sessionManagerV3.getMarketQueryV3().subscribeRecapForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey, recapCallback,testParm.queueAction);
                    System.out.println("Subscribed recap V2 through V3 MQ for " + sessionClass.itsClassKey);
                }

                if (testParm.includeUnderlying)
                {
                    try {
                        RecapCallbackDV2 underlyingRecapClient = new RecapCallbackDV2(underlyingRecapMeter,t);
                        TickerCallbackDV2  underlyingTickerClient = new TickerCallbackDV2(underlyingTickerMeter,t);

                        com.cboe.idl.cmiCallbackV2.CMIRecapConsumer underlyingRecapCallback =
                                TestCallbackFactory.getV2RecapConsumer(underlyingRecapClient);

                        com.cboe.idl.cmiCallbackV2.CMITickerConsumer underlyingTickerCallback =
                                TestCallbackFactory.getV2TickerConsumer(underlyingTickerClient);

                        SessionClassStruct underlyingClass;
                        underlyingClass = sessionManagerV3.getTradingSession().getClassBySessionForKey(sessionClass.itsSessionName,
                                sessionClass.itsClassKey);

                        underlyingRecapClient.addMessageCounter(MDCollector.getMarketDataCounter(MDCollector.UNDERLYING_RECAP, underlyingClass.classStruct.classKey));
                        underlyingTickerClient.addMessageCounter(MDCollector.getMarketDataCounter(MDCollector.UNDERLYING_TICKER, underlyingClass.classStruct.classKey));

                        sessionManagerV3.getMarketQueryV3().subscribeRecapForProductV2(underlyingClass.underlyingSessionName,
                                underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingRecapCallback,testParm.queueAction);

                        System.out.println("Subscribed underlying recap V2 through V3 MQ for " + underlyingClass.underlyingSessionName + ":"
                            + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);

                        sessionManagerV3.getMarketQueryV3().subscribeTickerForProductV2(underlyingClass.underlyingSessionName,
                                underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingTickerCallback,testParm.queueAction);
                        System.out.println("Subscribed underlying ticker V2 through V3 MQ for " + underlyingClass.underlyingSessionName + ":"
                            + underlyingClass.classStruct.underlyingProduct.productKeys.productKey);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

//            bestBookMeter = CASMeter.create("BestBook.out", classes.size());
//            tickerMeter = CASMeter.create("Ticker.oGut", classes.size());

/*
            lastClass = 0;
            int bucket = 0;

            BestBookCallbackDV2 bestBookCallbackDV2 = new BestBookCallbackDV2(bestBookMeter, bucket);
            TickerCallbackDV2 tickerCallbackDV2 = new TickerCallbackDV2(tickerMeter, bucket);
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer tickerCallback = null;
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer bestBookCallback = null;

            if (! testParm.underlyingOnly)
            {
                for (int t = 0; t < testParm.productKeys.size(); t++)
                {
                    product = (ProdClass)testParm.productKeys.get(t);
                    if ( lastClass != product.itsClassKey )
                    {
                        bestBookCallbackDV2 = new BestBookCallbackDV2(bestBookMeter, bucket);
                        bestBookCallback = TestCallbackFactory.getV2BestBookConsumer(bestBookCallbackDV2);

                        tickerCallbackDV2 = new TickerCallbackDV2(tickerMeter, bucket++);
                        tickerCallback = TestCallbackFactory.getV2TickerConsumer(tickerCallbackDV2);
                    }

                    try
                    {
                        sessionManagerV3.getMarketQueryV3().subscribeBookDepthForProductV2(product.itsSessionName,product.itsProductKey,bestBookCallback,testParm.queueAction);
                        System.out.println("Subscribed book depth V2 through V3 MQ for " + product.itsProductKey + " in bucket " + bucket);

                        sessionManagerV3.getMarketQueryV3().subscribeTickerForProductV2(product.itsSessionName,product.itsProductKey,tickerCallback,testParm.queueAction);
                        System.out.println("Subscribed ticker V2 through V3 MQ for " + product.itsProductKey + " in bucket " + bucket);
                    }
                    catch (DataValidationException dve)
                    {
                        //Keep going.....
                        System.out.println(dve.details);
                    }
                    lastClass = product.itsClassKey;
                }
            }
            */
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
        try {
            long totalRecap=0;
            long lastRecap=0;
            long totalCurrentMarket=0;
            long totalbestMarket=0;
            long totalbestPublic=0;
            long lastBestMarket=0;
            long lastBestPublic=0;
            long lastCurrentMarket=0;
            long totalBestBook=0;
            long lastBestBook=0;
            long totalTicker=0;
            long lastTicker=0;
            long lastUnderlyingRecap=0;
            long totalUnderlyingRecap=0;
            long lastUnderlyingTicker=0;
            long totalUnderlyingTicker=0;

            executionMode();
            System.out.println("Starting test");
            testParm.threadDone = false;
            while (!testParm.threadDone)
            {
                System.out.println("---------------------");
                System.out.println(GregorianCalendar.getInstance().getTime());
                totalRecap = 0;
                totalCurrentMarket = 0;
                totalBestBook = 0;
                totalTicker = 0;
                totalUnderlyingRecap = 0;
                totalUnderlyingTicker = 0;
                totalbestMarket = 0;
                totalbestPublic = 0;

                for (int i = 0; i < totalClasses; i++)
                {
                    totalRecap += recapMeter.getLaps(i).lFillCount;
                    totalbestMarket += bestMarketMeter.getLaps(i).lFillCount;
                    totalbestPublic += bestPublicMarketMeter.getLaps(i).lFillCount;
                    totalBestBook += bestBookMeter.getLaps(i).lFillCount;
                    totalTicker += tickerMeter.getLaps(i).lFillCount;
                    totalUnderlyingRecap += underlyingRecapMeter.getLaps(i).lFillCount;
                    totalUnderlyingTicker += underlyingTickerMeter.getLaps(i).lFillCount;
                }
//                long diffCM = (totalCurrentMarket - lastCurrentMarket);
                long diffbestMarket = totalbestMarket - lastBestMarket;
                long diffbestPublic = totalbestPublic - lastBestPublic;
                long diffR = (totalRecap - lastRecap);
                long diffB = (totalBestBook - lastBestBook);
                long diffT = (totalTicker - lastTicker);
                long diffUR = (totalUnderlyingRecap - lastUnderlyingRecap);
                long diffUT = (totalUnderlyingTicker - lastUnderlyingTicker);

//                float totalRate = (float) ( ((float)(diffCM + diffR + diffB + diffT + diffUR + diffUT))/(float)60 );
//                float rateCM = (float) ( ((float)(diffCM))/(float)60 );
                float totalRate = (float) ( ((float)(diffbestMarket + + diffbestPublic + diffR + diffB + diffT + diffUR + diffUT))/(float)60 );
                float ratebestMarket = (float) ( ((float)(diffbestMarket))/(float)60 );
                float ratebestPublic = (float) ( ((float)(diffbestPublic))/(float)60 );
                float rateR = (float) ( ((float)(diffR))/(float)60 );
                float rateB = (float) ( ((float)(diffB))/(float)60 );
                float rateT = (float) ( ((float)(diffT))/(float)60 );
                float rateUR = (float) ( ((float)(diffUR))/(float)60 );
                float rateUT = (float) ( ((float)(diffUT))/(float)60 );

//                System.out.println("current market = " + diffCM + " :: " + totalCurrentMarket + " :: rate = " + rateCM);
                System.out.println("best market = " + diffbestMarket + " :: " + totalbestMarket + " :: rate = " + ratebestMarket);
                System.out.println("best public = " + diffbestPublic + " :: " + totalbestPublic + " :: rate = " + ratebestPublic);
                System.out.println("recap = " + diffR + " :: " + totalRecap + " :: rate = " + rateR);
                System.out.println("book depth = " + diffB + " :: " + totalBestBook + " :: rate = " + rateB);
                System.out.println("ticker = " + diffT + " :: " + totalTicker + " :: rate = " + rateT);
                System.out.println("Underlying Recap = " + diffUR + " :: " + totalUnderlyingRecap + " :: rate = " + rateUR);
                System.out.println("Underlying Ticker = " + diffUT + " :: " + totalUnderlyingTicker + " :: rate = " + rateUT);
                System.out.println("Market data rate = " + totalRate);
                System.out.println("------------------------------");
//                writer.println(GregorianCalendar.getInstance().getTime() + "," + rateCM
//                        + "," + rateB + "," + rateR + "," + rateT + "," +
//                        rateUR + "," + rateUT + "," + totalRate);
                writer.println(GregorianCalendar.getInstance().getTime() + "," + ratebestMarket + "," + ratebestMarket
                        + "," + rateB + "," + rateR + "," + rateT + "," +
                        rateUR + "," + rateUT + "," + totalRate);

                writer.flush();
                lastRecap = totalRecap;
                lastCurrentMarket = totalCurrentMarket;
                lastBestMarket = totalbestMarket;
                lastBestPublic = totalbestPublic;
                lastBestBook = totalBestBook;
                lastTicker = totalTicker;
                lastUnderlyingTicker = totalUnderlyingTicker;
                lastUnderlyingRecap = totalUnderlyingRecap;

                MDCollector.dumpCount();
                Thread.sleep(60000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public static void recordMarketData(String iniFile)
    {
        Properties props = TestParameter.initializeProperties(iniFile);
        TestParameter testParm = TestParameter.getInstance();
        testParm.getParameters(props);
        ArrayList classKeys = testParm.classKeys;
        UserSessionManagerV2 userSessionManagerV2;
        UserSessionManager userSessionManager;

        try
        {
            SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(0).toString(), (String)testParm.userNames.get(0), testParm.host, testParm.port
                    , testParm.mode, testParm.gmdText, testParm.sessionMode);

            userSessionManagerV2 = sessionManagerStructV2.sessionManagerV2;
            userSessionManager = sessionManagerStructV2.sessionManager;
            System.out.println("user name = " +  (String)testParm.userNames.get(0));
        }
        catch (Exception e)
        {
            e.printStackTrace() ;
            return ;
        }

        System.out.println("starting subscribing for Current Market................");
        System.out.println("classKeys size:" + classKeys.size());

        try
        {
            CurrentMarketCallbackRV2 currentMarketCorba = new CurrentMarketCallbackRV2();
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketCallback =
                    TestCallbackFactory.getV2CurrentMarketConsumerR(currentMarketCorba);


            for (int i =0 ; i < classKeys.size(); i++)
            {
                Integer key = (Integer)(classKeys.get(i));
                int classKey = key.intValue();
                RemoteConnectionFactory.find().register_object(currentMarketCallback);

                userSessionManagerV2.getMarketQueryV2().subscribeCurrentMarketForClassV2(testParm.sessionName,classKey,currentMarketCallback,testParm.queueAction);
                System.out.println("Subscribed current market for " + classKey);
            }
        }
        catch (Exception e)
        {
           e.printStackTrace() ;
           return ;
        }
        while (!testParm.threadDone)
        {
        }
        System.out.println("Finish testing ....................................................");
    }

    public static void testInvalidOrderIDs(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "O";
            testParm.getParameters(props);
            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2[] sessionManagerStructs = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                SessionManagerStructV2 sessionManagerStructV2 =
                sessionManagerStructs[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                //testParm.casMeter
                userSessionManagers[t] = sessionManagerStructs[t].sessionManager;
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestInvalidOrderID[] testInvalidOrderIDs = new CASTestInvalidOrderID[testParm.userNames.size() * testParm.loginsPerUser];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                CASMeter casMeter = CASMeter.create("ordertest" + t + ".out", testParm.numOfTests * testParm.loginsPerUser);
                OrderCallbackD clientCallback = new OrderCallbackD(casMeter);
                org.omg.CORBA.Object orbObject =
                        (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(clientCallback);
                com.cboe.idl.cmiCallback.CMIOrderStatusConsumer cmiObject = CMIOrderStatusConsumerHelper.narrow(orbObject);
                System.out.println("subscribeOrderStatus for user: " + (String)testParm.userNames.get(t));
                userSessionManagers[t].getOrderQuery().subscribeOrdersWithoutPublish(cmiObject, testParm.gmdOrder);

                for (int u = 0; u < testParm.loginsPerUser; u++)
                {
                    System.out.println("Creating thread #" + (t + t * (testParm.loginsPerUser - 1) + u));
                    testInvalidOrderIDs[(t + t * (testParm.loginsPerUser - 1) + u)] = new CASTestInvalidOrderID(testParm, sessionManagerStructs[t],
                            casMeter, t, u, cmiObject);
                }
            }

            executionMode();
            for (int t = 0; t < testParm.userNames.size() * testParm.loginsPerUser; t++)
            {
                testInvalidOrderIDs[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace();
            return ;
        }
    }

    public static void testSeedOrders(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "O";
            testParm.getParameters(props);
            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2[] sessionManagerStructV2 = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerStructV2[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                userSessionManagers[t] = sessionManagerStructV2[t].sessionManager;
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestOrderSeed[] testOrderSeeds = new CASTestOrderSeed[testParm.userNames.size() * testParm.loginsPerUser];

            String subMethod;

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                CASMeter casMeter = CASMeter.create("ordertest" + t + ".out", testParm.numOfTests * testParm.loginsPerUser);
                OrderCallbackD clientCallback = new OrderCallbackD(casMeter);
                org.omg.CORBA.Object orbObject =
                        (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(clientCallback);
                com.cboe.idl.cmiCallback.CMIOrderStatusConsumer cmiObject = CMIOrderStatusConsumerHelper.narrow(orbObject);

                if (testParm.subscribeWithPublish)
                {
                    subMethod = "SubscribeWtihPublish";
                    userSessionManagers[t].getOrderQuery().subscribeOrders(cmiObject, testParm.gmdOrder);
                }
                else
                {
                    subMethod = "SubscribeWtihoutPublish";
                    userSessionManagers[t].getOrderQuery().subscribeOrdersWithoutPublish(cmiObject, testParm.gmdOrder);
                }

                System.out.println("subscribeOrderStatus for user: " + (String)testParm.userNames.get(t) + " with " + subMethod + ".");

                for (int u = 0; u < testParm.loginsPerUser; u++)
                {
                    System.out.println("Creating thread #" + (t + t * (testParm.loginsPerUser - 1) + u));
                    testOrderSeeds[(t + t * (testParm.loginsPerUser - 1) + u)] = new CASTestOrderSeed(testParm, sessionManagerStructV2[t],
                            casMeter, t, u, cmiObject);
                }
            }

            executionMode();
            for (int t = 0; t < testParm.userNames.size() * testParm.loginsPerUser; t++)
            {
                testOrderSeeds[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace();
            return ;
        }
    }

    public static void testOrders(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "O";
            testParm.getParameters(props);
            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2[] sessionManagerStructV2 = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerStructV2[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                userSessionManagers[t] = sessionManagerStructV2[t].sessionManager;
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestOrder[] testOrders = new CASTestOrder[testParm.userNames.size() * testParm.loginsPerUser];

            String subMethod;

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                CASMeter casMeter = CASMeter.create("ordertest" + t + ".out", testParm.numOfTests * testParm.loginsPerUser);
                OrderCallbackD clientCallback = new OrderCallbackD(casMeter);
                org.omg.CORBA.Object orbObject =
                        (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(clientCallback);
                com.cboe.idl.cmiCallback.CMIOrderStatusConsumer cmiObject = CMIOrderStatusConsumerHelper.narrow(orbObject);

                if (testParm.subscribeWithPublish)
                {
                    subMethod = "SubscribeWtihPublish";
                    userSessionManagers[t].getOrderQuery().subscribeOrders(cmiObject, testParm.gmdOrder);
                }
                else
                {
                    subMethod = "SubscribeWtihoutPublish";
                    userSessionManagers[t].getOrderQuery().subscribeOrdersWithoutPublish(cmiObject, testParm.gmdOrder);
                }

                System.out.println("subscribeOrderStatus for user: " + (String)testParm.userNames.get(t) + " with " + subMethod + ".");

                for (int u = 0; u < testParm.loginsPerUser; u++)
                {
                    System.out.println("Creating thread #" + (t + t * (testParm.loginsPerUser - 1) + u));
                    testOrders[(t + t * (testParm.loginsPerUser - 1) + u)] = new CASTestOrder(testParm, sessionManagerStructV2[t],
                            casMeter, t, u, cmiObject);
                }
            }

            executionMode();
            for (int t = 0; t < testParm.userNames.size() * testParm.loginsPerUser; t++)
            {
                testOrders[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace();
            return ;
        }
    }

    public static void testBlockQuotesMultiThreaded(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "O";
            testParm.getParameters(props);
            RateLogger.logQuoteRate("MSGRATE=" + testParm.msgRate + " " + "MSGINT=" + testParm.msgInterval + " "
                        + "STEPUP=" + testParm.stepUp + " " + "STEPUPINT=" + testParm.stepUpInterval + " "
                        + "BlockSize=" + testParm.numPerSeq);
            RateLogger.logQuoteRate("Date/Time" + " " + "Quote/Sec");

            SessionManagerStructV2[] sessionManagerStructV2 = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerStructV2[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);

                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestBlockQuote[] testBlockQuotes = new CASTestBlockQuote[testParm.userNames.size() * testParm.loginsPerUser];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                com.cboe.idl.cmiV2.Quote quoteService = sessionManagerStructV2[t].sessionManagerV2.getQuoteV2();
                for(int u = 0; u < testParm.loginsPerUser; u++)
                {
                    // create a separate CASMeter and consumer for every thread
                    CASMeter casMeter = CASMeter.create("bquotetest" + t + "-" + u + ".out", testParm.numOfTests * testParm.loginsPerUser);
                    QuoteCallbackDV2  callbackV2Consumer = new QuoteCallbackDV2(casMeter);
                    com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListenerV2 = TestCallbackFactory.getV2QuoteStatusConsumer(callbackV2Consumer);
                    quoteService.subscribeQuoteStatusV2( clientListenerV2, testParm.subscribeWithPublish, testParm.includeUserInitiatedStatus, testParm.gmdQuote);

                    System.out.println("Creating thread #" + (t + t * (testParm.loginsPerUser - 1) + u));
                    testBlockQuotes[(t + t * (testParm.loginsPerUser - 1) + u)] =
                            new CASTestBlockQuote(testParm, sessionManagerStructV2[t], casMeter, t, u);
                }
            }

            executionMode();
            for (int t = 0; t < testParm.userNames.size() * testParm.loginsPerUser; t++)
            {
                testBlockQuotes[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace();
            return ;
        }
    }

    public static void testCancelReplace(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "O";
            testParm.getParameters(props);
            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2[] sessionManagerStructV2 = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerStructV2[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                userSessionManagers[t] = sessionManagerStructV2[t].sessionManager;
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestCancelReplace[] testCancelReplace = new CASTestCancelReplace[testParm.userNames.size() * testParm.loginsPerUser];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                CASMeter casMeter = CASMeter.create("cancelreplace" + t + ".out", testParm.numOfTests * testParm.loginsPerUser);
                System.out.println("subscribeOrderStatus for user: " + (String)testParm.userNames.get(t));

                OrderCallbackD clientCallback = new OrderCallbackD(casMeter);

                org.omg.CORBA.Object orbObject =
                        (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(clientCallback);
                com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener = CMIOrderStatusConsumerHelper.narrow(orbObject);

                userSessionManagers[t].getOrderQuery().subscribeOrdersWithoutPublish(clientListener, testParm.gmdOrder);

                for (int u = 0; u < testParm.loginsPerUser; u++)
                {
                    UserOrderCache orderCache = new UserOrderCache(u, testParm.numOfTests);
                    clientCallback.addInterest(orderCache);

                    System.out.println("Creating thread #" + (t + t * (testParm.loginsPerUser - 1) + u));
                    testCancelReplace[(t + t * (testParm.loginsPerUser - 1) + u)] = new CASTestCancelReplace(testParm, sessionManagerStructV2[t],
                            casMeter, t, u, clientListener, orderCache);
                }
            }

            executionMode();
            for (int t = 0; t < testParm.userNames.size() * testParm.loginsPerUser; t++)
            {
                testCancelReplace[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace();
            return ;
        }
    }

    public static void testCancel(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "O";
            testParm.getParameters(props);
            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2[] sessionManagerStructV2 = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerStructV2[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                userSessionManagers[t] = sessionManagerStructV2[t].sessionManager;
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestCancel[] testCancel = new CASTestCancel[testParm.userNames.size() * testParm.loginsPerUser];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                CASMeter casMeter = CASMeter.create("cancel" + t + ".out", testParm.numOfTests * testParm.loginsPerUser);
                System.out.println("subscribeOrderStatus for user: " + (String)testParm.userNames.get(t));

                OrderCallbackC clientCallback = new OrderCallbackC(casMeter);//QuoteStatusConsumerDelegate(cb);

                org.omg.CORBA.Object orbObject =
                        (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(clientCallback);
                com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener = CMIOrderStatusConsumerHelper.narrow(orbObject);

                userSessionManagers[t].getOrderQuery().subscribeOrdersWithoutPublish(clientListener, testParm.gmdOrder);

                for (int u = 0; u < testParm.loginsPerUser; u++)
                {
                    UserOrderCache orderCache = new UserOrderCache(u, testParm.numOfTests);
                    clientCallback.addInterest(orderCache);

                    System.out.println("Creating thread #" + (t + t * (testParm.loginsPerUser - 1) + u));
                    testCancel[(t + t * (testParm.loginsPerUser - 1) + u)] = new CASTestCancel(testParm, sessionManagerStructV2[t],
                            casMeter, t, u, clientListener, orderCache);
                }
            }

            executionMode();
            for (int t = 0; t < testParm.userNames.size() * testParm.loginsPerUser; t++)
            {
                testCancel[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace();
            return ;
        }
    }

    public static void testBlockQuotes(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "B";
            testParm.getParameters(props);
            RateLogger.logQuoteRate("MSGRATE=" + testParm.msgRate + " " + "MSGINT=" + testParm.msgInterval + " "
                        + "STEPUP=" + testParm.stepUp + " " + "STEPUPINT=" + testParm.stepUpInterval + " "
                        + "BlockSize=" + testParm.numPerSeq);
            RateLogger.logQuoteRate("Date/Time" + " " + "Quote/Sec");

            SessionManagerStructV2[] sessionManagerStructs = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                sessionManagerStructs[t] = sessionManagerStructV2;
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestBlockQuote[] testBlockQuotes = new CASTestBlockQuote[testParm.userNames.size()];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                 testBlockQuotes[t] = new CASTestBlockQuote(testParm, sessionManagerStructs[t], CASMeter.create("bquotetest" + t + ".out", testParm.numOfTests), t);
            }

            executionMode();

            long spacing = (testParm.msgInterval * 1000) / testParm.userNames.size();
            System.out.println("User threads delay: " + spacing + "ms");

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testBlockQuotes[t].start();
                System.out.println("Thread " + t + " is started");
                Thread.sleep(spacing);
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    public static void testBlockQuotesV3(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "B";
            testParm.getParameters(props);

            UserSessionManagerV3[] sessionManagerV3 = new UserSessionManagerV3[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerV3[t]  = CASLogon.logonToCASV3(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }
     
            CASTestBlockQuote[] testBlockQuotes = new CASTestBlockQuote[testParm.userNames.size()];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                 testBlockQuotes[t] = new CASTestBlockQuote(testParm, sessionManagerV3[t], CASMeter.create("bquotetest" + t + ".out", testParm.numOfTests), t);
            }

            executionMode();

            long spacing = (testParm.msgInterval * 1000) / testParm.userNames.size();
            System.out.println("User threads delay: " + spacing + "ms");

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testBlockQuotes[t].start();
                System.out.println("Thread " + t + " is started");
                Thread.sleep(spacing);
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }
    
    /**
     * Send Block Quote Cancel.
     * @param iniFile
     * @author krueyay
     */
    public static void testBlockQuoteCancel(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance(); 
            testParm.testType = "BQC2";
            testParm.getParameters(props);
            if (testParm.callbackVersion.equals("V3")) 
            {              
                UserSessionManagerV3[] sessionManagerV3 = new UserSessionManagerV3[testParm.userNames.size()];
                for (int t = 0; t < testParm.userNames.size(); t++)
                {
                    sessionManagerV3[t]  = CASLogon.logonToCASV3(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                             testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                    //testParm.casMeter
                    System.out.println("user name = " +  (String)testParm.userNames.get(t));
                }
         
                CASTestBlockQuoteCancel[] testBlockQuoteCancels = new CASTestBlockQuoteCancel[testParm.userNames.size()];

                for (int t = 0; t < testParm.userNames.size(); t++)
                {
                     testBlockQuoteCancels[t] = new CASTestBlockQuoteCancel(testParm, sessionManagerV3[t], CASMeter.create("bquotetest" + t + ".out", testParm.numOfTests), t);
                }
                executionMode();

                long spacing = (testParm.msgInterval * 1000) / testParm.userNames.size();
                System.out.println("User threads delay: " + spacing + "ms");

                for (int t = 0; t < testParm.userNames.size(); t++)
                {
                    testBlockQuoteCancels[t].start();
                    System.out.println("Thread " + t + " is started");
                    Thread.sleep(spacing);
                }
                
            }
            else
            {
                
                SessionManagerStructV2[] sessionManagerStructs = new SessionManagerStructV2[testParm.userNames.size()];
                for (int t = 0; t < testParm.userNames.size(); t++)
                {
                    SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                             testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                    sessionManagerStructs[t] = sessionManagerStructV2;
                    //testParm.casMeter
                    System.out.println("user name = " +  (String)testParm.userNames.get(t));
                }

                CASTestBlockQuoteCancel[] testBlockQuoteCancels = new CASTestBlockQuoteCancel[testParm.userNames.size()];

                for (int t = 0; t < testParm.userNames.size(); t++)
                {
                     testBlockQuoteCancels[t] = new CASTestBlockQuoteCancel(testParm, sessionManagerStructs[t], CASMeter.create("bquotetest" + t + ".out", testParm.numOfTests), t);
                }
                executionMode();

                long spacing = (testParm.msgInterval * 1000) / testParm.userNames.size();
                System.out.println("User threads delay: " + spacing + "ms");

                for (int t = 0; t < testParm.userNames.size(); t++)
                {
                    testBlockQuoteCancels[t].start();
                    System.out.println("Thread " + t + " is started");
                    Thread.sleep(spacing);
                }
                
            }
            
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }
    public static void testSmartQuoteLock(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "B";
            testParm.getParameters(props);
            SessionManagerStructV2[] sessionManagerStructs = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                sessionManagerStructs[t] = sessionManagerStructV2;
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASSmartQuoteLockTest[] smartQuoteLockTests = new CASSmartQuoteLockTest[testParm.userNames.size()];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                 smartQuoteLockTests[t] = new CASSmartQuoteLockTest(testParm, sessionManagerStructs[t], CASMeter.create("bquotetest" + t + ".out", testParm.numOfTests));
            }

            executionMode();

            long spacing = (testParm.msgInterval * 1000) / testParm.userNames.size();
            System.out.println("User threads delay: " + spacing + "ms");

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                smartQuoteLockTests[t].start();
                System.out.println("Thread " + t + " is started");
                Thread.sleep(spacing);
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    public static void testQuoteQueue(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "B";
            testParm.getParameters(props);
//            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2[] sessionManagerStructs = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                         testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                sessionManagerStructs[t] = sessionManagerStructV2;
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestQuoteQueue[] testQuoteQueues = new CASTestQuoteQueue[testParm.userNames.size()];
//            CASTestQuoteBlockGenerator[] quoteGenerators = new CASTestQuoteBlockGenerator[testParm.userNames.size()];
            QuoteQueue[] quoteQueue = new QuoteQueue[testParm.userNames.size()];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                 CASMeter meter = CASMeter.create("qqtest" + t + ".out", testParm.numOfTests);
                 quoteQueue[t] = new QuoteQueue(3000);
//                 quoteGenerators[t] = new CASTestQuoteBlockGenerator(testParm, meter, quoteQueue[t]);
                 testQuoteQueues[t] = new CASTestQuoteQueue(testParm, sessionManagerStructs[t], meter, t, quoteQueue[t]);
            }

            executionMode();

            long spacing = (testParm.msgInterval * 1000) / testParm.userNames.size();
            System.out.println("User threads delay: " + spacing + "ms");

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testQuoteQueues[t].start();
//                quoteGenerators[t].start();
                System.out.println("Thread " + t + " is started");
                Thread.sleep(spacing);
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    public static void testQuoteCancel(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "B";
            testParm.getParameters(props);
            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2 sessionManagerStructV2[] = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerStructV2[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                        testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestQuoteCancel[] testQuoteCancels = new CASTestQuoteCancel[testParm.userNames.size()];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                 testQuoteCancels[t] = new CASTestQuoteCancel(testParm, sessionManagerStructV2[t], CASMeter.create("bquotetest" + t + ".out", testParm.numOfTests), t);
            }

            System.out.print("Press <enter> to start");
            System.in.read();
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testQuoteCancels[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }
    public static void testBlockQuoteAndCancel(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.testType = "BQC1";
            testParm.getParameters(props);
            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2 sessionManagerStructV2[] = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerStructV2[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                        testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestBlockQuoteAndCancel[] testBlockQuoteAndCancels = new CASTestBlockQuoteAndCancel[testParm.userNames.size()];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                 testBlockQuoteAndCancels[t] = new CASTestBlockQuoteAndCancel(testParm, sessionManagerStructV2[t], CASMeter.create("bquotetest" + t + ".out", testParm.numOfTests), t);
            }

            System.out.print("Press <enter> to start");
            System.in.read();
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testBlockQuoteAndCancels[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }
    public static void testQRM(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.getParameters(props);
            UserSessionManager[] userSessionManagers = new UserSessionManager[testParm.userNames.size()];
            SessionManagerStructV2 sessionManagerStructV2[] = new SessionManagerStructV2[testParm.userNames.size()];
            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                sessionManagerStructV2[t] = CASLogon.logonToCAS(testParm.userNames.get(t).toString(), (String)testParm.userNames.get(t),
                        testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
                //testParm.casMeter
                System.out.println("user name = " +  (String)testParm.userNames.get(t));
            }

            CASTestQRM[] testQRM = new CASTestQRM[testParm.userNames.size()];

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                 testQRM[t] = new CASTestQRM(testParm, sessionManagerStructV2[t]);
            }

            executionMode();

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testQRM[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    public static void testMarketDataLogin(String iniFile)
    {
        Properties props = TestParameter.initializeProperties(iniFile);
        TestParameter testParm = TestParameter.getInstance();
        testParm.getParameters(props);
        try {
/*            UserAccessV3 userAccess = null;

            try
            {
                Object userObject = RemoteConnectionFactory.find().find_initial_object("http://" + testParm.host + ":" + testParm.port, "/UserAccessV3.ior");

                userAccess = UserAccessV3Helper.narrow((org.omg.CORBA.Object) userObject);
            } catch (Throwable e)
            {
                e.printStackTrace();
            }
*/
            CASTestMDLogin[] testLogins = new CASTestMDLogin[testParm.userNames.size()];

            for (int i = 0; i < testParm.userNames.size(); i++)
            {
                String userName = (String) testParm.userNames.get(i);
                testLogins[i] = new CASTestMDLogin(testParm, userName);
            }

            executionMode();

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testLogins[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }

        while (!testParm.threadDone)
        {
            try
            {
                Thread.sleep(60000);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            MDCollector.dumpCount();
        }
    }

    public static void testUserLogin(String iniFile)
    {
        try {
            Properties props = TestParameter.initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.getParameters(props);

            UserAccessV2 userAccess = null;

            try
            {
                Object userObject = RemoteConnectionFactory.find().find_initial_object("http://" + testParm.host + ":" + testParm.port, "/UserAccessV2.ior");

                userAccess = UserAccessV2Helper.narrow((org.omg.CORBA.Object) userObject);
            } catch (Throwable e)
            {
                e.printStackTrace();
            }

            CASTestLogin[] testLogins = new CASTestLogin[testParm.userNames.size()];

            for (int i = 0; i < testParm.userNames.size(); i++)
            {
                String userName = (String) testParm.userNames.get(i);
                 testLogins[i] = new CASTestLogin(testParm, userName, userAccess);
            }

            executionMode();

            for (int t = 0; t < testParm.userNames.size(); t++)
            {
                testLogins[t].start();
                System.out.println("Thread " + t + " is started");
            }
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    public static void createProductFile(String iniFile)
    {
        try
        {
            Properties props = initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.getParameters(props);
            System.out.println("Using CAS at " + testParm.host + ":" + testParm.port);
            SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(0).toString(), (String)testParm.userNames.get(0),
                     testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
            UserSessionManager userSessionManager = sessionManagerStructV2.sessionManager;
            System.out.println("user name = " + (String)testParm.userNames.get(0));

            PrintWriter writer =  new PrintWriter(new FileWriter(testParm.productKeyFile));
//            PrintWriter Vivek_Writer_01 =  new PrintWriter(new FileWriter("V_ONE_MAIN_01.txt"));
//            PrintWriter Vivek_Writer_02 =  new PrintWriter(new FileWriter("V_ONE_MAIN_02.txt"));

            com.cboe.application.cas.TestCallback callback = new com.cboe.application.cas.TestCallback();

            TradingSessionStruct[] sessions = userSessionManager.getTradingSession().getCurrentTradingSessions(null);
            for (int i = 0; i < sessions.length; i++)
            {
                System.out.println("Session="+sessions[i].sessionName);

                ClassStatusConsumerDelegate classStatus = new ClassStatusConsumerDelegate(callback);
                ProductStatusConsumerDelegate productStatus = new ProductStatusConsumerDelegate(callback);

                org.omg.CORBA.Object orbObject =
                        (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(classStatus);
                com.cboe.idl.cmiCallback.CMIClassStatusConsumer cmiObject = CMIClassStatusConsumerHelper.narrow(orbObject);

                org.omg.CORBA.Object prodObject =
                        (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(productStatus);
                com.cboe.idl.cmiCallback.CMIProductStatusConsumer productObject = CMIProductStatusConsumerHelper.narrow(prodObject);

                SessionClassStruct classes[] = userSessionManager.getTradingSession().
                        getClassesForSession(sessions[i].sessionName, testParm.productType, cmiObject);
                System.out.println("ProductType input for server is: " + testParm.productType + " ************");
                for (int j = 0; j < classes.length; j++)
                {
                    System.out.println("Classes="+classes[j].classStruct.classSymbol + ", classKey=" + classes[j].classStruct.classKey);
                    SessionProductStruct products[] = userSessionManager.getTradingSession().getProductsForSession(sessions[i].sessionName,
                        classes[j].classStruct.classKey, productObject);
                    for (int k = 0; k < products.length; k++)
                    {
                        writer.println(classes[j].classStruct.classKey + "," + classes[j].classStruct.classSymbol + ","
                                + products[k].productStruct.productKeys.productKey + "," + sessions[i].sessionName);

/*                        Vivek_Writer_01.println("Product" + products[k].productStruct.productKeys.productKey + "=55="
                                + products[k].productStruct.productName.reportingClass + "_22=8_48="
                                + products[k].productStruct.productKeys.productKey + "_167=FUT");

                        Vivek_Writer_02.println("Class" + classes[j].classStruct.classKey + "=55="
                                + classes[j].classStruct.classSymbol + "_167=FUT");
*/
                    }
                }
            }
//            Vivek_Writer_01.close();
//            Vivek_Writer_02.close();
            writer.close();
//            SessionProductStruct[] products = userSessionManager.getTradingSession().getProductsForSession("W_AM1", 9, null);
        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    // This gets only HYBRID OPTION
    public static void createHybridOptonFileByTS(String iniFile)
    {
        try
        {
            Properties props = initializeProperties(iniFile);
            TestParameter testParm = TestParameter.getInstance();
            testParm.getParameters(props);
            System.out.println("Using CAS at " + testParm.host + ":" + testParm.port);
            SessionManagerStructV2 sessionManagerStructV2 = CASLogon.logonToCAS(testParm.userNames.get(0).toString(), (String)testParm.userNames.get(0),
                     testParm.host, testParm.port, testParm.mode, testParm.gmdText, testParm.sessionMode);
            System.out.println("user name = " + (String)testParm.userNames.get(0));

            CASTestProdFileBuilder prdBuilder = new CASTestProdFileBuilder(TestParameter.getInstance(), sessionManagerStructV2);

            prdBuilder.createHybridOptionFile();

        }
        catch (Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace() ;
            return ;
        }
    }

    public static void testMarketDataLoginV4(String iniFile)
    {
        Properties props = TestParameter.initializeProperties(iniFile);
        TestParameter testParm = TestParameter.getInstance();
        testParm.getParameters(props);
        v4TestLogins = new CASTestMDV4Login[testParm.userNames.size()];
        CountDownLatch latch = new CountDownLatch(v4TestLogins.length);
        try
        {
            for(int i = 0; i < testParm.userNames.size(); i++)
            {
                String userName = (String) testParm.userNames.get(i);
                v4TestLogins[i] = new CASTestMDV4Login(testParm, userName, testParm.host, testParm.port, testParm.mode,
                                                       testParm.gmdText, testParm.sessionMode, testParm.queueAction, i);
            }

            // wait for user to hit Enter to begin tests
            executionMode();

            //subscribe all users for market data
            for(int t = 0; t < testParm.userNames.size(); t++)
            {
                v4TestLogins[t].subscribe(latch);
            }

            //wait for all subscriptions to complete before starting to dump the results
            System.out.println("Waiting for all subscriptions to complete...");
            latch.await();
            System.out.println("...all subscriptions completed.");
        }
        catch(Exception e)
        {
            System.out.println("error in main");
            e.printStackTrace();
            return;
        }

        while(!testParm.threadDone)
        {
            try
            {
                Thread.sleep(60000);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            MDCollector.dumpCount();
        }
    }

    private static void unsubV4MarketData()
    {
        //todo: add a generic interface with subscribe()/unsubscribe() (e.g., "CASSubscription")
        //      that can be implemented by CASTestMDV4Login (and others), so we can maintain a
        //      collection of all subscriptions; when the main thread is interrupted everything
        //      can unsubscribe
        if(v4TestLogins != null && v4TestLogins.length > 0)
        {
            CountDownLatch unsubLatch = new CountDownLatch(v4TestLogins.length);
            for(int i = 0; i < v4TestLogins.length; i++)
            {
                v4TestLogins[i].unsubscribe(unsubLatch);
            }
            try
            {
                System.out.println("Waiting for all V4 Market Data unsubscriptions to complete...");
                //wait for all unsubscriptions to complete
                unsubLatch.await();
                System.out.println("... all unsubscriptions completed.");
            }
            catch(InterruptedException ie)
            {}
        }
    }

    private static void unsubscribeCASConsumers()
    {
        unsubV4MarketData();
    }

    public static void main(String[] args)
    {
        
        RemoteConnectionFactory.create(args);
        if (args.length < 2)
        {
            
            System.out.println("Usage: command -OPTION inifile");
            System.out.println("where OPTION is one of the following: ");
            System.out.println("-A : Orders & Quotes");
            System.out.println("-B : Block Quote");
            System.out.println("-BQ3 : Block Quote V3");
            System.out.println("-BQC1 : Block Quote followed by Quote Cancel");
            System.out.println("-BQC2 : Block Quote with a mix of Quote & QuoteCancels");
            System.out.println("-Q : Single Quote");
            System.out.println("-QC : Quote Cancel");
            System.out.println("-QRM : QRM");
            System.out.println("-QQ : Quote Queue");
            System.out.println("-QM : Block Quotes with Multithread");
            System.out.println("-QL : Smart Quote Lock");
            System.out.println("-c : Cancel Request");
            System.out.println("-C : Create Product File");
            System.out.println("-CH : Create Hybrid Opton File by TS");
            System.out.println("-D : DN");
            System.out.println("-L : MD Login");
            System.out.println("-UL : User Login");          
            System.out.println("-O : Order");
            System.out.println("-OC : Order Cancel");            
            System.out.println("-R : Order Cancel Replace");            
            System.out.println("-M/-MD3/-ML/-MLV4/-OS/-R/-S/-T/-V");
            System.out.println("     :  [-f] start up when a command file Startup is created.");
            System.exit(1);
        }

        SignalHandler aHandler = new SignalHandler() {
            public void handle(Signal sig) {
                TestParameter.getInstance().ctrlcCount++;
                if (TestParameter.getInstance().threadDone)
                {
                    System.out.println("Force shutdown");
                    RateLogger.closeRateLogger();
                    //if there are subscriptions for V4 MarketData this will unsubscribe
                    unsubscribeCASConsumers();
                    System.exit(1);
                }
                else if (TestParameter.getInstance().ctrlcCount > 1)
                {
                    System.out.println("Caught termination signal.  Shutting down test agent");
                    TestParameter.getInstance().threadDone = true;
                    //if there are subscriptions for V4 MarketData this will unsubscribe
                    unsubscribeCASConsumers();
                    RateLogger.closeRateLogger();
                }
            }
        } ;
        Signal.handle(new Signal("INT"), aHandler);
        Signal.handle(new Signal("TERM"), aHandler);

        if (args.length > 2 && args[2].equals("-f"))
        {
            System.out.println("set running mode to wait for Startup file, " + args[2] + " args.length = " + args.length);
            ShutdownWatcher myWatcher = new ShutdownWatcher (getInstance());
            myWatcher.start();
            setFileWatcherOption(true);
        }

        if (args[0].equals("-Q"))
        {
            testQuotes(args[1]);
        }
        if (args[0].equals("-O"))
        {
            testOrders(args[1]);
        }
        if (args[0].equals("-BQ3"))
        {
            testBlockQuotesV3(args[1]);
        }
        if (args[0].equals("-BQC1"))
        {
            testBlockQuoteAndCancel(args[1]);
        }
        if (args[0].equals("-BQC2"))
        {
            testBlockQuoteCancel(args[1]);
        }
        if (args[0].equals("-B"))
        {
            testBlockQuotes(args[1]);
        }
        if (args[0].equals("-A"))
        {
            testQuotes(args[1]);
            testOrders(args[1]);
        }
        if (args[0].equals("-C"))
        {
            createProductFile(args[1]);
        }
        if (args[0].equals("-M"))
        {
            testMarketData(args[1]);
        }
        if (args[0].equals("-R"))
        {
            testCancelReplace(args[1]);
        }
        if (args[0].equals("-c"))
        {
            testCancel(args[1]);
        }
        if (args[0].equals("-D"))
        {
            testDN(args[1]);
        }
        if (args[0].equals("-T"))
        {
            recordMarketData(args[1]);
        }
        if (args[0].equals("-V"))
        {
            testInvalidOrderIDs(args[1]);
        }
        if (args[0].equals("-QC"))
        {
            testQuoteCancel(args[1]);
        }
        if (args[0].equals("-S"))
        {
            seedQuotes(args[1]);
        }
        if (args[0].equals("-QQ"))
        {
            testQuoteQueue(args[1]);
        }
        if (args[0].equals("-L"))
        {
            testMDLogin(args[1]);
        }
        if (args[0].equals("-CH"))
        {
            createHybridOptonFileByTS(args[1]);
        }
        if (args[0].equals("-UL"))
        {
            testUserLogin(args[1]);
        }
        if (args[0].equals("-QRM"))
        {
            testQRM(args[1]);
        }
        if (args[0].equals("-QM"))
        {
            testBlockQuotesMultiThreaded(args[1]);
        }
        if (args[0].equals("-QL"))
        {
            testSmartQuoteLock(args[1]);
        }
        if (args[0].equals("-MD3"))
        {
            testMarketDataV3(args[1]);
        }
        if (args[0].equals("-OS"))
        {
            testSeedOrders(args[1]);
        }
        if (args[0].equals("-ML"))
        {
            testMarketDataLogin(args[1]);
        }
        if(args[0].equals("-MLV4"))
        {
            testMarketDataLoginV4(args[1]);
        }
    }

    private static void setFileWatcherOption(boolean b) {
        fileWatcherOption = b;
        System.out.println("set running mode to file control.");
    }
}
