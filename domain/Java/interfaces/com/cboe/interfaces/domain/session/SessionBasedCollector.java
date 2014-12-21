package com.cboe.interfaces.domain.session;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * This is session based collector.  All collectors in cas that are created per session should extend this.
 * @author Jing Chen
 */
public interface SessionBasedCollector
{
    public BaseSessionManager getSessionManager();
}
