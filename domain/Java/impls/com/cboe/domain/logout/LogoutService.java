package com.cboe.domain.logout;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.LogoutMonitor;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Jing Chen
 */
public class LogoutService
{
    HashMap logoutListenersBySource;

    public LogoutService()
    {
        super();
        logoutListenersBySource = new HashMap();
    }

    private LinkedList getListenersForSource(Object source)
    {
        LinkedList listeners = (LinkedList)logoutListenersBySource.get(source);
        if (listeners == null)
        {
            listeners = new LinkedList();
            logoutListenersBySource.put(source, listeners);
        }
        return listeners;
    }

    public synchronized void addLogoutListener(Object source, Object listener)
    {
        LinkedList listeners = getListenersForSource(source);
        if (!listeners.contains(listener))
        {
            LinkedList clonedNewList = (LinkedList)listeners.clone();
            clonedNewList.add(listener);
            logoutListenersBySource.put(source, clonedNewList);
        }
    }

    private synchronized void logoutCleanup(Object source)
    {
        Log.debug("LogoutService->logoutCleanup: " + source);
        if ( source instanceof LogoutMonitor )
        {
            ((LogoutMonitor)source).logoutCleanup();
        }
    }

    public synchronized void logoutComplete(Object source, Object listener)
    {
        LinkedList listeners = getListenersForSource(source);
        listeners.remove(listener);
        if (listeners.size() == 0)
        {
            logoutCleanup(source);
            logoutListenersBySource.remove(source);
        }
    }
}
