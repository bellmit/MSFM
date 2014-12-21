package com.cboe.application.tourist;

/**
 * TouristHomeImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Starts the Tourist connection for the JVM
 *
 */

import com.cboe.client.util.threadpool.*;
import com.cboe.client.util.tourist.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.*;

public class TouristHomeImpl extends ClientBOHome
{
    public final static String TOURIST_PORT = "TouristPortNumber";

    public TouristNetworkAcceptor touristNetworkAcceptor;

    public void clientInitialize() throws Exception
    {
        if (touristNetworkAcceptor == null)
        {
            int port = 0;

            String touristPort = null;

            try
            {
                touristPort = getProperty(TOURIST_PORT);
            }
            catch (Exception ex)
            {

            }

            try
            {
                port = Integer.parseInt(touristPort);
            }
            catch (Exception ex)
            {

            }

            if (port == 0)
            {
                touristPort = System.getProperty("prefixTouristPort");

                try
                {
                    port = Integer.parseInt(touristPort);
                }
                catch (Exception ex)
                {

                }
            }

            if (port == 0)
            {
                touristPort = System.getProperty("touristPort");

                try
                {
                    port = Integer.parseInt(touristPort);
                }
                catch (Exception ex)
                {

                }
            }

            if (port > 0 && port < 65536)
            {
                touristNetworkAcceptor = new TouristNetworkAcceptor(port);

                StringBuilder starting = new StringBuilder(50);
                starting.append("TOURIST: STARTING TouristAcceptor on port(").append(port).append(")");
                Log.information(this, starting.toString());

                AdaptiveThreadPool.getDefaultThreadPool().execute(touristNetworkAcceptor, "Tourist");
            }
            else
            {
                Log.information(this, "TOURIST: NOT STARTING TouristAcceptor");
            }
        }

        try
        {
            registerCommand(this,
                            "executeTourist",
                            "adminExecuteTourist",
                            "executes a Tourist from the specified string and returns results as an XML string",
                            new String[] {"java.lang.String"},
                            new String[] {"TouristString: 'com.cboe.xxx.XXXTourist?param1=value1&param2=value2&param3=value3'"});
        }
        catch (Exception ex)
        {
            //according to Ravi Vizarani, we can ignore this exception
        }
    }

    public String adminExecuteTourist(String touristString)
    {
        return TouristSession.createAndExecuteTourist(touristString, false);
    }
}
