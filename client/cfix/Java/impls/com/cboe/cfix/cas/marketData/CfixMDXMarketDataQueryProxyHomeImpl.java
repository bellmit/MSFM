package com.cboe.cfix.cas.marketData;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.cfix.*;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Collections;

/**
 * CfixMarketDataQueryProxyHomeImpl.java
 *
 * @author Vivek Beniwal
 *
 */
public class CfixMDXMarketDataQueryProxyHomeImpl extends ClientBOHome implements CfixMDXMarketDataQueryProxyHome
{
    public static final String  TOKENIZER_DELIMITERS                          = ",; ";
    public static final String  RATE_MONITOR_SESSIONS_PROPERTY_NAME           = "rateMonitorSessionsMDX";
    public static final String  BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME = "rateMonitorIntervalMDX";
    public static final String  BOOK_DEPTH_CALL_WINDOW_SIZE_PROPERTY_NAME     = "rateMonitorWindowMDX";

    public static final Long    EMPTY_LONG_CONSTRAINT                         = new Long(0);
    public static final Integer EMPTY_INTEGER_CONSTRAINT                      = new Integer(0);

    private Map sessionConstraints;

    public CfixMDXMarketDataQueryIF create(CfixSessionManager sessionManager) throws SystemException, CommunicationException, AuthorizationException
    {
        return this.createMDXQueryProxy(sessionManager);
    }

    private CfixMDXMarketDataQueryIF createMDXQueryProxy(CfixSessionManager sessionManager) throws SystemException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "Creating CfixMDXMarketDataQueryProxy for " + sessionManager);}
        CfixMDXMarketDataQueryProxy bo = new CfixMDXMarketDataQueryProxy(sessionConstraints);

        bo.create(String.valueOf(bo.hashCode()));
        bo.setCfixSessionManager(sessionManager);

        //Every bo object must be added to the container.
        addToContainer(bo);
        //The addToContainer call MUST occur prior to creation of the interceptor.
        CfixMDXMarketDataQueryInterceptor boi = null;

        try
        {
            boi = (CfixMDXMarketDataQueryInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(sessionManager);
        }
        catch (Exception ex)
        {
            Log.exception(this, ex);
            return null;
        }

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        CfixMDXMarketDataQueryIF marketQuery = boi;

        return marketQuery;

    }


    public void clientInitialize() throws Exception
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Initializing CfixMDXMarketDataQueryProxyHomeImpl. " );
        }


        sessionConstraints = new HashMap(10);

        String sessionProperty                     = getProperty(RATE_MONITOR_SESSIONS_PROPERTY_NAME);
        String bookDepthCallWindowIntervalProperty = getProperty(BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME);
        String bookDepthCallWindowSizeProperty     = getProperty(BOOK_DEPTH_CALL_WINDOW_SIZE_PROPERTY_NAME);

        if (sessionProperty.length() > 0)
        {
            StringTokenizer sessionTokenizer = new StringTokenizer(sessionProperty, TOKENIZER_DELIMITERS, false);
            StringTokenizer bookDepthCallWindowIntervalTokenizer = new StringTokenizer(bookDepthCallWindowIntervalProperty, TOKENIZER_DELIMITERS, false);
            StringTokenizer bookDepthCallWindowSizeTokenizer     = new StringTokenizer(bookDepthCallWindowSizeProperty,     TOKENIZER_DELIMITERS, false);

            String sessionToken;
            Long bookDepthCallWindowIntervalToken;
            Integer bookDepthCallWindowSizeToken;
            Map settingsMap;

            while (sessionTokenizer.hasMoreTokens())
            {
                sessionToken = sessionTokenizer.nextToken();

                settingsMap = new HashMap(2);

                if (bookDepthCallWindowIntervalTokenizer.hasMoreTokens())
                {
                    bookDepthCallWindowIntervalToken = new Long(bookDepthCallWindowIntervalTokenizer.nextToken());
                }
                else
                {
                    bookDepthCallWindowIntervalToken = EMPTY_LONG_CONSTRAINT;
                }

                settingsMap.put(BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME, bookDepthCallWindowIntervalToken);

                if (bookDepthCallWindowSizeTokenizer.hasMoreTokens())
                {
                    bookDepthCallWindowSizeToken = new Integer(bookDepthCallWindowSizeTokenizer.nextToken());
                }
                else
                {
                    bookDepthCallWindowSizeToken = EMPTY_INTEGER_CONSTRAINT;
                }

                settingsMap.put(BOOK_DEPTH_CALL_WINDOW_SIZE_PROPERTY_NAME, bookDepthCallWindowSizeToken);

                sessionConstraints.put(sessionToken, settingsMap);
            }
        }

        sessionConstraints = Collections.unmodifiableMap(sessionConstraints);
    }
}
