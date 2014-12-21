package com.cboe.presentation.api;

import java.util.Hashtable;

public class SessionProductCacheFactory
{
    private static Hashtable sessions;

    public SessionProductCacheFactory() {
        super();
    }

    private static Hashtable getSessions() {
        if (sessions == null) {
            sessions = new Hashtable();
        }
        return sessions;
    }

    public static SessionProductCache find(String sessionName) {
        SessionProductCache sessionProductCache = (SessionProductCache)getSessions().get(sessionName);
        if (sessionProductCache == null) {
            sessionProductCache = new SessionProductCache();
            getSessions().put(sessionName, sessionProductCache);
        };
        return sessionProductCache;
    }
}
