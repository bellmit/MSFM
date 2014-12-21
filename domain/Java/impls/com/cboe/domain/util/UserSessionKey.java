package com.cboe.domain.util;

import com.cboe.interfaces.domain.session.BaseSessionManager;

public class UserSessionKey extends Object {
    private BaseSessionManager sessionManager;
    private Object key;

    /**
      * Sets the internal fields to the passed values
      */
    public UserSessionKey(BaseSessionManager sessionManager, Object key) {
	this.sessionManager = sessionManager;
	this.key = key;
    }
    public BaseSessionManager getSessionManager()
    {
        return sessionManager;
    }

    public Object getKey()
    {
        return key;
    }

    public int hashCode()
    {
        return key.hashCode();
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof UserSessionKey))
        {
            BaseSessionManager sessionManager = ((UserSessionKey)obj).getSessionManager();
            Object key = ((UserSessionKey)obj).getKey();
            return (this.sessionManager.equals(sessionManager) && this.key.equals(key));
        }
        return false;
    }
}
