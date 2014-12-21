package com.cboe.interfaces.application;


/**
 *
 * @author Mike Pyatesky
 *
 */
public interface ForcedLogoutCollector
{
    public void acceptForcedLogout( int sessionKey, String userId);
}
