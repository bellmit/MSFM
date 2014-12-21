package com.cboe.cfix.fix.net;

/**
 * testFixNetworkAcceptor.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;
import java.util.*;

import com.cboe.cfix.util.*;
import com.cboe.client.util.*;
import com.cboe.client.util.threadpool.*;
import com.cboe.client.util.tourist.*;

/**
 * Can listen for and process an inbound FIX connection
 *
 */

public class testFixNetworkAcceptor
{
    public static Thread startFixNetworkAcceptor(String propertyPrefix, Properties properties) throws Exception
    {
        FixNetworkAcceptor fixNetworkAcceptor = new FixNetworkAcceptor();
        fixNetworkAcceptor.initialize(propertyPrefix, properties);

/*
        fixNetworkAcceptor.addFixSessionListener(new FixSessionListenerIF()
        {
            Thread  sessionBlasterThread;
            boolean keepGoing = true;

            public void sessionStarting(final FixSessionIF fixSession)
            {

            }

            public void sessionTargetLoggedIn(final FixSessionIF fixSession)
            {
                sessionBlasterThread = new Thread()
                {
                    public void run()
                    {
                        setName("MarketDataBlaster<" + fixSession.getSenderCompID() + ">");
                        ThreadHelper.sleepSeconds(1);
                        System.out.println("BIRTHED");

                        CfixMarketDataMapperImpl fixCasMarketDataMapper = new CfixMarketDataMapperImpl();
                        debugMarketDataMapper   debugMarketDataMapper  = new debugMarketDataMapper();
                        ChannelEvent            channelEvent;

//                        fixCasMarketDataMapper.setDebugMarketDataMapper(debugMarketDataMapper);

                        for (int i = 0; keepGoing == true && i < 3; i++) //TODO: FIX i<2
                        {
                            channelEvent = null;

                            try
                            {
                                if (fixSession.getQueueDepth() >= 25000)
                                {
                                    ThreadHelper.sleepSeconds(1);
                                    i--;
                                    continue;
                                }

//                                switch (random.nextInt(4))
                                switch (i)
                                {
                                    case 0:
//                                        writer.add(FixMDReqIDField.TagIDAsChars, "BAC_CURRENT_MARKET_CLASS");
                                        channelEvent = fixCasMarketDataMapper.mapMarketData(debugMarketDataMapper.debugMakeCmiCurrentMarketStruct(), i);
                                        break;
                                    case 1:
//                                        writer.add(FixMDReqIDField.TagIDAsChars, "BOOK_DEPTH:SLB1CM3");
                                        channelEvent = fixCasMarketDataMapper.mapMarketData(debugMarketDataMapper.debugMakeCmiBookDepthStruct(), i);
                                        break;
                                    case 2:
//                                        writer.add(FixMDReqIDField.TagIDAsChars, "BAC_RECAP_CLASS");
                                        channelEvent = fixCasMarketDataMapper.mapMarketData(debugMarketDataMapper.debugMakeCmiRecapStruct(), i);
                                        break;
//                                    case 4:
//                                        fixMarketDataSnapshotFullRefreshMessage.fieldCboeDebugText = FixCboeDebugTextField.create("NBBO");
//                                        fixCasMarketDataMapper.buildFixNBBO(fixMarketDataRequestMessage, fixMarketDataSnapshotFullRefreshMessage, debugMarketDataMapper.debugMakeCmiNBBOStruct());
//                                        break;
//                                    case 5:
//                                        fixCasMarketDataMapper.buildFixTicker(fixMarketDataSnapshotFullRefreshMessage, debugMarketDataMapper.debugMakeCmiTickerStruct());
//                                        break;
                                }

//                                if ((i % 10000) == 0)
//                                {
//                                    System.out.println("ENQUEUEING[" + i + "/" + fixSession.getQueueDepth() + "]");
//                                }

                                if (channelEvent != null && !fixSession.enqueueOutboundFixMessage(channelEvent))
                                {
                                    break;
                                }
                            }
                            catch (Exception ex)
                            {
                                System.out.println("Exception: " + ExceptionHelper.getStackTrace(ex));
                                System.exit(0);
                            }
                        }

                        System.out.println("DIED");
                    }
                };

                sessionBlasterThread.start();
            }

            public void sessionEnding(FixSessionIF fixSession, int sessionEndingFlags)
            {
                keepGoing = false;
            }
        });
*/
        Thread t = new Thread(fixNetworkAcceptor, "FixNetworkAcceptor");
        t.start();

        return t;
    }

    public static Thread startTouristNetworkAcceptor(String propertyPrefix, Properties properties) throws Exception
    {
        TouristNetworkAcceptor touristNetworkAcceptor = new TouristNetworkAcceptor();
        touristNetworkAcceptor.initialize(propertyPrefix, properties);
        Thread t = new Thread(touristNetworkAcceptor, "TouristNetworkAcceptor");
        t.start();

        return t;
    }

    public static void main(String[] args) throws Exception
    {
        String filename = "d:/cboe/properties/cfix.properties";

        if (args.length > 0)
        {
            filename = args[0];
        }

        System.setOut(new PrintTimeStampedStream(System.out, true, true));

        System.setErr(System.out);

        Properties properties = new Properties();

        FileInputStream fileStream = new FileInputStream(new File(filename));

        try
        {
            properties.load(fileStream);
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ExceptionHelper.getStackTrace(ex));
            System.exit(1);
        }

        fileStream.close();

        String connectionToStart;
        String runnableInitializableClass;
        RunnableInitializableIF runnableInitializable;
        PropertiesHelper propertiesHelper = new PropertiesHelper(properties);

        for (int i = 0; i < 1000; i++)
        {
            connectionToStart = propertiesHelper.getProperty("start", "" + i);
            if (connectionToStart == null)
            {
                break;
            }

            runnableInitializableClass = propertiesHelper.getProperty(connectionToStart, "cfix.runnableInitializableClass");
            if (runnableInitializableClass == null)
            {
                System.out.println("CRITICAL MISCONFIGURATION ERROR: no 'cfix.runnableInitializableClass' defined for " + connectionToStart);
                continue;
            }

            try
            {
                runnableInitializable = (RunnableInitializableIF) ClassHelper.loadClass(runnableInitializableClass);
                runnableInitializable.initialize(connectionToStart, properties);
                AdaptiveThreadPool.getDefaultThreadPool().execute(runnableInitializable, connectionToStart);
            }
            catch (Exception ex)
            {
                System.out.println("Couldn't initialize and start component: " + runnableInitializableClass);
            }
        }

//        Thread touristThread = startTouristNetworkAcceptor("50010.", properties);
//        Thread fixThread     = startFixNetworkAcceptor("50000.", properties);

        Thread.sleep(1000);
    }
}
