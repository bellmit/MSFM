package com.cboe.cfix.fix.session;

/**
 * FixSessionManagerLocator.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.client.util.collections.*;

public final class FixSessionManagerLocator
{
    protected static final Map fixSessionManagerMap = new HashMap();

    protected static final IntStringMap engineNamesMap = new IntStringMap(4);
    protected static       List         engineNames    = new ArrayList(4);

    public static void setEngineNameForPort(int port, String engineName)
    {
        engineNamesMap.putKeyValue(port, engineName);

        if (!engineNames.contains(engineName))
        {
            engineNames.add(engineName);
        }
    }

    public static String getEngineNameForPort(int port)
    {
        return engineNamesMap.getValueForKey(port);
    }

    public static String[] getEngineNames()
    {
        return (String[]) engineNames.toArray(new String[engineNames.size()]);
    }

    public static FixSessionManagerIF createFixSessionManager(String engineName, String className) throws Exception
    {
        FixSessionManagerIF fixSessionManager = (FixSessionManagerIF) ClassHelper.loadClassWithExceptions(className);

        fixSessionManager.setEngineName(engineName);

        synchronized(fixSessionManagerMap)
        {
            fixSessionManagerMap.put(engineName, fixSessionManager);
        }

        return fixSessionManager;
    }

    public static FixSessionManagerIF destroyFixSessionManager(FixSessionManagerIF fixSessionManager)
    {
        synchronized(fixSessionManagerMap)
        {
            fixSessionManagerMap.remove(fixSessionManager.getEngineName());
        }

        return fixSessionManager;
    }

    public static FixSessionManagerIF getFixSessionManager(String engineName)
    {
        return (FixSessionManagerIF) fixSessionManagerMap.get(engineName);
    }

    public static FixSessionIF getFixSessionByName(String sessionName)
    {
        FixSessionIF fixSession = null;

        synchronized(fixSessionManagerMap)
        {
            for (Iterator iterator = fixSessionManagerMap.values().iterator(); fixSession == null && iterator.hasNext(); )
            {
                fixSession = ((FixSessionManagerIF) iterator.next()).getFixSessionByName(sessionName);
            }
        }

        return fixSession;
    }

    public static FixSessionIF getFixSessionByName(String engineName, String sessionName)
    {
        if (engineName == null)
        {
            return getFixSessionByName(sessionName);
        }

        FixSessionManagerIF fixSessionManager = (FixSessionManagerIF) fixSessionManagerMap.get(engineName);

        if (fixSessionManager == null)
        {
            return null;
        }

        return fixSessionManager.getFixSessionByName(sessionName);
    }

    public static FixSessionInformationIF getFixSessionInformationByName(String sessionName)
    {
        FixSessionInformationIF fixSessionInformation = null;

        synchronized(fixSessionManagerMap)
        {
            for (Iterator iterator = fixSessionManagerMap.values().iterator(); fixSessionInformation == null && iterator.hasNext(); )
            {
                fixSessionInformation = ((FixSessionManagerIF) iterator.next()).getFixSessionInformationByName(sessionName);
            }
        }

        return fixSessionInformation;
    }

    public static FixSessionInformationIF getFixSessionInformationByName(String engineName, String sessionName)
    {
        if (engineName == null)
        {
            return getFixSessionInformationByName(sessionName);
        }

        FixSessionManagerIF fixSessionManager = (FixSessionManagerIF) fixSessionManagerMap.get(engineName);

        if (fixSessionManager == null)
        {
            return null;
        }

        return fixSessionManager.getFixSessionInformationByName(sessionName);
    }

    public static int getFixSessionCount()
    {
        int size = 0;

        synchronized(fixSessionManagerMap)
        {
            for (Iterator iterator = fixSessionManagerMap.values().iterator(); iterator.hasNext(); )
            {
                size += ((FixSessionManagerIF) iterator.next()).size();
            }
        }

        return size;
    }

    public static List copyFixSessionList(List list)
    {
        FixSessionManagerIF fixSessionManager = null;

        synchronized(fixSessionManagerMap)
        {
            for (Iterator iterator = fixSessionManagerMap.values().iterator(); iterator.hasNext(); )
            {
                fixSessionManager = ((FixSessionManagerIF) iterator.next());
                fixSessionManager.copyFixSessionList(list);
            }
        }

        return list;
    }

    public static List copyFixSessionManagerList(List list)
    {
        synchronized(fixSessionManagerMap)
        {
            list.addAll(fixSessionManagerMap.values());
        }

        return list;
    }
}