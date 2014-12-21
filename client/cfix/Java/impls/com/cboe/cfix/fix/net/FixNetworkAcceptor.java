package com.cboe.cfix.fix.net;

/**
 * FixNetworkAcceptor.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.net.*;
import java.util.*;

import com.cboe.cfix.fix.session.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.client.util.threadpool.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

/**
 * Accepts network connection requests, spins off a FixSession, and hands off the connected Socket to the FixSession
 *
 */

public class FixNetworkAcceptor implements RunnableInitializableIF
{
    protected ServerSocket        serverSocket;
    protected FixSessionManagerIF fixSessionManager;
    protected Properties          properties;
    protected String              propertyPrefix;
    protected String              targetCompID;
    protected boolean             terminated;
    protected List                fixSessionListenerList = new ArrayList();
    protected PropertiesHelper    propertiesHelper;

    public FixNetworkAcceptor()
    {

    }

    public void addFixSessionListener(FixSessionListenerIF fixSessionListener)
    {
        fixSessionListenerList.add(fixSessionListener);
    }

    public void removeFixSessionListener(FixSessionListenerIF fixSessionListener)
    {
        fixSessionListenerList.remove(fixSessionListener);
    }

    public void initialize(String propertyPrefix, Properties properties) throws Exception
    {
        if(Log.isDebugOn())
        {
            Log.debug("Initialized FixNetworkAcceptor. CFIX is Event Channel Enabled.");
        }

        this.properties = properties;

        this.propertyPrefix = propertyPrefix;

        propertiesHelper = new PropertiesHelper(properties, propertyPrefix);

        targetCompID = propertiesHelper.getProperty("cfix.fixNetworkAcceptor.targetCompID");

        this.fixSessionManager = FixSessionManagerLocator.createFixSessionManager(targetCompID, propertiesHelper.getProperty("cfix.fixNetworkAcceptor.fixSessionManagerClass", "com.cboe.cfix.fix.session.FixSessionManager"));

        this.fixSessionManager.initialize(propertyPrefix, this.properties);
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void run()
    {
        Log.information(Thread.currentThread().getName() + " Started");

        Socket socket = null;
        int attempt = 2;
        FixSessionIF fixSession = null;
        int connectionCounter = 0;

        boolean retryListening           = propertiesHelper.getPropertyBoolean("cfix.fixNetworkAcceptor.retryListening", "true");
        boolean oneConnectionAtATime     = propertiesHelper.getPropertyBoolean("cfix.fixNetworkAcceptor.oneConnectionAtATime", "false");
        int     port                     = propertiesHelper.getPropertyInt("cfix.fixNetworkAcceptor.port");
        String  adaptiveThreadPoolPrefix = propertiesHelper.getProperty("cfix.fixNetworkAcceptor.fixSessionThreadPoolConfigurationPrefix", "defaults");
        int     startUpCheckSeconds      = propertiesHelper.getPropertyInt("cfix.fixNetworkAcceptor.startUpCheckSeconds", "30");

        for (int i = 0; i < 100000; i++)
        {
            String startUpAtHHMMString = propertiesHelper.getProperty("cfix.fixNetworkAcceptor.startUpAtHHMM");

            if (startUpAtHHMMString != null)
            {
                int hour = startUpAtHHMMString.charAt(0) - '0';
                hour = hour * 10 + startUpAtHHMMString.charAt(1) - '0';

                int minute = startUpAtHHMMString.charAt(3) - '0';
                minute = minute * 10 + startUpAtHHMMString.charAt(4) - '0';

                if (hour != 0 || minute != 0)
                {
                    GregorianCalendar startUpAtHHMMTime = new GregorianCalendar();

                    startUpAtHHMMTime.set(startUpAtHHMMTime.get(Calendar.YEAR), startUpAtHHMMTime.get(Calendar.MONTH), startUpAtHHMMTime.get(Calendar.DATE), hour, minute, 0);

                    if (System.currentTimeMillis() < startUpAtHHMMTime.getTime().getTime())
                    {
                        if (i == 0)
                        {
                            String startTime = startUpAtHHMMTime.getTime().toString();
                            String threadName = Thread.currentThread().getName();
                            StringBuilder notYet = new StringBuilder(startTime.length()+threadName.length()+40);
                            notYet.append(threadName).append(" Not Accepting Connections Until ").append(startTime);
                            Log.information(notYet.toString());
                        }

                        ThreadHelper.sleepSeconds(startUpCheckSeconds);

                        continue;
                    }
                }

                startUpAtHHMMString = null;
            }

            break;
        }

        serverSocket = null;

        while (!terminated)
        {
            try
            {
                try
                {
                    while (!terminated)
                    {
                        if (serverSocket == null)
                        {
                            try
                            {
                                serverSocket = new ServerSocket(port, oneConnectionAtATime ? 1 : 15);

                                attempt = 0;
                            }
                            catch (Exception ex)
                            {
                                if (attempt++ > 4)
                                {
                                    Log.alarm(Thread.currentThread().getName() + " Can't listen on port(" + port + ")");
                                    Log.exception(ex);

                                    if (!retryListening)
                                    {
                                        Log.alarm(Thread.currentThread().getName() + " Stopping listening on port(" + port + ")");
                                        terminated = true;
                                        break;
                                    }
                                }

                                Thread.sleep(1000);

                                continue;
                            }
                        }

                        String threadName = Thread.currentThread().getName();
                        StringBuilder accepting = new StringBuilder(threadName.length()+65);
                        accepting.append(threadName).append(" Accepting Connections on ").append(port);
                        Log.information(accepting.toString());

                        socket = serverSocket.accept();

                        if (oneConnectionAtATime)
                        {
                            try
                            {
                                serverSocket.close();
                                serverSocket = null;
                            }
                            catch (Exception ex)
                            {

                            }
                        }

                        accepting.setLength(0);
                        accepting.append(threadName).append(" Accepted New Connection (").append((++connectionCounter))
                                 .append(") From ").append(socket);
                        Log.information(accepting.toString());

                        fixSession = fixSessionManager.createFixSession(socket);
                        if (fixSession == null)
                        {
                            try
                            {
                                socket.close();
                                socket = null;
                            }
                            catch (Exception ex)
                            {

                            }

                            continue;
                        }

                        for (Iterator iterator = fixSessionListenerList.iterator(); iterator.hasNext();)
                        {
                            fixSession.addFixSessionListener((FixSessionListenerIF) iterator.next());
                        }

                        AdaptiveThreadPool sessionThreadPool;

                        if (adaptiveThreadPoolPrefix != null)
                        {
                            sessionThreadPool = AdaptiveThreadPool.createThreadPool(adaptiveThreadPoolPrefix, properties);
                        }
                        else
                        {
                            sessionThreadPool = AdaptiveThreadPool.createThreadPool();
                        }

                        sessionThreadPool.startPool();

                        fixSession.setThreadPool(sessionThreadPool);

                        sessionThreadPool.execute(fixSession, "FixSession");

                        if (oneConnectionAtATime)
                        {
                            fixSession.blockUntilSessionTerminated();
                        }
                    }
                }
                catch (Exception ex)
                {
                    Log.exception(Thread.currentThread().getName() + " Error accepting on port(" + port + ")", ex);
                }

                if (serverSocket != null)
                {
                    serverSocket.close();
                    serverSocket = null;
                }

                if (terminated)
                {
                    continue;
                }

                Thread.sleep(150);
            }
            catch (Exception exx)
            {

            }
        }

        if (serverSocket != null)
        {
            try
            {
                serverSocket.close();
            }
            catch (Exception exx)
            {

            }
            serverSocket = null;
        }

        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (Exception exx)
            {

            }
            socket = null;
        }
    }
}
