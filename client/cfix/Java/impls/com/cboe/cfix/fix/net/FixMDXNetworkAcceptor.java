package com.cboe.cfix.fix.net;

import com.cboe.client.util.RunnableInitializableIF;
import com.cboe.client.util.PropertiesHelper;
import com.cboe.client.util.ThreadHelper;
import com.cboe.client.util.threadpool.AdaptiveThreadPool;
import com.cboe.cfix.interfaces.FixSessionManagerIF;
import com.cboe.cfix.interfaces.FixSessionListenerIF;
import com.cboe.cfix.interfaces.FixSessionIF;
import com.cboe.cfix.fix.session.FixSessionManagerLocator;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * FixMDXNetworkAcceptor.java
 * Replaces FixNetworkAcceptor - for MDX enabled CFIX
 *
 * @author Dmitry Volpyansky - Vivek Beniwal
 *
 */

public class FixMDXNetworkAcceptor implements RunnableInitializableIF
{
    protected ServerSocket        serverSocket;
    protected FixSessionManagerIF fixSessionManager;
    protected Properties          properties;
    protected String              propertyPrefix;
    protected String              targetCompID;
    protected boolean             terminated;
    protected List                fixSessionListenerList = new ArrayList();
    protected PropertiesHelper    propertiesHelper;

    public FixMDXNetworkAcceptor()
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
            Log.debug("Initialized FixMDXNetworkAcceptor. CFIX is MDX Enabled.");
        }

        this.properties = properties;

        this.propertyPrefix = propertyPrefix;

        propertiesHelper = new PropertiesHelper(properties, propertyPrefix);

        // targetCompID is the FIX ID of the CFIX engine -
        targetCompID = propertiesHelper.getProperty("cfix.fixNetworkAcceptor.targetCompID");

        // VivekB: Changed FixSessionManager class to FixMDXSessionManager
        this.fixSessionManager = FixSessionManagerLocator.createFixSessionManager(targetCompID, propertiesHelper.getProperty("cfix.fixNetworkAcceptor.fixSessionManagerClassMDX", "com.cboe.cfix.fix.session.FixMDXSessionManager"));

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
                            Log.information(Thread.currentThread().getName() + " Not Accepting Connections Until " + startUpAtHHMMTime.getTime());
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

                        Log.information(Thread.currentThread().getName() + " Accepting Connections on " + port);

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

                        Log.information(Thread.currentThread().getName() + " Accepted New Connection (" + (++connectionCounter) + ") From " + socket);

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
