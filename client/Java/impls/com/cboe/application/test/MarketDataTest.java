package com.cboe.application.test;

import com.cboe.idl.cmi.ProductQuery;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;
import com.cboe.idl.cmiMarketData.TickerStructV4;
import com.cboe.idl.cmiMarketData.RecapStructV4;
import com.cboe.idl.cmiMarketData.LastSaleStructV4;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiSession.TradingSessionStateStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.UserAccessV2Helper;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserAccessV3Helper;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV4.MarketQuery;
import com.cboe.idl.cmiV4.UserAccessV4;
import com.cboe.idl.cmiV4.UserAccessV4Helper;
import com.cboe.idl.cmiV4.UserSessionManagerV4;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumerPOA;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumerPOA;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright 1999-2009 by the Chicago Board Options Exchange ("CBOE"), as an
 * unpublished work. The information contained in this software program
 * constitutes confidential and/or trade secret information belonging to CBOE.
 * <p/>
 * MarketDataTest.java is intented to be used for testing market data subscription and availability of actual market data.
 * This application can be used during production hours for testing purpose without noticable impact on the system or other applications.
 *
 * @author Piyush Patel
 */
public class MarketDataTest
{
    static ORB orb;
    static POA rootPOA;

    static final int EXIT_SUCCESS = 0;
    static final int EXIT_ERROR = 1;

    UserLogonStruct userLogonStruct;
    UserSessionManager userSessionManager;
    SessionManagerStructV2 userSessionManagerStructV2;

    // Properties for tailoring CAS specification
    private static final String PROP_CAS_IP = "Test.CasIp";
    private static final String PROP_CAS_PORT = "Test.CasPort";
    private static final String PROP_LOGIN_MODE = "Test.LoginMode";
    private static final String PROP_USER_ID = "Test.UserId";
    private static final String PROP_USER_PASSWORD = "Test.UserPassword";
    private static final String PROP_TRADING_SESSION = "Test.TradingSession";
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
    private static final String IP_LOCALHOST = "127.0.0.1";
    private static final int CAS_LOCATOR_PORT = 8003;
    private static final String IOR_REFERENCENAME_V4 = "/UserAccessV4.ior";
    private static final String IOR_REFERENCENAME_V3 = "/UserAccessV3.ior";
    private static final String IOR_REFERENCENAME_V2 = "/UserAccessV2.ior";
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
    private static int executionDuration;
    private static boolean subScribeRecap;
    private static boolean subScribeTicker;
    private static String logDir;
    private static String MARKET_DATA_LOG="marketdata";
    private static String TICKER_LOG ="ticker";
    private static String RECAP_LOG ="recap";
    private BufferedWriter mlogger = null;
    private BufferedWriter rlogger = null;
    private BufferedWriter tlogger = null;
    protected final static String EOL = "\n";
    protected static String DELIM = "\n";
    final static SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss:S yyyy/MM/dd");
    private Calendar nowCal = new GregorianCalendar();
    private Calendar midNight = new GregorianCalendar(nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH), nowCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    private Map<Integer, String> cStructs = new HashMap();
    private Set<Integer> pKeys = new HashSet();

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
            logBoth("CAS sent message. messageKey:" + message.messageKey + " timeStamp:" + message.timeStamp + " sender:" + message.sender + " replyRequested:" + message.replyRequested + " messageText:" + message.messageText,MARKET_DATA_LOG);
            logBoth("CAS sent message. messageKey:" + message.messageKey + " timeStamp:" + message.timeStamp + " sender:" + message.sender + " replyRequested:" + message.replyRequested + " messageText:" + message.messageText,RECAP_LOG);
            logBoth("CAS sent message. messageKey:" + message.messageKey + " timeStamp:" + message.timeStamp + " sender:" + message.sender + " replyRequested:" + message.replyRequested + " messageText:" + message.messageText,TICKER_LOG);
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
    public class TradingSessionStatusConsumerCallback extends com.cboe.idl.cmiCallback.CMITradingSessionStatusConsumerPOA
    {
        public void acceptTradingSessionState(TradingSessionStateStruct state)
        {
            // do nothing
        }
    };



    /**
     * V4 Callback for mdx test
     */
    public class CurrentMarketConsumerV4Callback extends com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumerPOA
    {
        public void acceptCurrentMarket(CurrentMarketStructV4[] bestMarket, CurrentMarketStructV4[] bestPublicMarket, int messageSequence, int queueDepth, short queueAction)
        {
            synchronized(this)
            {
                if(DELIM.equals(EOL))
                {
                    print(bestMarket, bestPublicMarket,  messageSequence, queueDepth,queueAction);
                }
                else
                {
                    cprint(bestMarket, bestPublicMarket,  messageSequence, queueDepth,queueAction);
                }
            }
        }

        void  print(CurrentMarketStructV4[] bestMarket, CurrentMarketStructV4[] bestPublicMarket, int messageSequence, int queueDepth, short queueAction)
        {
            StringBuffer buff = new StringBuffer();
            buff.append("################# Start acceptCurrentMarket #################" + EOL);
            buff.append("messageSequence\t= " + messageSequence + EOL);
            buff.append("queueDepth\t\t= " + queueDepth + EOL);
            buff.append("queueAction\t\t= " +queueAction + EOL);
            buff.append(EOL);
            buff.append("Start Current Market..." + EOL);
            buff.append(EOL);
            printMarketData(bestMarket, buff);
            buff.append("End Current Market..." + EOL);
            buff.append(EOL);
            buff.append("Start Best Public Market..." + EOL);
            buff.append(EOL);
            printMarketData(bestPublicMarket, buff);
            buff.append("End Best Public Market..." + EOL);
            buff.append("################## End acceptCurrentMarket ##################" + EOL);
            buff.append(EOL);
            log(buff.toString(),MARKET_DATA_LOG);

        }
        void printMarketData(CurrentMarketStructV4[] currentMarkets, StringBuffer buff)
        {
            buff.append("Quotes in block = " + currentMarkets.length + EOL);
            for (int i = 0; i < currentMarkets.length; i++)
            {
                if(productKeys!=null && !pKeys.contains(currentMarkets[i].productKey))
                {
                    continue;
                }

                buff.append("  Start Quote..." + i + EOL);

                long sentTime = currentMarkets[i].sentTime + midNight.getTime().getTime();
                Date sentDate = new Date(sentTime);
                buff.append("\tsentTime\t\t= " + DF.format(sentDate) + EOL);
                buff.append("\tcurentTime\t\t= " + DF.format(new Date()) + EOL);
                buff.append("\tclassSymbol\t\t= " + cStructs.get(currentMarkets[i].classKey));
                buff.append(" (classKey= " + currentMarkets[i].classKey + ")" + EOL);
                buff.append("\tproductKey\t\t= " + currentMarkets[i].productKey + EOL);
                buff.append("\tproductType\t\t= " + currentMarkets[i].productType + EOL);
                buff.append("\texchange\t\t= " + currentMarkets[i].exchange + EOL);
                buff.append("\tcurrentMarketType\t= " + currentMarkets[i].currentMarketType + EOL);
                double bidPrice = currentMarkets[i].bidPrice < 0 ? 0 : currentMarkets[i].bidPrice;
                buff.append("\tbidPrice\t\t= " + bidPrice / 100 + EOL);
                buff.append("\tbidTickDirection\t= " + currentMarkets[i].bidTickDirection + EOL);
                double askPrice = currentMarkets[i].askPrice < 0 ? 0 : currentMarkets[i].askPrice;
                buff.append("\taskPrice\t\t= " + askPrice / 100 + EOL);
                buff.append("\tmarketIndicator\t\t= " + currentMarkets[i].marketIndicator + EOL);
                buff.append("\tproductState\t\t= " + currentMarkets[i].productState + EOL);
                buff.append("\tpriceScale\t\t= " + currentMarkets[i].priceScale + EOL);
                buff.append(EOL);
                buff.append("  Bid Volume size:" + currentMarkets[i].bidSizeSequence.length + EOL);
                printMarketVolumeSequence(currentMarkets[i].bidSizeSequence, buff);
                buff.append("  Ask Volume size:" + currentMarkets[i].askSizeSequence.length + EOL);
                printMarketVolumeSequence(currentMarkets[i].askSizeSequence, buff);
                buff.append("  End Quote..." + i + EOL);
                buff.append(EOL);
            }

        }
        void printMarketVolumeSequence(MarketVolumeStructV4[] volumeStructs, StringBuffer buff)
        {
            for (int i = 0; i < volumeStructs.length; i++)
            {
                buff.append("\tvolumeType\t\t= " + volumeStructs[i].volumeType);
                buff.append("\tquantity\t= " + volumeStructs[i].quantity);
                buff.append("\tmultipleParties\t= " + volumeStructs[i].multipleParties);
                buff.append(EOL);
            }
        }


        void  cprint(CurrentMarketStructV4[] bestMarket, CurrentMarketStructV4[] bestPublicMarket, int messageSequence, int queueDepth, short queueAction)
        {
            StringBuffer buff = new StringBuffer();
            buff.append("MS=" + messageSequence+ DELIM);
            buff.append("QD=" + queueDepth + DELIM);
            buff.append("QA=" +queueAction+ DELIM);

            buff.append("\tSCM" );
            buff.append(" { ");
            cprintMarketData(bestMarket, buff);
            buff.append(" } ");

            buff.append("\tBPM");
            buff.append(" { ");
            cprintMarketData(bestPublicMarket, buff);
            buff.append(" } ");
            buff.append(EOL);
            log(buff.toString(),MARKET_DATA_LOG);
        }
        void cprintMarketData(CurrentMarketStructV4[] currentMarkets, StringBuffer buff)
        {
            buff.append("BL=" + currentMarkets.length + DELIM);
            for (int i = 0; i < currentMarkets.length; i++)
            {
                if(productKeys!=null && !pKeys.contains(currentMarkets[i].productKey))
                {
                    continue;
                }
                buff.append("\tN" + i + "{");
                long sentTime = currentMarkets[i].sentTime + midNight.getTime().getTime();
                Date sentDate = new Date(sentTime);
                buff.append("ST=" + DF.format(sentDate) + DELIM);
                buff.append("CT=" + DF.format(new Date()) + DELIM);
                buff.append("CS=" + cStructs.get(currentMarkets[i].classKey) + DELIM);
                buff.append("CK=" + currentMarkets[i].classKey + DELIM);
                buff.append("PK=" + currentMarkets[i].productKey + DELIM);
                buff.append("PT=" + currentMarkets[i].productType + DELIM);
                buff.append("EX=" + currentMarkets[i].exchange + DELIM);
                buff.append("MT=" + currentMarkets[i].currentMarketType + DELIM);
                double bidPrice = currentMarkets[i].bidPrice < 0 ? 0 : currentMarkets[i].bidPrice;
                buff.append("BP=" + bidPrice / 100 + DELIM);
                buff.append("BD=" + currentMarkets[i].bidTickDirection + DELIM);
                double askPrice = currentMarkets[i].askPrice < 0 ? 0 : currentMarkets[i].askPrice;
                buff.append("AP=" + askPrice / 100 + DELIM);
                buff.append("MI=" + currentMarkets[i].marketIndicator + DELIM);
                buff.append("PS=" + currentMarkets[i].productState + DELIM);
                buff.append("SC=" + currentMarkets[i].priceScale + DELIM);
                buff.append("\tBS=" + currentMarkets[i].bidSizeSequence.length + DELIM);
                cprintMarketVolumeSequence(currentMarkets[i].bidSizeSequence, buff);
                buff.append("\tAS=" + currentMarkets[i].askSizeSequence.length + DELIM);
                cprintMarketVolumeSequence(currentMarkets[i].askSizeSequence, buff);
                buff.append("}" + DELIM);
            }

        }


        void cprintMarketVolumeSequence(MarketVolumeStructV4[] volumeStructs, StringBuffer buff)
        {
            for (int i = 0; i < volumeStructs.length; i++)
            {
                buff.append("{");
                buff.append("VT=" + volumeStructs[i].volumeType+ DELIM);
                buff.append("QTY=" + volumeStructs[i].quantity+ DELIM);
                buff.append("MP=" + volumeStructs[i].multipleParties+ DELIM);
                buff.append("}");
            }
        }

    }

    /**
     * Ticker callback for Market query V4
     */


    public class TickerConsumerV4Callback extends CMITickerConsumerPOA
    {
        public void acceptTicker(TickerStructV4[] tickerStructs, int messageSequence,
                                 int queueDepth, short queueAction)
        {

            StringBuffer message = new StringBuffer("");
            message.append("MS=" + messageSequence + DELIM);
            message.append("QD=" + queueDepth + DELIM);
            message.append("QA=" + queueAction + DELIM);
            message.append("{");
            printTickers(message, tickerStructs);
            message.append("}");
            message.append(EOL);
            log(message.toString(),TICKER_LOG);
        }

        void printTickers(StringBuffer message, TickerStructV4[] tickers)
        {
            message.append("BL=" + tickers.length + DELIM);
            for (int i=0 ; i < tickers.length ; i++)
            {
                TickerStructV4 ticker = tickers[i];
                long sentTime = ticker.sentTime + midNight.getTime().getTime();
                Date sentDate = new Date(sentTime);
                message.append("N"+ i + "{");
                message.append("ST=" + DF.format(sentDate) + DELIM);
                message.append("CT=" + DF.format(new Date()) + DELIM);
                message.append("CK=" + ticker.classKey + DELIM);
                message.append("PK=" + ticker.productKey + DELIM);
                message.append("PT=" + ticker.productType + DELIM);
                message.append("EX=" + ticker.exchange + DELIM);
                message.append("SC=" + ticker.priceScale + DELIM);
                message.append("TRADE-TIME=" + ticker.tradeTime + DELIM);
                message.append("TRADE-PRICE=" + ticker.tradePrice + DELIM);
                message.append("TRACE-VOLUME=" + ticker.tradeVolume + DELIM);
                message.append("SALE-PREFIX=" + ticker.salePrefix + DELIM);
                message.append("SALE-POSTFIX=" + ticker.salePostfix + DELIM);
                message.append("}" );
            }
        }

    }


    /**
     * Recap callback for market query V$
     */

    public class RecapConsumerV4Callback extends CMIRecapConsumerPOA
    {


        public void acceptRecap(RecapStructV4[] recapStructs, int messageSequence,
                                int queueDepth, short queueAction)
        {


            StringBuffer message = new StringBuffer("RECAP START {" + DELIM);
            message.append("MS=" + messageSequence + DELIM);
            message.append("QD=" + queueDepth + DELIM);
            message.append("QA=" + queueAction + DELIM);
            message.append("{");
            printRecaps(message, recapStructs);
            message.append("}"+ EOL);
            log(message.toString(),RECAP_LOG);


        }

        public void acceptLastSale(LastSaleStructV4[] lastSaleStructs, int messageSequence,
                                   int queueDepth, short queueAction)
        {


            StringBuffer message = new StringBuffer("");
            message.append("MS=" + messageSequence + DELIM);
            message.append("QD=" + queueDepth + DELIM);
            message.append("QA=" + queueAction + DELIM);
            message.append("{");
            printLastSales(message, lastSaleStructs);
            message.append("}"+ EOL);
            log(message.toString(),RECAP_LOG);
        }

        void printRecaps(StringBuffer message, RecapStructV4[] recaps)
        {
            message.append("RB= " + recaps.length + DELIM);
            for (int i=0 ; i < recaps.length ; i++)
            {
                RecapStructV4 recap = recaps[i];
                message.append("N"+ i + "{");
                long sentTime = recap.sentTime + midNight.getTime().getTime();
                Date sentDate = new Date(sentTime);
                message.append("ST=" + DF.format(sentDate) + DELIM);
                message.append("CT=" + DF.format(new Date()) + DELIM);
                message.append("CK=" + recap.classKey + DELIM);
                message.append("PK=" + recap.productKey + DELIM);
                message.append("PT=" + recap.productType + DELIM);
                message.append("EX=" + recap.exchange + DELIM);
                message.append("SC=" + recap.priceScale + DELIM);
                message.append("LOW-PRICE=" + recap.lowPrice + DELIM);
                message.append("HIGH-PRICE=" + recap.highPrice + DELIM);
                message.append("OPEN-PRICE= " + recap.openPrice + DELIM);
                message.append("CLOSE-PRICE= " + recap.previousClosePrice + DELIM);
                message.append("STATUSCODES=" + recap.statusCodes);
                message.append("}");
            }
        }

        void printLastSales(StringBuffer message, LastSaleStructV4[] lastSales)
        {
            message.append("BL=" + lastSales.length + DELIM);
            for (int i=0 ; i < lastSales.length ; i++)
            {
                LastSaleStructV4 lastSale =  lastSales[i];
                long sentTime = lastSale.sentTime + midNight.getTime().getTime();
                Date sentDate = new Date(sentTime);
                message.append("N" + i+ DELIM);
                message.append("ST=" + DF.format(sentDate) + DELIM);
                message.append("CT=" + DF.format(new Date()) + DELIM);
                message.append("CK=" + lastSale.classKey + DELIM);
                message.append("PK=" + lastSale.productKey + DELIM);
                message.append("PT=" + lastSale.productType + DELIM);
                message.append("EX=" + lastSale.exchange + DELIM);
                message.append("PS=" + lastSale.priceScale + DELIM);
                message.append("LS-TIME=" + lastSale.lastSaleTime + DELIM);
                message.append("LS-PRICE=" + lastSale.lastSalePrice + DELIM);
                message.append("LS-VOLUME=" + lastSale.lastSaleVolume + DELIM);
                message.append("TOTAL-VOLUME=" + lastSale.totalVolume + DELIM);
                message.append("TICKER-DIRECTION=" + lastSale.tickDirection + DELIM);
                message.append("NET-PR-CHANGE=" + lastSale.netPriceChange + DELIM);
                message.append("}");
            }
        }
    }








    /**
     * Prints market data for market query v2 and v3.
     *
     * @param currentMarket
     */
    private synchronized void printCurrentMarketStructV2V3(CurrentMarketStruct[] currentMarket,StringBuffer buf)
    {
        for (int i = 0; i < currentMarket.length; i++)
        {
            CurrentMarketStruct cm = currentMarket[i];
            buf.append("\tN" + i + "{");
            buf.append("CK=" + cm.productKeys.classKey + DELIM);
            buf.append("PK=" + cm.productKeys.productKey + DELIM);

            buf.append("BP=" + cm.bidPrice.whole + "." + cm.bidPrice.fraction + DELIM);
            buf.append("BS=" + cm.bidSizeSequence.length + DELIM);
            for (int j = 0; j < cm.bidSizeSequence.length; j++)
            {
                MarketVolumeStruct mv = cm.bidSizeSequence[j];
                buf.append(" { ");
                buf.append("QTY=" + mv.quantity);
                buf.append("VT="  + mv.volumeType);
                buf.append("MP="  + mv.multipleParties);
                buf.append(" } ");
            }

            buf.append("AP=" + cm.askPrice.whole + "." + cm.askPrice.fraction + DELIM);
            buf.append("AS=" + cm.askSizeSequence.length+ DELIM);
            for (int j = 0; j < cm.askSizeSequence.length; j++)
            {
                MarketVolumeStruct mv = cm.askSizeSequence[j];
                buf.append(" { ");
                buf.append("QTY=" + mv.quantity);
                buf.append("VT="  + mv.volumeType);
                buf.append("MP="  + mv.multipleParties);
                buf.append(" } ");
            }
            buf.append("LM=" + cm.legalMarket);
            buf.append("}");
        }

    }

    /**
     * Current Market Callback for subscribe by product and for market query v2.
     */
    public class CurrentMarketConsumerCallback extends com.cboe.idl.cmiCallback.CMICurrentMarketConsumerPOA
    {
        public void acceptCurrentMarket(CurrentMarketStruct[] currentMarket)
        {
            StringBuffer buff = new StringBuffer();
            printCurrentMarketStructV2V3(currentMarket,buff);
            buff.append(EOL);
            log(buff.toString());
        }
    }

    ;

    /**
     * Current Market v3 callback.
     */
    public class CurrentMarketConsumerCallbackV3 extends com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerPOA
    {


        public void acceptCurrentMarket(CurrentMarketStruct[] bestMarket, CurrentMarketStruct[] bestPublicMarket, int queueDepth, short queueAction)
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("QD=" + queueDepth + DELIM);
            buffer.append("QA=" + queueAction + DELIM);
            buffer.append("SCM"+DELIM);
            buffer.append(" { ");
            printCurrentMarketStructV2V3(bestMarket,buffer);
            buffer.append(" } ");
            buffer.append("BPM"+ DELIM);
            buffer.append(" { ");
            printCurrentMarketStructV2V3(bestPublicMarket,buffer);
            buffer.append(" } ");
            buffer.append(EOL);
            log(buffer.toString());
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
    void loginV2()
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

        UserAccessV2 userAccessV2 = UserAccessV2Helper.narrow(orb.string_to_object(userAccessIOR));


        userLogonStruct = new UserLogonStruct();
        userLogonStruct.userId = userId;
        userLogonStruct.password = userId;
        userLogonStruct.version = Version.CMI_VERSION;
        userLogonStruct.loginMode = sessionMode;

        try
        {
            userSessionManagerStructV2 = userAccessV2.logon(userLogonStruct, SESSION_TYPE, userSessionAdminCallback._this(), GMD_MESSAGING);
        }
        catch (Exception e)
        {
            fail(e);
        }
    }

    /**
     * login to cas for user access v3
     */
    void loginV3()
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

        String userAccessIOR = null;
        try
        {
            URL url = new URL("http", hostIp, locatorPort, IOR_REFERENCENAME_V3);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader((InputStream) connection.getContent()));
            userAccessIOR = in.readLine();
        }
        catch (Exception e)
        {
            fail(e);
        }

        UserAccessV3 userAccessV3 = UserAccessV3Helper.narrow(orb.string_to_object(userAccessIOR));


        userLogonStruct = new UserLogonStruct();
        userLogonStruct.userId = userId;
        userLogonStruct.password = userId;
        userLogonStruct.version = Version.CMI_VERSION;
        userLogonStruct.loginMode = sessionMode;

        try
        {
            userSessionManager = userAccessV3.logon(userLogonStruct, SESSION_TYPE, userSessionAdminCallback._this(), GMD_MESSAGING);
        }
        catch (Exception e)
        {
            fail(e);
        }
    }

    /**
     * login to cas for user access v4
     */
    void loginV4()
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

        String userAccessIOR = null;
        try
        {
            URL url = new URL("http", hostIp, locatorPort, IOR_REFERENCENAME_V4);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader((InputStream) connection.getContent()));
            userAccessIOR = in.readLine();
        }
        catch (Exception e)
        {
            fail(e);
        }

        UserAccessV4 userAccessV4 = UserAccessV4Helper.narrow(orb.string_to_object(userAccessIOR));


        userLogonStruct = new UserLogonStruct();
        userLogonStruct.userId = userId;
        userLogonStruct.password = userId;
        userLogonStruct.version = Version.CMI_VERSION;
        userLogonStruct.loginMode = sessionMode;

        try
        {
            userSessionManager = userAccessV4.logon(userLogonStruct, SESSION_TYPE, userSessionAdminCallback._this(), GMD_MESSAGING);
        }
        catch (Exception e)
        {
            fail(e);
        }
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
            if (userSessionManagerStructV2 != null)
            {
                userSessionManagerStructV2.sessionManager.logout();
            }
        }
        catch (Exception e)
        {
            fail(e);
        }
    }

    /**
     * validates the session for market query subscribe.
     */
    private void getSessions()
    {
        TradingSessionStatusConsumerCallback tradingSessionStatusConsumerCallback = new TradingSessionStatusConsumerCallback();
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
            if (userSessionManager != null)
            {
                tradingSessions = userSessionManager.getTradingSession().getCurrentTradingSessions(tradingSessionStatusConsumerCallback._this());
            }
            else
            {
                tradingSessions = userSessionManagerStructV2.sessionManager.getTradingSession().getCurrentTradingSessions(tradingSessionStatusConsumerCallback._this());

            }
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
        fail(new RuntimeException("No such session: " + PROP_TRADING_SESSION + "=" + sessionName));
    }

    /**
     * Searches for the class using product query based on class symbol.
     *
     * @param query ProductQuery to use
     * @return Set<ClassStruct> based comma seperated class symbols.
     */
    public Set<ClassStruct> findClasses(ProductQuery query)
    {

        Set<String> classeSymbols = new HashSet();
        Set<ClassStruct> classes = new HashSet();
        try
        {
            ClassStruct[] classStructs = query.getProductClasses(productType);
            if (classNames != null)
            {
                String[] symbols = classNames.split(",");
                for (int i = 0; i < symbols.length; i++)
                {
                    classeSymbols.add(symbols[i].toUpperCase().trim());
                }


                for (int i = 0; i < classStructs.length; i++)
                {
                    ClassStruct classStruct = classStructs[i];
                    if (classeSymbols.contains(classStruct.classSymbol))
                    {
                        logBoth("Found class\t\t:" + classStruct.classSymbol + "\tclass key: " + classStruct.classKey + EOL);
                        classes.add(classStruct);
                    }
                }
                if (classes.isEmpty())
                {
                    String msg = "One or more class not found. Plese verify Class Symbols:" + classNames.toUpperCase() + EOL;
                    fail(new RuntimeException(msg));
                }
            }
            else
            {
                logBoth("Picking up any two classes randomly." + EOL);
                for (int i = 0; i < 2; i++)
                {
                    ClassStruct cs = classStructs[i];
                    logBoth("Found class\t\t:" + cs.classSymbol + "\tclass key: " + cs.classKey + EOL);
                    classes.add(cs);
                }
            }
        }
        catch (Exception e)
        {
            fail(e);
        }
        return classes;


    }

    /**
     * searches products based on comma seperated product keys.
     *
     * @param query Product Query to use
     * @return Set<ProductStruct> based on comma seperated product keys
     */
    public Set<ProductStruct> findProducts(ProductQuery query)
    {
        Set<ProductStruct> products = new HashSet();
        try
        {
            Set<Integer> keys = new HashSet();
            String[] s = productKeys.split(",");
            for (int i = 0; i < s.length; i++)
            {
                keys.add(new Integer(s[i].trim()));
            }
            for (Integer productKey : keys)
            {
                ProductStruct product = query.getProductByKey(productKey);
                if (product != null)
                {
                    logBoth("Found Product key\t:" + productKeys + EOL);
                    products.add(product);
                }
                else
                {
                    String msg = "Product not found. Product Key:" + productKeys + EOL;
                    fail(new RuntimeException(msg));
                }
            }
        }
        catch (Exception e)
        {
            logBoth("Runtime Exception during product query" + EOL);
            fail(e);
        }
        return products;
    }

    /**
     * Subscribe for market data using market Query v2 based on specified class symbols or product keys.
     *
     * @throws Exception
     */
    void runV2() throws Exception
    {
        com.cboe.idl.cmiV2.MarketQuery queryV2 = userSessionManagerStructV2.sessionManagerV2.getMarketQueryV2();
        if (productKeys != null)
        {
            Set<ProductStruct> products = findProducts(userSessionManagerStructV2.sessionManager.getProductQuery());
            Map<ProductStruct, CurrentMarketConsumerCallback> consumers = new HashMap();
            for (ProductStruct product : products)
            {
                CurrentMarketConsumerCallback currentMarketConsumerCallback = new CurrentMarketConsumerCallback();
                rootPOA.activate_object(currentMarketConsumerCallback);
                consumers.put(product, currentMarketConsumerCallback);
            }
            for (ProductStruct product : consumers.keySet())
            {
                logBoth("Subscribing for product:" + product.productKeys.productKey + EOL);
                queryV2.subscribeCurrentMarketForProduct(sessionName, product.productKeys.productKey, consumers.get(product)._this());
            }
            logBoth(EOL + "..........Subscribing for " + executionDuration + " seconds.........." + EOL + EOL);
            Thread.sleep(executionDuration * 1000);
            for (ProductStruct product : consumers.keySet())
            {
                logBoth("Unsubscribing for product:" + product.productKeys.productKey + EOL);
                queryV2.unsubscribeCurrentMarketForProduct(sessionName, product.productKeys.productKey, consumers.get(product)._this());
            }
        }
        if (classNames != null || productKeys == null)
        {
            Set<ClassStruct> classStructs = findClasses(userSessionManagerStructV2.sessionManager.getProductQuery());
            Map<ClassStruct, CurrentMarketConsumerCallback> consumers = new HashMap();
            for (ClassStruct classStruct : classStructs)
            {
                CurrentMarketConsumerCallback currentMarketConsumerCallback = new CurrentMarketConsumerCallback();
                rootPOA.activate_object(currentMarketConsumerCallback);
                consumers.put(classStruct, currentMarketConsumerCallback);
            }
            for (ClassStruct classStruct : consumers.keySet())
            {
                logBoth("Subscribing for class:" + classStruct.classSymbol + EOL);
                queryV2.subscribeCurrentMarketForClass(sessionName, classStruct.classKey, consumers.get(classStruct)._this());
            }
            logBoth(EOL + "..........Subscribing for " + executionDuration + " seconds.........." + EOL + EOL);
            Thread.sleep(executionDuration * 1000);
            for (ClassStruct classStruct : consumers.keySet())
            {
                logBoth("Unsubscribing for class:" + classStruct.classSymbol + EOL);
                queryV2.unsubscribeCurrentMarketForClass(sessionName, classStruct.classKey, consumers.get(classStruct)._this());
            }
        }

    }

    /**
     * Subscribe for market data using market query v3 based on specified class symbols or product keys.
     *
     * @throws Exception
     */

    void runV3() throws Exception
    {

        com.cboe.idl.cmiV3.MarketQuery queryV3 = ((UserSessionManagerV3) userSessionManager).getMarketQueryV3();
        if (productKeys != null)
        {
            Set<ProductStruct> products = findProducts(userSessionManager.getProductQuery());
            Map<ProductStruct, CurrentMarketConsumerCallback> consumers = new HashMap();
            for (ProductStruct product : products)
            {
                CurrentMarketConsumerCallback currentMarketConsumerCallback = new CurrentMarketConsumerCallback();
                rootPOA.activate_object(currentMarketConsumerCallback);
                consumers.put(product, currentMarketConsumerCallback);
            }
            for (ProductStruct product : consumers.keySet())
            {
                logBoth("Subscribing for product:" + product.productKeys.productKey + EOL);
                queryV3.subscribeCurrentMarketForProduct(sessionName, product.productKeys.productKey, consumers.get(product)._this());
            }
            logBoth(EOL + "..........Subscribing for " + executionDuration + " seconds.........." + EOL + EOL);
            Thread.sleep(executionDuration * 1000);
            for (ProductStruct product : consumers.keySet())
            {
                logBoth("Unsubscribing for product:" + product.productKeys.productKey + EOL);
                queryV3.unsubscribeCurrentMarketForProduct(sessionName, product.productKeys.productKey, consumers.get(product)._this());
            }
        }
        if (classNames != null || productKeys == null)
        {
            Set<ClassStruct> classStructs = findClasses(userSessionManager.getProductQuery());
            Map<ClassStruct, CurrentMarketConsumerCallbackV3> consumers = new HashMap();
            for (ClassStruct classStruct : classStructs)
            {
                CurrentMarketConsumerCallbackV3 currentMarketConsumerCallbackV3 = new CurrentMarketConsumerCallbackV3();
                rootPOA.activate_object(currentMarketConsumerCallbackV3);
                consumers.put(classStruct, currentMarketConsumerCallbackV3);
            }
            for (ClassStruct classStruct : consumers.keySet())
            {
                logBoth("Subscribing for class:" + classStruct.classSymbol + EOL);
                queryV3.subscribeCurrentMarketForClassV3(sessionName, classStruct.classKey, consumers.get(classStruct)._this(), QueueActions.OVERLAY_LAST);
            }

            logBoth(EOL + "..........Subscribing for " + executionDuration + " seconds.........." + EOL + EOL);
            Thread.sleep(executionDuration * 1000);
            for (ClassStruct classStruct : consumers.keySet())
            {
                logBoth("Unsubscribing for class:" + classStruct.classSymbol + EOL);
                queryV3.unsubscribeCurrentMarketForClassV3(sessionName, classStruct.classKey, consumers.get(classStruct)._this());
            }

        }

    }

    /**
     * Subscribe for market data using market query v4 based on specified class symbols.
     *
     * @throws Exception
     */
    void runV4() throws Exception
    {
        UserSessionManagerV4 userSessionManagerV4 = (UserSessionManagerV4) userSessionManager;
        Set<ClassStruct> classStructs = new HashSet();
        final MarketQuery queryV4 = userSessionManagerV4.getMarketQueryV4();
        final Map<ClassStruct, CurrentMarketConsumerV4Callback> marketDataCallbacks = new HashMap();
        final Map<ClassStruct, TickerConsumerV4Callback> tickerCallbacks = new HashMap();
        final Map<ClassStruct, RecapConsumerV4Callback> recapCallbacks = new HashMap();
        if (productKeys != null)
        {

            ProductQuery query = userSessionManager.getProductQuery();
            Set<ProductStruct> products = findProducts(query);

            for(ProductStruct product:products)
            {
                pKeys.add(product.productKeys.productKey);
                ClassStruct cs = query.getClassByKey(product.productKeys.classKey);
                if(cs != null)
                {
                    classStructs.add(cs);
                }
                else
                {
                    String msg = "Class not found for product key" + product.productKeys.productKey + EOL;
                    fail(new RuntimeException(msg));
                }
            }
        }
        if(classNames!=null)
        {
            Set<ClassStruct> cs = findClasses(userSessionManager.getProductQuery());
            classStructs.addAll(cs);

        }
        for (ClassStruct classStruct : classStructs)
        {
            CurrentMarketConsumerV4Callback currentMarketConsumerCallbackV4 = new CurrentMarketConsumerV4Callback();
            rootPOA.activate_object(currentMarketConsumerCallbackV4);
            marketDataCallbacks.put(classStruct, currentMarketConsumerCallbackV4);
            if(subScribeTicker)
            {
                TickerConsumerV4Callback tickerConsumerCallbackV4 = new TickerConsumerV4Callback();
                rootPOA.activate_object(tickerConsumerCallbackV4);
                tickerCallbacks.put(classStruct, tickerConsumerCallbackV4);
            }
            if(subScribeRecap)
            {
                RecapConsumerV4Callback recapConsumerCallbackV4 = new RecapConsumerV4Callback();
                rootPOA.activate_object(recapConsumerCallbackV4);
                recapCallbacks.put(classStruct, recapConsumerCallbackV4);
            }
            cStructs.put(classStruct.classKey,classStruct.classSymbol);

        }
        for (ClassStruct classStruct : marketDataCallbacks.keySet())
        {
            logBoth("Subscribing for class " + classStruct.classSymbol + EOL);
            queryV4.subscribeCurrentMarket(classStruct.classKey, marketDataCallbacks.get(classStruct)._this(), QueueActions.OVERLAY_LAST);
            if(tickerCallbacks.get(classStruct)!=null)
            {
                queryV4.subscribeTicker(classStruct.classKey,  tickerCallbacks.get(classStruct)._this(), QueueActions.OVERLAY_LAST);
            }
            if(recapCallbacks.get(classStruct)!=null)
            {
                queryV4.subscribeRecap(classStruct.classKey, recapCallbacks.get(classStruct)._this(), QueueActions.OVERLAY_LAST);
            }
        }

        Thread.sleep(100);
        logBoth(EOL + "..........Subscribing for " + executionDuration + " seconds.........." + EOL + EOL);
        Thread.sleep(executionDuration * 1000);
     
        for (ClassStruct classStruct : marketDataCallbacks.keySet())
        {
            logBoth("Unsubscribing for class " + classStruct.classSymbol + EOL);
            queryV4.unsubscribeCurrentMarket(classStruct.classKey, marketDataCallbacks.get(classStruct)._this());
        }

    }

    public void run() throws Exception
    {

        if (marketQueryType == 2)
        {
            loginV2();
            getSessions();
            runV2();
        }
        else if (marketQueryType == 3)
        {
            loginV3();
            getSessions();
            runV3();
        }
        else
        {
            loginV4();
            getSessions();
            runV4();
        }
        logout();
        logBoth("Market data test completed."+ EOL);
        System.out.print("Logs are available at " + new File( MARKET_DATA_LOG).getAbsoluteFile() +  EOL);
        System.out.print("Logs are available at " + new File( RECAP_LOG).getAbsoluteFile() + EOL );
        System.out.print("Logs are available at " + new File( TICKER_LOG).getAbsoluteFile() + EOL + EOL);

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

        s = props.getProperty(PROP_LOGIN_MODE);
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

        sessionName = props.getProperty(PROP_TRADING_SESSION);
        sessionName = sessionName == null ? "W_MAIN" : sessionName;
        classNames = props.getProperty(PROP_CLASS_NAME);
        productKeys = props.getProperty(PROP_PRODUCT_KEYS);

        s = props.getProperty(PROP_PRODUCT_TYPE);
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

        userId = props.getProperty(PROP_USER_ID);
        userId = userId == null ? "X01" : userId;
        password = props.getProperty(PROP_USER_PASSWORD);
        password = (password == null) ? userId : password;


        s = props.getProperty(PROP_MARKET_QUERY_TYPE);
        s = s == null ? "4" : String.valueOf(s.charAt(s.length() - 1));
        marketQueryType = Integer.parseInt(s);

        s = props.getProperty(PROP_EXECUTION_DURATION);
        executionDuration = (s == null) ? 15 : Integer.parseInt(s);
        s = props.getProperty(PROP_LOG_FILE_LOCATION);
        logDir = (s == null) ? TEMP_DIR : s;
        SimpleDateFormat f = new SimpleDateFormat("yyMMdd_HHmmss");
        String logExtenstion =  f.format(new Date());
        MARKET_DATA_LOG = logDir + "/"+ MARKET_DATA_LOG + logExtenstion + ".log";
        RECAP_LOG = logDir + "/"+ RECAP_LOG + logExtenstion + ".log";
        TICKER_LOG = logDir + "/"+ TICKER_LOG + logExtenstion + ".log";


        s = props.getProperty(PROP_LOGFILE_FORMAT);
        DELIM  = (s == null) ? "\n" : s.toUpperCase().startsWith("M")?"\n":",";

        s = props.getProperty(PROP_SUBSCRIBE_RECAP);
        subScribeRecap  = (s == null) ? false:s.toUpperCase().equals("Y");

        s = props.getProperty(PROP_SUBSCRIBE_TICKER);
        subScribeTicker  = (s == null) ? false:s.toUpperCase().equals("Y");

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
        msg.append("Log file location\t:" + new File(MARKET_DATA_LOG).getAbsoluteFile() + EOL);
        msg.append("Log file location\t:" + new File(RECAP_LOG).getAbsoluteFile() + EOL);
        msg.append("Log file location\t:" + new File(TICKER_LOG).getAbsoluteFile() + EOL);
        msg.append("Log file delimiter\t:" + (DELIM.equals("\n")?"NEW LINE(\\n)": "COMMA(,)") + EOL);

        if(!DELIM.equals(EOL))
        {
            String notations = "Acronyms Used:\n"
                + "AP  => Ask Price\n"
                + "AS  => Ask Size\n"
                + "BD  => Bid Direction\n"
                + "BP  => Bid Price\n"
                + "BS  => Bid Size\n"
                + "BPM => Start Best Public Market\n"
                + "CK  => Class Key\n"
                + "CS  => Class Symbol\n"
                + "CT  => Current System time\n"
                + "EX  => Exchange\n"
                + "LM  => Legal Market\n"
                + "MI  => Market Indicator\n"
                + "MP  => Multiple Parties\n"
                + "MS  => Message Sequence\n"
                + "MT  => Current Market Type\n"
                + "PK  => Product Key\n"
                + "PS  => Product State\n"
                + "PT  => Product Type\n"
                + "QA  => Queue Action\n"
                + "BL  => Items in Block\n"
                + "QD  => Queue Depth\n"
                + "N+i => Sequence Number i\n"
                + "QTY => Quantity\n"
                + "SC  => Price Scale\n"
                + "SCM => Start Current Market\n"
                + "ST  => Quote sent Time\n"
                + "VT  => Volume Type" ;
            msg.append(notations);
        }
        logBoth(msg.toString());

    }

    public synchronized void log(String s)
    {
        log(s,MARKET_DATA_LOG);

    }

    /**
     * Logs the output to output file
     *
     * @param s String to write
     */
    public synchronized void log(String s, String logFile)
    {
        if(mlogger==null)
        {
            File mFile = new File(MARKET_DATA_LOG);
            File rFile = new File(RECAP_LOG);
            File tFile = new File(TICKER_LOG);
            try
            {
                if (mFile.exists() || mFile.createNewFile())
                {
                    mlogger = new BufferedWriter(new FileWriter(mFile));
                    rlogger = new BufferedWriter(new FileWriter(rFile));
                    tlogger = new BufferedWriter(new FileWriter(tFile));


                }
            }
            catch (IOException e)
            {
                System.out.println("Can not create " + mFile.getAbsoluteFile() + ". Please grant correct write permissions Or configure log file path using property -D" + PROP_LOG_FILE_LOCATION + "=<writable file directory>.");
                System.out.println("Can not create " + rFile.getAbsoluteFile() + ". Please grant correct write permissions Or configure log file path using property -D" + PROP_LOG_FILE_LOCATION + "=<writable file directory>.");
                System.out.println("Can not create " + tFile.getAbsoluteFile() + ". Please grant correct write permissions Or configure log file path using property -D" + PROP_LOG_FILE_LOCATION + "=<writable file directory>.");
                e.printStackTrace();
                System.exit(EXIT_ERROR);
            }

        }


        try
        {
            if(logFile.equals(MARKET_DATA_LOG))
            {
                mlogger.write(s);
                mlogger.flush();
            }
            else if(logFile.equals(RECAP_LOG))
            {
                rlogger.write(s);
                rlogger.flush();

            }
            else if(logFile.equals(TICKER_LOG))
            {
                tlogger.write(s);
                tlogger.flush();
            }

        }
        catch (IOException e)
        {
            if (e.getMessage() != null && e.getMessage().contains("Stream closed"))
            {
                System.out.println("WARNING: All market data has not written to the log file. Subscription time can be increased using -DTest.executionDuration=<time in ms> property." + EOL);
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
        logBoth(s,MARKET_DATA_LOG);
    }
    public  void logBoth(Exception e)
    {
        logBoth(e,MARKET_DATA_LOG);
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

        MarketDataTest tester = new MarketDataTest();
        try
        {
            orb = ORB.init(args, null);
            rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            tester.getArgs();

            tester.run();
        }
        catch (Exception e)
        {
            tester.fail(e);
        }
        if (tester.mlogger != null)
        {
            tester.mlogger.flush();
            tester.mlogger.close();
            tester.rlogger.flush();
            tester.rlogger.close();
            tester.tlogger.flush();
            tester.tlogger.close();
        }
        System.exit(EXIT_SUCCESS);
    } // main


}
