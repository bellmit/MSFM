package com.cboe.domain.util;

import java.util.*;

import com.cboe.exceptions.*;


public abstract class CacheClassTrackFactory {

    private static Hashtable classKeyCache = null;

    public CacheClassTrackFactory()
    {
        super();
    }

    private static Map getClassCache()
    {
        if (classKeyCache == null)
        {
            classKeyCache = new Hashtable();
        }

        return classKeyCache;
    }

    public static CacheClassTrackImpl create(SessionKeyContainer sessionKey)
        throws SystemException
    {
        return find(sessionKey);
    }

    public static synchronized CacheClassTrackImpl find(SessionKeyContainer sessionKey)
        throws SystemException
    {
        CacheClassTrackImpl track = (CacheClassTrackImpl)getClassCache().get(sessionKey);

        if (track == null)
        {
            track = new CacheClassTrackImpl(sessionKey);
            getClassCache().put(sessionKey, track);
        }

        return track;
    }

    public static synchronized void remove(SessionKeyContainer sessionKey)
        throws SystemException
    {
        CacheClassTrackImpl track = (CacheClassTrackImpl)getClassCache().get(sessionKey);
        if (track != null)
        {
            track.allCacheCleanUp();
            getClassCache().remove(sessionKey);
        }
    }
}