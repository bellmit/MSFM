package com.cboe.application.quote;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.Collections;

/**
 * @author Jing Chen
 */
public class UserQuoteBaseHomeImpl extends ClientBOHome
{
    public static final String TOKENIZER_DELIMITERS = ",; ";
    public static final String RATE_MONITOR_SESSIONS_PROPERTY_NAME = "rateMonitorSessions";
    public final static String CALL_WINDOW_INTERVAL_PROPERTY_NAME = "rateMonitorInterval";
    public final static String CALL_WINDOW_SIZE_PROPERTY_NAME = "rateMonitorWindow";
    public final static String QUOTE_WINDOW_INTERVAL_PROPERTY_NAME = "quoteRateMonitorInterval";
    public final static String QUOTE_WINDOW_SIZE_PROPERTY_NAME = "quoteRateMonitorWindow";
    public static final String QUOTE_SEQUENCE_SIZE_PROPERTY_NAME = "rateMonitorQuoteSequenceSize";
    public static final Long EMPTY_LONG_CONSTRAINT = 0L;
    public static final Integer EMPTY_INTEGER_CONSTRAINT = 0;
    protected Map sessionConstraints;

    public UserQuoteBaseHomeImpl() {
        super();
    }

    public void clientInitialize() throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }

        sessionConstraints = new HashMap(10);

        String sessionProperty = getProperty(UserQuoteBaseHomeImpl.RATE_MONITOR_SESSIONS_PROPERTY_NAME);
        String callWindowIntervalProperty = getProperty(UserQuoteBaseHomeImpl.CALL_WINDOW_INTERVAL_PROPERTY_NAME);
        String callWindowSizeProperty = getProperty(UserQuoteBaseHomeImpl.CALL_WINDOW_SIZE_PROPERTY_NAME);
        String quoteWindowIntervalProperty = getProperty(UserQuoteBaseHomeImpl.QUOTE_WINDOW_INTERVAL_PROPERTY_NAME);
        String quoteWindowSizeProperty = getProperty(UserQuoteBaseHomeImpl.QUOTE_WINDOW_SIZE_PROPERTY_NAME);
        String quoteSequenceSizeProperty = getProperty(UserQuoteBaseHomeImpl.QUOTE_SEQUENCE_SIZE_PROPERTY_NAME);

        if(sessionProperty.length() > 0)
        {
            StringTokenizer sessionTokenizer = new StringTokenizer(sessionProperty, UserQuoteBaseHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer callWindowIntervalTokenizer =
                    new StringTokenizer(callWindowIntervalProperty, UserQuoteBaseHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer callWindowSizeTokenizer =
                    new StringTokenizer(callWindowSizeProperty, UserQuoteBaseHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer quoteWindowIntervalTokenizer =
                    new StringTokenizer(quoteWindowIntervalProperty, UserQuoteBaseHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer quoteWindowSizeTokenizer =
                    new StringTokenizer(quoteWindowSizeProperty, UserQuoteBaseHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer quoteSequenceSizeTokenizer =
                    new StringTokenizer(quoteSequenceSizeProperty, UserQuoteBaseHomeImpl.TOKENIZER_DELIMITERS, false);

            String sessionToken;
            Long callWindowIntervalToken;
            Integer callWindowSizeToken;
            Long quoteWindowIntervalToken;
            Integer quoteWindowSizeToken;
            Integer quoteSequenceSizeToken;
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
                    callWindowIntervalToken = UserQuoteBaseHomeImpl.EMPTY_LONG_CONSTRAINT;
                }
                settingsMap.put(UserQuoteBaseHomeImpl.CALL_WINDOW_INTERVAL_PROPERTY_NAME, callWindowIntervalToken);

                if(callWindowSizeTokenizer.hasMoreTokens())
                {
                    callWindowSizeToken = Integer.valueOf(callWindowSizeTokenizer.nextToken());
                }
                else
                {
                    callWindowSizeToken = UserQuoteBaseHomeImpl.EMPTY_INTEGER_CONSTRAINT;
                }
                settingsMap.put(UserQuoteBaseHomeImpl.CALL_WINDOW_SIZE_PROPERTY_NAME, callWindowSizeToken);

                if(quoteWindowIntervalTokenizer.hasMoreTokens())
                {
                    quoteWindowIntervalToken = Long.valueOf(quoteWindowIntervalTokenizer.nextToken());
                }
                else
                {
                    quoteWindowIntervalToken = UserQuoteBaseHomeImpl.EMPTY_LONG_CONSTRAINT;
                }
                settingsMap.put(UserQuoteBaseHomeImpl.QUOTE_WINDOW_INTERVAL_PROPERTY_NAME, quoteWindowIntervalToken);

                if(quoteWindowSizeTokenizer.hasMoreTokens())
                {
                    quoteWindowSizeToken = Integer.valueOf(quoteWindowSizeTokenizer.nextToken());
                }
                else
                {
                    quoteWindowSizeToken = UserQuoteBaseHomeImpl.EMPTY_INTEGER_CONSTRAINT;
                }
                settingsMap.put(UserQuoteBaseHomeImpl.QUOTE_WINDOW_SIZE_PROPERTY_NAME, quoteWindowSizeToken);

                if(quoteSequenceSizeTokenizer.hasMoreTokens())
                {
                    quoteSequenceSizeToken = Integer.valueOf(quoteSequenceSizeTokenizer.nextToken());
                }
                else
                {
                    quoteSequenceSizeToken = UserQuoteBaseHomeImpl.EMPTY_INTEGER_CONSTRAINT;
                }
                settingsMap.put(UserQuoteBaseHomeImpl.QUOTE_SEQUENCE_SIZE_PROPERTY_NAME, quoteSequenceSizeToken);

                sessionConstraints.put(sessionToken, settingsMap);
            }
        }
        sessionConstraints = Collections.unmodifiableMap(sessionConstraints);
    }
}
