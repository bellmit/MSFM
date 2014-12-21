package com.cboe.domain.logout;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.util.HashMap;

public class LogoutQueueFactory
{
    private static HashMap logoutQueueForUsers = new HashMap();
    private final static String TIMEOUT = "LogoutQueue.Timeout";
    private static long timeout;

    public LogoutQueueFactory()
    {
        super();
        timeout = 0;  // default value in case of error
        String s = "";
        try
        {
            s = System.getProperty(TIMEOUT);
            if (s == null)
            {
                Log.notification("Missing systemProperty:" + TIMEOUT);
            }
            else
            {
                timeout = Long.parseLong(s);
            }
        }
        catch (NumberFormatException se)
        {
            Log.notification("Invalid value:" + s + " for property " + TIMEOUT);
        }
        catch (Exception e)
        {
            // ignore other exceptions
        }
    }

    public synchronized static LogoutQueue create(String userId)
    {
        LogoutQueue logoutQueue = (LogoutQueue)logoutQueueForUsers.get(userId);
        if ( logoutQueue == null )
        {
            logoutQueue = new LogoutQueue(timeout);
            logoutQueueForUsers.put(userId,logoutQueue);
        }
        return logoutQueue;
    }

    public static LogoutQueue find(String userId)
    {
        return create(userId);
    }
}
