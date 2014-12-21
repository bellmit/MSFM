package com.cboe.domain.util;

import com.cboe.exceptions.*;

public class CacheClassTrackImpl
{
    private int classKey                        = 0;
    private String sessionName;

    private boolean sessionProductsLoaded        = false;
    private boolean sessionLessProductsLoaded    = false;
    private boolean sessionLessStrategiesLoaded   = false;
    private boolean sessionStrategiesLoaded       = false;

    public CacheClassTrackImpl(SessionKeyContainer sessionKey)
    {
        this.classKey = sessionKey.getKey();
        this.sessionName = sessionKey.getSessionName();
    }

    public synchronized boolean wasSessionProductsLoaded()
    {
        return sessionProductsLoaded;
    }

    public synchronized void setSessionProductsLoaded(boolean flag)
        throws SystemException
    {
        sessionProductsLoaded = flag;
    }

    public synchronized boolean wasProductsLoaded()
    {
        return sessionLessProductsLoaded;
    }

    public synchronized void setSessionLessProductsLoaded(boolean flag)
    {
        sessionLessProductsLoaded = flag;
    }

    public synchronized boolean wasSessionStrategiesLoaded()
    {
        return sessionStrategiesLoaded;
    }

    public synchronized void setSessionStrategiesLoaded(boolean flag)
        throws SystemException
    {
        sessionStrategiesLoaded = flag;
    }

    public synchronized boolean wasStrategiesLoaded()
    {
        return sessionLessStrategiesLoaded;
    }

    public synchronized void setSessionLessStrategiesLoaded(boolean flag)
    {
        sessionLessStrategiesLoaded = flag;
    }

    public synchronized void sessionCachecleanUp()
    {
        sessionProductsLoaded        = false;
        sessionStrategiesLoaded       = false;
    }

    public synchronized void allCacheCleanUp()
    {
        sessionProductsLoaded        = false;
        sessionLessProductsLoaded    = false;
        sessionLessStrategiesLoaded   = false;
        sessionStrategiesLoaded       = false;
    }
}
