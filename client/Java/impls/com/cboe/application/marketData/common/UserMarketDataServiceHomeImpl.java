package com.cboe.application.marketData.common;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserMarketDataServiceHome;
import com.cboe.interfaces.application.UserMarketDataService;
import com.cboe.interfaces.application.inprocess.MarketQuery;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.inprocess.marketData.MarketQueryImpl;

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Collections;

/**
 * @author Jing Chen
 */
public class UserMarketDataServiceHomeImpl extends ClientBOHome implements UserMarketDataServiceHome
{
    public static final String TOKENIZER_DELIMITERS = ",; ";
    public static final String RATE_MONITOR_SESSIONS_PROPERTY_NAME = "rateMonitorSessions";
    public final static String BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME = "rateMonitorInterval";
    public final static String BOOK_DEPTH_CALL_WINDOW_SIZE_PROPERTY_NAME = "rateMonitorWindow";
    public static final Long EMPTY_LONG_CONSTRAINT = 0L;
    public static final Integer EMPTY_INTEGER_CONSTRAINT = 0;
    protected Map sessionConstraints;

    public final static String WINDOW_INTERVAL                  = "rateMonitorInterval";
    public final static String WINDOW_SIZE                      = "rateMonitorWindow";
    protected Map userMarketQueries;

    public UserMarketDataServiceHomeImpl()
    {
        super();
        userMarketQueries = new HashMap(11);
    }

    protected void initSessionConstraints() throws NoSuchPropertyException
    {
        sessionConstraints = new HashMap(10);

        String sessionProperty = getProperty(RATE_MONITOR_SESSIONS_PROPERTY_NAME);
        String bookDepthCallWindowIntervalProperty = getProperty(BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME);
        String bookDepthCallWindowSizeProperty = getProperty(BOOK_DEPTH_CALL_WINDOW_SIZE_PROPERTY_NAME);

        if(sessionProperty.length() > 0)
        {
            StringTokenizer sessionTokenizer = new StringTokenizer(sessionProperty, TOKENIZER_DELIMITERS, false);
            StringTokenizer bookDepthCallWindowIntervalTokenizer =
                    new StringTokenizer(bookDepthCallWindowIntervalProperty, TOKENIZER_DELIMITERS, false);
            StringTokenizer bookDepthCallWindowSizeTokenizer =
                    new StringTokenizer(bookDepthCallWindowSizeProperty, TOKENIZER_DELIMITERS, false);
            String sessionToken;
            Long bookDepthCallWindowIntervalToken;
            Integer bookDepthCallWindowSizeToken;
            Map settingsMap;
            while(sessionTokenizer.hasMoreTokens())
            {
                sessionToken = sessionTokenizer.nextToken();
                settingsMap = new HashMap(2);
                if(bookDepthCallWindowIntervalTokenizer.hasMoreTokens())
                {
                    bookDepthCallWindowIntervalToken = Long.valueOf(bookDepthCallWindowIntervalTokenizer.nextToken());
                }
                else
                {
                    bookDepthCallWindowIntervalToken = EMPTY_LONG_CONSTRAINT;
                }
                settingsMap.put(BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME, bookDepthCallWindowIntervalToken);

                if(bookDepthCallWindowSizeTokenizer.hasMoreTokens())
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

    public synchronized UserMarketDataService create(BaseSessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating MarketQueryImpl for " + sessionManager);
        }
        UserMarketDataServiceImpl marketQuery = (UserMarketDataServiceImpl)userMarketQueries.get(sessionManager);
        if(marketQuery == null)
        {
            marketQuery = new UserMarketDataServiceImpl(sessionManager, sessionConstraints);
            addToContainer(marketQuery);
            userMarketQueries.put(sessionManager, marketQuery);
        }
        return marketQuery;
    }

    public UserMarketDataService find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public void clientInitialize() throws Exception
    {
         if (Log.isDebugOn())
         {
             Log.debug(this, "SMA Type = " + this.getSmaType());
         }
         initSessionConstraints();
    }

    public synchronized void remove(BaseSessionManager sessionManager)
    {
        userMarketQueries.remove(sessionManager);
    }
    
    public Map getSessionConstraints()
    {
    	return sessionConstraints;
    }

}
