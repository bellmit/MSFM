package com.cboe.remoteApplication.shared;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

public class RemoteMarketDataSessionNameHelper
{
    public static final String LOCAL_FILTER_ONLY_SESSIONS = "LocalFilterOnlySessions";
    
    private static Set localFilterOnlySessions;
    
    public static void init()
    {
        localFilterOnlySessions = new HashSet();
        
        String string = System.getProperty(LOCAL_FILTER_ONLY_SESSIONS);

        if ((string != null) && (string.length() > 0))
        {
            StringTokenizer t = new StringTokenizer(string, ",");

            while (t.hasMoreTokens())
            {
                String s = t.nextToken();
                localFilterOnlySessions.add(s);
            }
        }
        
        if(Log.isDebugOn())
        {
            Log.debug("RemoteMarketDataSessionNameHelper: Got " + localFilterOnlySessions.size() + " session names configured for this MD CAS.");
        }
        
    }

    public static String[] getLocalFilterOnlySessions()
    {
        String[] sessions = new String[localFilterOnlySessions.size()];
        localFilterOnlySessions.toArray(sessions);
        return sessions;
    }
}
