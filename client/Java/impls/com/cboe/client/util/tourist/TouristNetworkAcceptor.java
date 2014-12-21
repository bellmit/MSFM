package com.cboe.client.util.tourist;

/**
 * TouristNetworkAcceptor.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Maintains the server side of the Tourist connection
 *
 * Creates and executes valid Tourists from Strings
 *
 * String looks like: "com.cboe.somepackage.DoSomethingTourist?key1=value1&key2=value2&key3=value3"
 *
 */

import java.net.*;
import java.util.*;

import com.cboe.client.util.*;
import com.cboe.client.util.threadpool.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class TouristNetworkAcceptor implements RunnableInitializableIF
{
    protected ServerSocket serverSocket;
    protected Properties   properties;
    protected String       propertyPrefix;
    protected int          specifiedPort;
    protected PropertiesHelper propertiesHelper;

    public TouristNetworkAcceptor()
    {

    }

    public TouristNetworkAcceptor(int port)
    {
        specifiedPort = port;
    }

    public void initialize(String propertyPrefix, Properties properties) throws Exception
    {
        this.properties = properties;

        this.propertyPrefix = propertyPrefix;

        this.propertiesHelper = new PropertiesHelper(properties, propertyPrefix);
    }

    public void setPort(int port)
    {
        specifiedPort = port;
    }

    public void run()
    {
        int            port              = propertiesHelper.getPropertyInt("cfix.touristNetworkAcceptor.port", Integer.toString(specifiedPort));
        boolean        retryListening    = propertiesHelper.getPropertyBoolean("cfix.touristNetworkAcceptor.retryListening", "true");
        boolean        logOutput         = propertiesHelper.getPropertyBoolean("cfix.touristNetworkAcceptor.logOutput", "true");
        int            attempt           = 0;
        int            connectionCounter = 0;
        Socket         socket            = null;
        TouristSession touristSession    = null;
        boolean        terminate         = false;
        StringBuilder  sb                = logOutput ? new StringBuilder(100) : null;

        while (!terminate)
        {
            try
            {
                while (!terminate)
                {
                    if (serverSocket == null)
                    {
                        try
                        {
                            serverSocket = new ServerSocket(port);

                            attempt = 0;
                        }
                        catch (Exception ex)
                        {
                            if (attempt++ > 4)
                            {
                                String thname = Thread.currentThread().getName();
                                StringBuilder cantlisten = new StringBuilder(thname.length()+30);
                                cantlisten.append(thname).append(" Can't listen on port(").append(port).append(")");
                                Log.alarm(cantlisten.toString());
                                Log.exception(ex);

                                if (!retryListening)
                                {
                                    terminate = true;
                                    break;
                                }
                            }

                            Thread.sleep(1000);

                            continue;
                        }
                    }

                    if (logOutput)
                    {
                        sb.setLength(0);
                        sb.append(Thread.currentThread().getName()).append(" TOURIST Accepting Connections on ").append(port);
                        Log.information(sb.toString());
                    }

                    socket = serverSocket.accept();

                    if (logOutput)
                    {
                        sb.setLength(0);
                        sb.append(Thread.currentThread().getName()).append(" TOURIST Accepted New Connection (")
                          .append(++connectionCounter).append(") From ").append(socket);
                        Log.information(sb.toString());
                    }

                    touristSession = new TouristSession(socket, logOutput);

                    if (touristSession == null)
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

                    AdaptiveThreadPool.getDefaultThreadPool().execute(touristSession, "TouristSession(" + connectionCounter + ")");
                }
            }
            catch (Exception ex)
            {
                Log.exception(Thread.currentThread().getName() + " Error accepting on port(" + port + ")", ex);
            }

            try
            {
                if (serverSocket != null)
                {
                    serverSocket.close();
                    serverSocket = null;
                }
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
