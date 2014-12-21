//
// -----------------------------------------------------------------------------------
// Source file: CASTestMDV4Login.java
//
// PACKAGE: com.cboe.testDrive
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.testDrive;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiV4.UserSessionManagerV4;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;

import com.cboe.exceptions.AuthorizationException;

public class CASTestMDV4Login
{
    private String userID;
    private TestParameter testParm = null;
    private UserSessionManagerV4 sessionManagerV4 = null;
    private CASMeter recapMeter = null;
    private CASMeter tickerMeter = null;
    private MarketDataCounter recapCounter = null;
    private MarketDataCounter tickerCounter = null;
    private MarketDataCounter bestMarketCounter = null;
    private MarketDataCounter publicMarketCounter = null;
    private ProdClass[] prodClasses;
    private ArrayList subscriptions;

    short queueAction;
    String host;
    int port;
    char mode;
    short sessionMode;
    boolean gmdTextFlag;

    protected int testID = 0;

    public CASTestMDV4Login(TestParameter parm, String userID, String host, int port, char mode,
                             boolean gmdTextFlag, short sessionMode, short queueAction, int testID)
    {
        this.userID = userID;
        this.testParm = parm;
        this.testID = testID;
        this.queueAction = queueAction;
        this.host = host;
        this.port = port;
        this.mode = mode;
        this.sessionMode = sessionMode;
        this.gmdTextFlag = gmdTextFlag;
        initialize();
    }

    public void login()
    {
        UserLogonStruct logonStruct = new UserLogonStruct(userID, userID, Version.CMI_VERSION, mode);
        com.cboe.testDrive.CMIUserSessionAdmin userSessionAdminCallback = new com.cboe.testDrive.CMIUserSessionAdmin();
        System.out.println("Logging on to " + host + " CAS as " + userID + " password " + userID);

        try
        {
            long startTime = System.currentTimeMillis();

            sessionManagerV4 = CASLogon.logonToCASV4(userID, userID, host, port,
                                                     mode, gmdTextFlag, sessionMode);
            long endTime = System.currentTimeMillis();

            userSessionAdminCallback.setUserSessionManager(sessionManagerV4);
            userSessionAdminCallback.setUserLogonStruct(logonStruct);
            System.out.println("Successful login: " + host + ":" + userID + ":" + endTime + ":" +
                               startTime + ":" + (endTime - startTime) + ":ms");

            setupSubscriptions();
        }
        catch(Exception e)
        {
            System.out.println(userID + " login attempt to " + host + " failed");
            e.printStackTrace();
        }
    }

    public void subscribe()
    {
        subscribe(new CountDownLatch(1));
    }

    public void subscribe(final CountDownLatch latch)
    {
        ProdClass[] classes = getProdClasses();
        if(classes == null || classes.length == 0)
        {
            System.out.println("Error: No product classes have been specified");
            latch.countDown();
            return;
        }

        if(subscriptions == null || subscriptions.size() == 0)
        {
            System.out.println("Error: Cannot subscribe because there are no subscriptions setup for user "+userID);
            latch.countDown();
            return;
        }

        System.out.println(userID + " Subscribing for V4 Market Data................");

        Runnable r = new Runnable()
        {
            public void run()
            {
                try
                {
                    for(Iterator i=subscriptions.iterator(); i.hasNext(); )
                    {
                        MDSubscription sub = (MDSubscription)i.next();
                        sub.subscribe();
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Error during subscriptions for user " + userID);
                    e.printStackTrace();
                }
                finally
                {
                    latch.countDown();
                }
            }
        };
        new Thread(r).start();
    }

    public void unsubscribe()
    {
        unsubscribe(new CountDownLatch(1));
    }

    public void unsubscribe(final CountDownLatch latch)
    {
        Runnable r = new Runnable()
        {
            public void run()
            {
                try
                {
                    for(Iterator i = subscriptions.iterator(); i.hasNext();)
                    {
                        MDSubscription sub = (MDSubscription)i.next();
                        sub.unsubscribe();
                        i.remove();
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Error during unsubscriptions for user "+userID);
                    e.printStackTrace();
                }
                finally
                {
                    latch.countDown();
                }
            }
        };
        new Thread(r).start();
    }

    private void initialize()
    {
        bestMarketCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.BEST_MARKET_V4, testID);
        publicMarketCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.PUBLIC_V4, testID);
        recapCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.RECAP_V4, testID);
        tickerCounter = MDCollector.getMarketDataCounterByTypeByThreadID(MDCollector.TICKER_V4, testID);

        login();
    }

    // build list of unique ProdClasses from the list of all in the TestParameter
    private ProdClass[] getProdClasses()
    {
        if(prodClasses == null || prodClasses.length == 0)
        {
            ArrayList classes = new ArrayList();
            int lastClass = 0;
            ProdClass product = (ProdClass) testParm.productKeys.get(0);
            classes.add(product);
            int currentClass = product.itsClassKey;
            System.out.println(userID + " added class = " + classes.size() + " -> " + currentClass);
            for(int i = 0; i < testParm.productKeys.size(); i++)
            {
                product = (ProdClass) testParm.productKeys.get(i);
                currentClass = product.itsClassKey;
                if(lastClass != currentClass)
                {
                    if(lastClass != 0)
                    {
                        classes.add(product);
                        System.out.println(userID + " added class = " + classes.size() + " -> " + currentClass);
                    }
                    lastClass = currentClass;
                }
            }
            prodClasses = (ProdClass[]) classes.toArray(new ProdClass[classes.size()]);
        }
        return prodClasses;
    }

    private void setupSubscriptions()
    {
        subscriptions = new ArrayList();
        ProdClass[] classes = getProdClasses();
        for(int t = 0; t < classes.length; t++)
        {
            try
            {
                //currentMarket -- separate consumer for each class
                CurrentMarketCallbackDV4 currentMarketClient = new CurrentMarketCallbackDV4();
                currentMarketClient.addMessageCounter(bestMarketCounter, publicMarketCounter);
                CMICurrentMarketConsumer marketCallback =
                        TestCallbackFactory.getV4CurrentMarketConsumer(currentMarketClient);
                MDSubscription cmSub = new MDSubscription(sessionManagerV4, classes[t].itsClassKey,
                                                          marketCallback, queueAction);
                subscriptions.add(cmSub);

                //recap -- separate consumer for each class
                RecapCallbackDV4 recapClient = new RecapCallbackDV4(recapMeter, t);
                recapClient.addMessageCounter(recapCounter);
                CMIRecapConsumer recapCallback = TestCallbackFactory.getV4RecapConsumer(recapClient);
                MDSubscription recapSub = new MDSubscription(sessionManagerV4, classes[t].itsClassKey,
                                                             recapCallback, queueAction);
                subscriptions.add(recapSub);

                //ticker -- separate consumer for each class
                TickerCallbackDV4 tickerClient = new TickerCallbackDV4(tickerMeter, t);
                tickerClient.addMessageCounter(tickerCounter);
                CMITickerConsumer tickerCallback = TestCallbackFactory.getV4TickerConsumer(tickerClient);
                MDSubscription tickerSub = new MDSubscription(sessionManagerV4, classes[t].itsClassKey,
                                                              tickerCallback, queueAction);
                subscriptions.add(tickerSub);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private class MDSubscription
    {
        static final short CURRENT_MARKET = 1;
        static final short RECAP = 2;
        static final short TICKER = 3;
        static final String CURRENT_MARKET_DESC = "V4 Current Market";
        static final String RECAP_DESC = "V4 Recap";
        static final String TICKER_DESC = "V4 Ticker";

        UserSessionManagerV4 session;
        int classKey;
        Object cmiConsumer;
        short mdType;
        String mdDesc;
        short queueAction;
        String userID;

        MDSubscription(UserSessionManagerV4 sessionManagerV4, int classKey, Object cmiConsumer, short queueAction)
        {
            mdType = 0;
            if(cmiConsumer instanceof CMICurrentMarketConsumer)
            {
                mdType = CURRENT_MARKET;
                mdDesc = CURRENT_MARKET_DESC;
            }
            else if(cmiConsumer instanceof CMIRecapConsumer)
            {
                mdType = RECAP;
                mdDesc = RECAP_DESC;
            }
            else if(cmiConsumer instanceof CMITickerConsumer)
            {
                mdType = TICKER;
                mdDesc = TICKER_DESC;
            }
            if(mdType == 0)
            {
                throw new IllegalArgumentException("Unsupported CMi consumer type: '"+cmiConsumer.getClass().getName()+"'");
            }

            this.session = sessionManagerV4;
            this.classKey = classKey;
            this.cmiConsumer = cmiConsumer;
            this.queueAction = queueAction;
            try
            {
                this.userID = session.getValidUser().userId;
            }
            catch(UserException e)
            {
                e.printStackTrace();
            }
        }

        void subscribe()
        {
            try
            {
                switch(mdType)
                {
                    case CURRENT_MARKET:
                        session.getMarketQueryV4().subscribeCurrentMarket(classKey, (CMICurrentMarketConsumer) cmiConsumer, queueAction);
                        break;
                    case RECAP:
                        session.getMarketQueryV4().subscribeRecap(classKey, (CMIRecapConsumer) cmiConsumer, queueAction);
                        break;
                    case TICKER:
                        session.getMarketQueryV4().subscribeTicker(classKey, (CMITickerConsumer) cmiConsumer, queueAction);
                        break;
                }
                System.out.println(userID + " subscribed for " + mdDesc + " (classKey=" + classKey + ")");
            }
            catch(AuthorizationException e)
            {
                System.out.println("ERROR: " + userID + " is not authorized to for " + mdDesc + " subscription:");
                e.printStackTrace();
            }
            catch(UserException e)
            {
                System.out.println("ERROR: " + userID + " " + mdDesc + " subscription failed:");
                e.printStackTrace();
            }
        }

        void unsubscribe()
        {
            try
            {
                switch(mdType)
                {
                    case CURRENT_MARKET:
                        session.getMarketQueryV4().unsubscribeCurrentMarket(classKey, (CMICurrentMarketConsumer) cmiConsumer);
                        break;
                    case RECAP:
                        session.getMarketQueryV4().unsubscribeRecap(classKey, (CMIRecapConsumer) cmiConsumer);
                        break;
                    case TICKER:
                        session.getMarketQueryV4().unsubscribeTicker(classKey, (CMITickerConsumer) cmiConsumer);
                        break;
                }
                System.out.println(userID + " unsubscribed for " + mdDesc + " (classKey=" + classKey + ")");
            }
            catch(UserException e)
            {
                System.out.println("ERROR: " + userID + " " + mdDesc + " unsubscription failed.");
                e.printStackTrace();
            }
        }
    }
}
