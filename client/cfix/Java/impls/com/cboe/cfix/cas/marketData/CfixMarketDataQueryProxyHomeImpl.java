package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataQueryProxyHomeImpl.java
 *
 * @author Jing Chen
 *
 */

import java.util.*;

import com.cboe.domain.startup.*;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;

public class CfixMarketDataQueryProxyHomeImpl extends ClientBOHome implements CfixMarketDataQueryProxyHome
{
    public static final String  TOKENIZER_DELIMITERS                          = ",; ";
    public static final String  RATE_MONITOR_SESSIONS_PROPERTY_NAME           = "rateMonitorSessions";
    public static final String  BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME = "rateMonitorInterval";
    public static final String  BOOK_DEPTH_CALL_WINDOW_SIZE_PROPERTY_NAME     = "rateMonitorWindow";

    public static final Long    EMPTY_LONG_CONSTRAINT                         = 0L;
    public static final Integer EMPTY_INTEGER_CONSTRAINT                      = 0;

    private Map sessionConstraints;

    public CfixMarketDataQueryIF create(CfixSessionManager sessionManager) throws SystemException, CommunicationException, AuthorizationException
    {
        return this.createQueryProxy(sessionManager);
    }

    private CfixMarketDataQueryIF createQueryProxy(CfixSessionManager sessionManager) throws SystemException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "Creating CfixMarketDataQueryProxy for " + sessionManager);}
        CfixMarketDataQueryProxy bo = new CfixMarketDataQueryProxy(sessionConstraints);

        bo.create(String.valueOf(bo.hashCode()));
        bo.setCfixSessionManager(sessionManager);

        // Every BObject must be added to the container.
        addToContainer(bo);
        //The addToContainer call MUST occur prior to creation of the interceptor.
        CfixMarketDataQueryInterceptor boi = null;

        try
        {
            boi = (CfixMarketDataQueryInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(sessionManager);
        }
        catch (Exception ex)
        {
            Log.exception(this, ex);
            return null;
        }

        //Every BObject create MUST have a name...if the object is to be a managed object.
        CfixMarketDataQueryIF marketQuery = boi;

        return marketQuery;
    }

    public void clientInitialize() throws Exception
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Initializing CfixMarketDataQueryProxyHomeImpl. ");
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
                    bookDepthCallWindowIntervalToken = Long.valueOf(bookDepthCallWindowIntervalTokenizer.nextToken());
                }
                else
                {
                    bookDepthCallWindowIntervalToken = EMPTY_LONG_CONSTRAINT;
                }

                settingsMap.put(BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME, bookDepthCallWindowIntervalToken);

                if (bookDepthCallWindowSizeTokenizer.hasMoreTokens())
                {
                    bookDepthCallWindowSizeToken = Integer.valueOf(bookDepthCallWindowSizeTokenizer.nextToken());
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
