package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.util.*;
import java.io.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.delegates.application.*;
import com.cboe.delegates.callback.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.ReflectiveStructBuilder;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserAccessV2Helper;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.exceptions.*;

public class CASTestMDLogin extends Thread
{
    protected String UserName;
    protected TestParameter testParm = null;
    protected UserAccessV3 userAccess = null;
    protected UserSessionManagerV3 sessionManagerV3 = null;
//    protected UserSessionManagerV2 userSessionManagerV2 = null;
//    protected UserSessionManager userSessionManager = null;
//    protected SessionManagerStructV2 sessionManagerV2;
    protected CASMeter recapMeter = null;
//    protected CASMeter currentMarketMeter = null;
//    protected CASMeter bestBookMeter = null;
//    protected CASMeter tickerMeter = null;
    protected CASMeter underlyingRecapMeter = null;
    protected CASMeter underlyingTickerMeter = null;
//    protected CASMeter NBBOMeter = null;
//    protected CASMeter expectedOpeningPriceMeter = null;
//    protected CASMeter bestMarketMeter = null;
//    protected CASMeter bestPublicMarketMeter = null;
//    protected CASMeter nbboMeter = null;
//    protected CASMeter eopMeter = null;
    protected MarketDataCounter bestbookCounter = null;
    protected MarketDataCounter recapCounter = null;
    protected MarketDataCounter tickerCounter = null;
    protected MarketDataCounter underlyingRecapCounter = null;
    protected MarketDataCounter underlyingTickerCounter = null;
    protected MarketDataCounter NBBOCounter = null;
    protected MarketDataCounter expectedOpeningPriceCounter = null;
    protected MarketDataCounter bestMarketCounter = null;
    protected MarketDataCounter publicMarketCounter = null;

    protected int totalClasses = 0;
    protected int myThreadId = 0;

    public CASTestMDLogin(TestParameter parm, String userID)
    {
        this.UserName = userID;
        this.testParm = parm;
//        this.userAccess = userAccess;
        myThreadId = testParm.userNames.indexOf(UserName);
        System.out.println("Created Login Thread for user = " + UserName + " with assigned thread id of " + myThreadId);
    }

    public  void run()
    {
        try {
            Thread.currentThread().sleep(10000);
        } catch (Exception e)
        {
        }

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

        UserLogonStruct logonStruct = new UserLogonStruct(UserName, UserName, Version.CMI_VERSION, testParm.mode);
        com.cboe.testDrive.CMIUserSessionAdmin userSessionAdminCallback = new com.cboe.testDrive.CMIUserSessionAdmin();
//        org.omg.CORBA.Object userSessionObject =
//                (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(userSessionAdminCallback);
//        com.cboe.idl.cmiCallback.CMIUserSessionAdmin clientListener = CMIUserSessionAdminHelper.narrow(userSessionObject);
        System.out.println("Logging on to CAS as " + UserName + " password " + UserName);

        int failedCount = 0;
        boolean success = false;

        while (failedCount < 3 && !success)
        {
            try
            {
                long startTime = System.currentTimeMillis( );

                sessionManagerV3 = CASLogon.logonToCASV3(UserName, UserName, testParm.host, testParm.port
                        , testParm.mode, testParm.gmdText, testParm.sessionMode);
                success = true;
                long endTime = System.currentTimeMillis( );

                userSessionAdminCallback.setUserSessionManager(sessionManagerV3);
                userSessionAdminCallback.setUserLogonStruct(logonStruct);
                System.out.println(testParm.host + ":" + UserName + ":" + endTime + ":" + startTime + ":" + (endTime-startTime) + ":ms");
            } catch (Exception e)
            {
                failedCount++;
            }
            if (!success)
            {
                try {
                    Thread.currentThread().sleep(500);
                } catch (Exception e)
                {
                }
            }
        }

        if (failedCount >= 3)
        {
            System.out.println(this.UserName + " could not log into CAS : ");
            return;
        }

        System.out.println("Adding filters................");

        try
        {
/*            recapMeter = MDRateMeter.getMDMeter(MDRateMeter.RECAP, classes.size(), myThreadId);
            bestMarketMeter = MDRateMeter.getMDMeter(MDRateMeter.BESTMARKET, classes.size(), myThreadId);
            bestPublicMarketMeter = MDRateMeter.getMDMeter(MDRateMeter.PUBLICMARKET, classes.size(), myThreadId);
            nbboMeter = MDRateMeter.getMDMeter(MDRateMeter.NBBO, classes.size(), myThreadId);
            expectedOpeningPriceMeter = MDRateMeter.getMDMeter(MDRateMeter.EOP, classes.size(), myThreadId);
            underlyingRecapMeter = MDRateMeter.getMDMeter(MDRateMeter.UNDERLYING_RECAP, classes.size(), myThreadId);
            underlyingTickerMeter = MDRateMeter.getMDMeter(MDRateMeter.UNDERLYING_TICKER, classes.size(), myThreadId);
            bestBookMeter = MDRateMeter.getMDMeter(MDRateMeter.BESTBOOK, classes.size(), myThreadId);
            tickerMeter = MDRateMeter.getMDMeter(MDRateMeter.TICKER, classes.size(), myThreadId);
*/
/*            bestbookCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.BESTBOOK, myThreadId);
            recapCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.RECAP, myThreadId);
            tickerCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.TICKER, myThreadId);
            underlyingRecapCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.UNDERLYING_RECAP, myThreadId);
            underlyingTickerCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.UNDERLYING_TICKER, myThreadId);
            NBBOCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.NBBO, myThreadId);
            expectedOpeningPriceCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.EOP, myThreadId);
*/
            bestMarketCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.BestMarket, myThreadId);
            publicMarketCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.PUBLIC, myThreadId);


            totalClasses = classes.size();
            for (int t = 0; t < classes.size(); t++)
            {
                ProdClass sessionClass = (ProdClass)classes.get(t);

                if (!testParm.underlyingOnly)
                {
//                    CurrentMarketCallbackDV3 currentMarketClient = new CurrentMarketCallbackDV3(bestMarketMeter, bestPublicMarketMeter,t);
                    CurrentMarketCallbackDV3 currentMarketClient = new CurrentMarketCallbackDV3();
                    currentMarketClient.addMessageCounter(bestMarketCounter, publicMarketCounter);
                    com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer marketCallback =
                            TestCallbackFactory.getV3CurrentMarketConsumer(currentMarketClient);
/*
//                    RecapCallbackDV2 recapCallbackClient = new RecapCallbackDV2(recapMeter,t);
                    RecapCallbackDV2 recapCallbackClient = new RecapCallbackDV2();
                    recapCallbackClient.addMessageCounter(recapCounter);
                    com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapCallback =
                            TestCallbackFactory.getV2RecapConsumer(recapCallbackClient);

//                    NBBOCallbackDV2 nbboCallbackClient = new NBBOCallbackDV2(nbboMeter,t);
                    NBBOCallbackDV2 nbboCallbackClient = new NBBOCallbackDV2();
                    nbboCallbackClient.addMessageCounter(NBBOCounter);
                    com.cboe.idl.cmiCallbackV2.CMINBBOConsumer nbboCallback =
                            TestCallbackFactory.getV2NBBOConsumer(nbboCallbackClient);

//                    ExpectedOpeningPriceCallbackDV2 eopCallbackClient = new ExpectedOpeningPriceCallbackDV2(recapMeter,t);
                    ExpectedOpeningPriceCallbackDV2 eopCallbackClient = new ExpectedOpeningPriceCallbackDV2();
                    eopCallbackClient.addMessageCounter(expectedOpeningPriceCounter);
                    com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer eopCallback =
                            TestCallbackFactory.getV2ExpectedOpeningPriceConsumer(eopCallbackClient);

//                    BestBookCallbackDV2 bestBookCallbackClient = new BestBookCallbackDV2(bestBookMeter, t);
                    BestBookCallbackDV2 bestBookCallbackClient = new BestBookCallbackDV2();
                    bestBookCallbackClient.addMessageCounter(bestbookCounter);
                    com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer bestBookCallback = TestCallbackFactory.getV2BestBookConsumer(bestBookCallbackClient);

//                    TickerCallbackDV2 tickerCallbackClient = new TickerCallbackDV2(tickerMeter, t);
                    TickerCallbackDV2 tickerCallbackClient = new TickerCallbackDV2();
                    tickerCallbackClient.addMessageCounter(tickerCounter);
                    com.cboe.idl.cmiCallbackV2.CMITickerConsumer tickerCallback = TestCallbackFactory.getV2TickerConsumer(tickerCallbackClient);
*/
                    sessionManagerV3.getMarketQueryV3().subscribeCurrentMarketForClassV3(sessionClass.itsSessionName,sessionClass.itsClassKey,marketCallback,testParm.queueAction);
/*                    sessionManagerV3.getMarketQueryV3().subscribeRecapForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey, recapCallback,testParm.queueAction);
                    sessionManagerV3.getMarketQueryV3().subscribeNBBOForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey, nbboCallback,testParm.queueAction);
                    sessionManagerV3.getMarketQueryV3().subscribeExpectedOpeningPriceForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey, eopCallback,testParm.queueAction);
                    sessionManagerV3.getMarketQueryV3().subscribeBookDepthForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey, bestBookCallback,testParm.queueAction);
                    sessionManagerV3.getMarketQueryV3().subscribeTickerForClassV2(sessionClass.itsSessionName,sessionClass.itsClassKey, tickerCallback,testParm.queueAction);
*/
                }

                if (testParm.includeUnderlying)
                {
                    try {
                        RecapCallbackDV2 underlyingRecapClient = new RecapCallbackDV2(underlyingRecapMeter,t);
                        TickerCallbackDV2  underlyingTickerClient = new TickerCallbackDV2(underlyingTickerMeter,t);
//                        RecapCallbackDV2 underlyingRecapClient = new RecapCallbackDV2();
//                        TickerCallbackDV2  underlyingTickerClient = new TickerCallbackDV2();
                        underlyingRecapClient.addMessageCounter(underlyingRecapCounter);
                        underlyingTickerClient.addMessageCounter(underlyingTickerCounter);

                        com.cboe.idl.cmiCallbackV2.CMIRecapConsumer underlyingRecapCallback =
                                TestCallbackFactory.getV2RecapConsumer(underlyingRecapClient);

                        com.cboe.idl.cmiCallbackV2.CMITickerConsumer underlyingTickerCallback =
                                TestCallbackFactory.getV2TickerConsumer(underlyingTickerClient);

                        SessionClassStruct underlyingClass;
                        underlyingClass = sessionManagerV3.getTradingSession().getClassBySessionForKey(sessionClass.itsSessionName,
                                sessionClass.itsClassKey);

                        sessionManagerV3.getMarketQueryV2().subscribeRecapForProductV2(underlyingClass.underlyingSessionName,
                                underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingRecapCallback,testParm.queueAction);

                        sessionManagerV3.getMarketQueryV2().subscribeTickerForProductV2(underlyingClass.underlyingSessionName,
                                underlyingClass.classStruct.underlyingProduct.productKeys.productKey, underlyingTickerCallback,testParm.queueAction);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
         catch (Exception e)
        {
            System.out.println("Failed subscriptions.....................");
            e.printStackTrace();
            return;
        }

        System.out.println("Finished subscriptions.............");

        while (!testParm.threadDone)
        {
            try
            {
                Thread.currentThread().sleep(10000);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
/*            MDCollector.dumpCount();
*/
        }

        System.out.println("Finish testing ....................................................");
    }
}
