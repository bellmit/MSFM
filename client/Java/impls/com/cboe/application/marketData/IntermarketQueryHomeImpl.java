//
// -----------------------------------------------------------------------------------
// Source file: IntermarketQueryHomeImpl.java
//
// PACKAGE: com.cboe.application.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.marketData;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.IntermarketQuery;
import com.cboe.interfaces.application.IntermarketQueryHome;
import com.cboe.interfaces.application.SessionManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class IntermarketQueryHomeImpl extends ClientBOHome implements IntermarketQueryHome
{
    public static final String TOKENIZER_DELIMITERS = ",; ";
    public static final String RATE_MONITOR_SESSIONS_PROPERTY_NAME = "rateMonitorSessions";
    public final static String CALL_WINDOW_INTERVAL_PROPERTY_NAME = "rateMonitorInterval";
    public final static String CALL_WINDOW_SIZE_PROPERTY_NAME = "rateMonitorWindow";

    public static final Long EMPTY_LONG_CONSTRAINT = 0L;
    public static final Integer EMPTY_INTEGER_CONSTRAINT = 0;

    private Map sessionConstraints;

    public IntermarketQuery create(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating IntermarketQueryImpl for " + sessionManager);
        }
        IntermarketQueryImpl bo = new IntermarketQueryImpl(sessionConstraints);
        bo.setSessionManager(sessionManager);

        //add the bo to the container.
        addToContainer( bo );

  	    bo.create( String.valueOf( bo.hashCode() ) );

    	IntermarketQueryInterceptor boi = null;
        try
        {
            boi = (IntermarketQueryInterceptor)this.createInterceptor( bo );
            boi.setSessionManager(sessionManager);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch ( Throwable ex)
        {
            Log.alarm(this, "Failed to create interceptor");
        }

        return boi;
    }

    public void clientInitialize() throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }

        sessionConstraints = new HashMap(10);

        String sessionProperty = getProperty(RATE_MONITOR_SESSIONS_PROPERTY_NAME);
        String callWindowIntervalProperty = getProperty(CALL_WINDOW_INTERVAL_PROPERTY_NAME);
        String callWindowSizeProperty = getProperty(CALL_WINDOW_SIZE_PROPERTY_NAME);

        if(sessionProperty.length() > 0)
        {
            StringTokenizer sessionTokenizer = new StringTokenizer(sessionProperty, TOKENIZER_DELIMITERS, false);
            StringTokenizer callWindowIntervalTokenizer =
                    new StringTokenizer(callWindowIntervalProperty, TOKENIZER_DELIMITERS, false);
            StringTokenizer callWindowSizeTokenizer =
                    new StringTokenizer(callWindowSizeProperty, TOKENIZER_DELIMITERS, false);

            String sessionToken;
            Long callWindowIntervalToken;
            Integer callWindowSizeToken;
            Map settingsMap;

            while(sessionTokenizer.hasMoreTokens())
            {
                sessionToken = sessionTokenizer.nextToken();
                settingsMap = new HashMap(5);
                if(callWindowIntervalTokenizer.hasMoreTokens())
                {
                    callWindowIntervalToken = Long.valueOf(callWindowIntervalTokenizer.nextToken());
                }
                else
                {
                    callWindowIntervalToken = EMPTY_LONG_CONSTRAINT;
                }
                settingsMap.put(CALL_WINDOW_INTERVAL_PROPERTY_NAME, callWindowIntervalToken);

                if(callWindowSizeTokenizer.hasMoreTokens())
                {
                    callWindowSizeToken = Integer.valueOf(callWindowSizeTokenizer.nextToken());
                }
                else
                {
                    callWindowSizeToken = EMPTY_INTEGER_CONSTRAINT;
                }
                settingsMap.put(CALL_WINDOW_SIZE_PROPERTY_NAME, callWindowSizeToken);

                sessionConstraints.put(sessionToken, settingsMap);
            }
        }
        sessionConstraints = Collections.unmodifiableMap(sessionConstraints);
    }
}